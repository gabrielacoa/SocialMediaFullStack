package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.audit.Auditable;
import com.socialmediaapp.backend.dto.request.auth.ChangePasswordRequest;
import com.socialmediaapp.backend.dto.request.auth.LoginRequest;
import com.socialmediaapp.backend.dto.request.auth.RegisterRequest;
import com.socialmediaapp.backend.dto.request.auth.TwoFactorLoginRequest;
import com.socialmediaapp.backend.dto.request.auth.TwoFactorSetupRequest;
import com.socialmediaapp.backend.dto.response.AuthResponse;
import com.socialmediaapp.backend.dto.response.TwoFactorSetupResponse;
import com.socialmediaapp.backend.dto.response.UserDto;
import com.socialmediaapp.backend.exception.custom.AccountLockedException;
import com.socialmediaapp.backend.exception.custom.BadRequestException;
import com.socialmediaapp.backend.exception.custom.DuplicateResourceException;
import com.socialmediaapp.backend.exception.custom.UnauthorizedException;
import com.socialmediaapp.backend.mapper.UserMapper;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.security.service.JwtService;
import com.socialmediaapp.backend.security.service.LoginAttemptService;
import com.socialmediaapp.backend.security.service.TwoFactorAuthService;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y registro de usuarios.
 * Separado del UserController siguiendo Single Responsibility Principle.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de sesiones")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    /**
     * Registra un nuevo usuario.
     */
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una cuenta de usuario y devuelve un token JWT para autenticación inmediata"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
        @ApiResponse(responseCode = "409", description = "Email o username ya registrado")
    })
    @PostMapping("/register")
    @Auditable(action = "USER_REGISTER", description = "Nuevo registro de usuario")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Usuario", "email", request.getEmail());
        }

        // Verificar si el username ya existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Usuario", "username", request.getUsername());
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Generar token
        String token = jwtService.generateToken(savedUser.getUsername());

        // Crear respuesta
        UserDto userDto = userMapper.toDto(savedUser);
        AuthResponse response = new AuthResponse(token, userDto, "Usuario registrado exitosamente");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Inicia sesión de usuario.
     */
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario con email/username y contraseña, devolviendo un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "423", description = "Cuenta bloqueada por demasiados intentos")
    })
    @PostMapping("/login")
    @Auditable(action = "USER_LOGIN", description = "Intento de inicio de sesion")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String clientIP = getClientIP(httpRequest);
        String attemptKey = clientIP + ":" + request.getEmailOrUsername();

        // Verificar si la cuenta está bloqueada
        if (loginAttemptService.isBlocked(attemptKey)) {
            long remaining = loginAttemptService.getRemainingBlockTime(attemptKey);
            throw new AccountLockedException(remaining);
        }

        // Buscar usuario por email o username
        User user = userRepository.findByEmail(request.getEmailOrUsername())
                .orElseGet(() -> userRepository.findByUsername(request.getEmailOrUsername())
                        .orElse(null));

        // Verificar credenciales
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(attemptKey);
            int remaining = loginAttemptService.getRemainingAttempts(attemptKey);
            throw new UnauthorizedException(
                String.format("Credenciales inválidas. Intentos restantes: %d", remaining));
        }

        // Login exitoso - limpiar intentos fallidos
        loginAttemptService.loginSucceeded(attemptKey);

        // Verificar si el usuario tiene 2FA habilitado
        if (user.isTwoFactorEnabled()) {
            // Generar token temporal para completar 2FA
            String tempToken = jwtService.generateTempToken(user.getUsername());
            return ResponseEntity.ok(AuthResponse.requiresTwoFactor(tempToken));
        }

        // Generar token
        String token = jwtService.generateToken(user.getUsername());

        // Crear respuesta
        UserDto userDto = userMapper.toDto(user);
        AuthResponse response = new AuthResponse(token, userDto);

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene la IP real del cliente considerando proxies.
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    /**
     * Cambia la contraseña del usuario.
     */
    @Operation(
        summary = "Cambiar contraseña",
        description = "Permite al usuario autenticado cambiar su contraseña verificando la contraseña actual",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Las contraseñas no coinciden"),
        @ApiResponse(responseCode = "401", description = "Contraseña actual incorrecta")
    })
    @PutMapping("/change-password")
    @Auditable(action = "PASSWORD_CHANGE", description = "Cambio de contrasena")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader("userId") Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        // Verificar que las nuevas contraseñas coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    /**
     * Obtiene el usuario autenticado actual.
     */
    @Operation(
        summary = "Obtener usuario actual",
        description = "Obtiene los datos del usuario autenticado mediante el token JWT",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token no proporcionado");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Valida un token JWT.
     */
    @Operation(
        summary = "Validar token JWT",
        description = "Verifica si un token JWT es válido y devuelve los datos del usuario asociado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @GetMapping("/validate")
    public ResponseEntity<UserDto> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token no proporcionado");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        if (!jwtService.validateToken(token, username)) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Cierra sesión (invalida token del lado del cliente).
     * El cliente debe eliminar el token almacenado.
     */
    @Operation(
        summary = "Cerrar sesión",
        description = "Finaliza la sesión del usuario. El cliente debe eliminar el token JWT almacenado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // En implementación con refresh tokens, aquí se invalidaría el token en Redis/BD
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

    // ==================== ENDPOINTS 2FA ====================

    /**
     * Genera los datos necesarios para configurar 2FA.
     */
    @Operation(
        summary = "Iniciar configuracion 2FA",
        description = "Genera el codigo QR y secreto para configurar 2FA con Google Authenticator",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos de configuracion generados"),
        @ApiResponse(responseCode = "400", description = "2FA ya esta habilitado")
    })
    @GetMapping("/2fa/setup")
    @Auditable(action = "2FA_SETUP_INIT", description = "Inicio de configuracion 2FA")
    public ResponseEntity<TwoFactorSetupResponse> setup2FA(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);

        if (user.isTwoFactorEnabled()) {
            throw new BadRequestException("2FA ya esta habilitado para esta cuenta");
        }

        String secret = twoFactorAuthService.generateSecret();
        String qrCode = twoFactorAuthService.generateQrCodeDataUri(user.getUsername(), secret);
        String manualKey = twoFactorAuthService.generateManualEntryKey(user.getUsername(), secret);

        return ResponseEntity.ok(new TwoFactorSetupResponse(secret, qrCode, manualKey));
    }

    /**
     * Activa 2FA verificando el codigo generado por la app.
     */
    @Operation(
        summary = "Activar 2FA",
        description = "Activa 2FA despues de verificar el codigo de la app autenticadora",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "2FA activado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Codigo invalido")
    })
    @PostMapping("/2fa/enable")
    @Auditable(action = "2FA_ENABLED", description = "Activacion de 2FA")
    public ResponseEntity<String> enable2FA(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String secret,
            @Valid @RequestBody TwoFactorSetupRequest request) {

        User user = getUserFromToken(authHeader);

        if (user.isTwoFactorEnabled()) {
            throw new BadRequestException("2FA ya esta habilitado");
        }

        // Verificar que el codigo sea valido
        if (!twoFactorAuthService.verifyCode(secret, request.getCode())) {
            throw new BadRequestException("Codigo invalido. Asegurate de escanear el QR correctamente");
        }

        // Guardar el secreto y activar 2FA
        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok("2FA activado exitosamente");
    }

    /**
     * Completa el login verificando el codigo 2FA.
     */
    @Operation(
        summary = "Verificar codigo 2FA",
        description = "Completa el proceso de login verificando el codigo 2FA"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login completado",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Codigo 2FA invalido")
    })
    @PostMapping("/2fa/verify")
    @Auditable(action = "2FA_VERIFY", description = "Verificacion de codigo 2FA")
    public ResponseEntity<AuthResponse> verify2FA(@Valid @RequestBody TwoFactorLoginRequest request) {
        // Extraer username del token temporal
        String username;
        try {
            username = jwtService.extractUsername(request.getTempToken());
            if (!jwtService.validateTempToken(request.getTempToken(), username)) {
                throw new UnauthorizedException("Token temporal invalido o expirado");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Token temporal invalido o expirado");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        // Verificar codigo 2FA
        if (!twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            throw new UnauthorizedException("Codigo 2FA invalido");
        }

        // Generar token completo
        String token = jwtService.generateToken(user.getUsername());

        UserDto userDto = userMapper.toDto(user);
        AuthResponse response = new AuthResponse(token, userDto, "Login completado con 2FA");

        return ResponseEntity.ok(response);
    }

    /**
     * Desactiva 2FA para el usuario.
     */
    @Operation(
        summary = "Desactivar 2FA",
        description = "Desactiva 2FA verificando el codigo actual",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "2FA desactivado"),
        @ApiResponse(responseCode = "400", description = "Codigo invalido o 2FA no esta habilitado")
    })
    @PostMapping("/2fa/disable")
    @Auditable(action = "2FA_DISABLED", description = "Desactivacion de 2FA")
    public ResponseEntity<String> disable2FA(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody TwoFactorSetupRequest request) {

        User user = getUserFromToken(authHeader);

        if (!user.isTwoFactorEnabled()) {
            throw new BadRequestException("2FA no esta habilitado");
        }

        // Verificar codigo antes de desactivar
        if (!twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            throw new UnauthorizedException("Codigo 2FA invalido");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        return ResponseEntity.ok("2FA desactivado exitosamente");
    }

    /**
     * Verifica el estado de 2FA del usuario.
     */
    @Operation(
        summary = "Estado de 2FA",
        description = "Verifica si el usuario tiene 2FA habilitado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/2fa/status")
    public ResponseEntity<Boolean> get2FAStatus(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        return ResponseEntity.ok(user.isTwoFactorEnabled());
    }

    /**
     * Metodo auxiliar para obtener usuario desde token.
     */
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token no proporcionado");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
    }
}

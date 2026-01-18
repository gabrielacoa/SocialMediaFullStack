package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.auth.ChangePasswordRequest;
import com.socialmediaapp.backend.dto.request.auth.LoginRequest;
import com.socialmediaapp.backend.dto.request.auth.RegisterRequest;
import com.socialmediaapp.backend.dto.response.AuthResponse;
import com.socialmediaapp.backend.dto.response.UserDto;
import com.socialmediaapp.backend.exception.custom.BadRequestException;
import com.socialmediaapp.backend.exception.custom.DuplicateResourceException;
import com.socialmediaapp.backend.exception.custom.UnauthorizedException;
import com.socialmediaapp.backend.mapper.UserMapper;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.security.service.JwtService;
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
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Buscar usuario por email o username
        User user = userRepository.findByEmail(request.getEmailOrUsername())
                .orElseGet(() -> userRepository.findByUsername(request.getEmailOrUsername())
                        .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas")));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Generar token
        String token = jwtService.generateToken(user.getUsername());

        // Crear respuesta
        UserDto userDto = userMapper.toDto(user);
        AuthResponse response = new AuthResponse(token, userDto);

        return ResponseEntity.ok(response);
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
}

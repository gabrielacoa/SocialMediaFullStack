package com.socialmediaapp.backend.security.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

/**
 * Servicio para manejar autenticacion de dos factores (2FA) usando TOTP.
 * Compatible con Google Authenticator, Authy, y otras apps TOTP.
 */
@Service
public class TwoFactorAuthService {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthService.class);

    @Value("${app.name:SocialMediaApp}")
    private String appName;

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final CodeVerifier codeVerifier;

    public TwoFactorAuthService() {
        this.secretGenerator = new DefaultSecretGenerator();
        this.qrGenerator = new ZxingPngQrGenerator();

        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    /**
     * Genera un nuevo secreto para 2FA.
     * @return El secreto en formato Base32
     */
    public String generateSecret() {
        return secretGenerator.generate();
    }

    /**
     * Genera el QR code como Data URI para mostrar al usuario.
     * @param username Nombre del usuario
     * @param secret Secreto generado
     * @return Data URI de la imagen PNG del QR code
     */
    public String generateQrCodeDataUri(String username, String secret) {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(appName)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] imageData = qrGenerator.generate(data);
            return getDataUriForImage(imageData, qrGenerator.getImageMimeType());
        } catch (QrGenerationException e) {
            logger.error("Error generando QR code para usuario: {}", username, e);
            throw new RuntimeException("Error generando QR code", e);
        }
    }

    /**
     * Genera la URL para configuracion manual (sin QR).
     * @param username Nombre del usuario
     * @param secret Secreto generado
     * @return URL otpauth:// para configuracion manual
     */
    public String generateManualEntryKey(String username, String secret) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                appName, username, secret, appName);
    }

    /**
     * Verifica si el codigo TOTP proporcionado es valido.
     * @param secret Secreto del usuario
     * @param code Codigo de 6 digitos ingresado por el usuario
     * @return true si el codigo es valido
     */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || code.length() != 6) {
            return false;
        }

        try {
            return codeVerifier.isValidCode(secret, code);
        } catch (Exception e) {
            logger.warn("Error verificando codigo TOTP: {}", e.getMessage());
            return false;
        }
    }
}

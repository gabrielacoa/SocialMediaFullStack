package com.socialmediaapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response con los datos necesarios para configurar 2FA.
 */
@Data
@AllArgsConstructor
public class TwoFactorSetupResponse {

    /**
     * Secreto temporal (se guarda solo al confirmar con codigo valido).
     */
    private String secret;

    /**
     * QR code como Data URI (base64) para escanear con la app.
     */
    private String qrCodeDataUri;

    /**
     * URL para configuracion manual si no puede escanear QR.
     */
    private String manualEntryKey;
}

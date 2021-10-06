package it.auties.whatsapp4j.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.NonNull;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

/**
 * A utility class used to generate QR codes to authenticate with whatsapp
 */
public class WhatsappQRCode {
    private String ref;
    private static final int SIZE = 50;
    private static final MultiFormatWriter WRITER = new MultiFormatWriter();

    /**
     * Generates a QR code to initialize the connection with WhatsappWeb's WebSocket
     *
     * @param ref       the ref to generate the qr code, might be null
     * @param publicKey the non-null raw publicKey
     * @param clientId  the non-null client id
     * @return a non-null {@link BitMatrix}
     */
    public @NonNull BitMatrix generate(String ref, byte @NonNull [] publicKey, @NonNull String clientId) {
        this.ref = Objects.requireNonNullElse(ref, this.ref);
        var qr = "%s,%s,%s".formatted(this.ref, Base64.getEncoder().encodeToString(publicKey), clientId);
        return generate(qr);
    }

    /**
     * Generates a QR code to initialize the connection with WhatsappWeb's WebSocket
     *
     * @param ref         the ref to generate the qr code, might be null
     * @param publicKey   the non-null raw publicKey
     * @param identityKey the non-null identity key
     * @param secretKey   the non-null secret key
     * @return a non-null {@link BitMatrix}
     */
    public @NonNull BitMatrix generate(String ref, byte @NonNull [] publicKey, byte @NonNull [] identityKey, @NonNull String secretKey) {
        this.ref = Objects.requireNonNullElse(ref, this.ref);
        var qr = "%s,%s,%s, %s".formatted(this.ref, Base64.getEncoder().encodeToString(publicKey), Base64.getEncoder().encodeToString(identityKey), secretKey);
        return generate(qr);
    }

    private BitMatrix generate(String qr) {
        try {
            return WRITER.encode(qr, BarcodeFormat.QR_CODE, SIZE, SIZE);
        } catch (WriterException ex) {
            throw new RuntimeException("WhatsappAPI: Cannot generate a QR code", ex);
        }
    }
}

package it.auties.whatsapp.crypto;

import it.auties.bytes.Bytes;
import it.auties.whatsapp.controller.Keys;
import lombok.NonNull;
import lombok.SneakyThrows;

import static it.auties.bytes.Bytes.of;

public class Handshake {
    public static final byte[] PROLOGUE = new byte[]{87, 65, 5, 2};
    private static final Bytes PROTOCOL = Bytes.of("Noise_XX_25519_AESGCM_SHA256\0\0\0\0");

    private final Keys keys;
    private Bytes hash;
    private Bytes salt;
    private Bytes cryptoKey;
    private long counter;

    public Handshake(Keys keys) {
        this.keys = keys;
        this.hash = PROTOCOL;
        this.salt = PROTOCOL;
        this.cryptoKey = PROTOCOL;
        this.counter = 0;
        updateHash(PROLOGUE);
    }

    @SneakyThrows
    public void updateHash(byte @NonNull [] data) {
        var input = hash.append(data);
        this.hash = of(Sha256.calculate(input.toByteArray()));
    }

    @SneakyThrows
    public byte[] cipher(byte @NonNull [] bytes, boolean encrypt) {
        var cyphered = AesGmc.cipher(counter++, bytes, cryptoKey.toByteArray(), hash.toByteArray(), encrypt);
        if (!encrypt) {
            updateHash(bytes);
            return cyphered;
        }

        updateHash(cyphered);
        return cyphered;
    }

    public void finish() {
        var expanded = Bytes.of(Hkdf.extractAndExpand(new byte[0], salt.toByteArray(), null, 64));
        keys.writeKey(expanded.cut(32));
        keys.readKey(expanded.slice(32));
    }

    public void mixIntoKey(byte @NonNull [] bytes) {
        var expanded = Bytes.of(Hkdf.extractAndExpand(bytes, salt.toByteArray(), null, 64));
        this.salt = expanded.cut(32);
        this.cryptoKey = expanded.slice(32);
        this.counter = 0;
    }
}
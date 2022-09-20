package it.auties.whatsapp.model.sync;

import it.auties.protobuf.api.model.ProtobufMessage;
import it.auties.protobuf.api.model.ProtobufProperty;
import it.auties.whatsapp.model.media.AttachmentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import static it.auties.protobuf.api.model.ProtobufProperty.Type.*;

@AllArgsConstructor
@Data
@Builder
@Jacksonized
@Accessors(fluent = true)
public final class ExternalBlobReference implements ProtobufMessage, AttachmentProvider {
    @ProtobufProperty(index = 1, type = BYTES)
    private byte[] mediaKey;

    @ProtobufProperty(index = 2, type = STRING)
    private String mediaDirectPath;

    @ProtobufProperty(index = 3, type = STRING)
    private String handle;

    @ProtobufProperty(index = 4, type = UINT64)
    private long mediaSize;

    @ProtobufProperty(index = 5, type = BYTES)
    private byte[] mediaSha256;

    @ProtobufProperty(index = 6, type = BYTES)
    private byte[] mediaEncryptedSha256;

    @Override
    public String mediaUrl() {
        return null;
    }

    @Override
    public AttachmentProvider mediaUrl(String mediaUrl) {
        return this;
    }

    @Override
    public String mediaName() {
        return "WhatsApp App State Keys";
    }
}

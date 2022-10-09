package it.auties.whatsapp.model.button;

import it.auties.protobuf.base.ProtobufMessage;
import it.auties.protobuf.base.ProtobufProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import static it.auties.protobuf.base.ProtobufType.STRING;

/**
 * A model class that represents the selection of a row
 */
@AllArgsConstructor(staticName = "of")
@Data
@Builder(builderMethodName = "newSingleSelectReplyButtonBuilder")
@Jacksonized
@Accessors(fluent = true)
public class SingleSelectReplyButton implements ProtobufMessage {
    /**
     * The id of the selected row
     */
    @ProtobufProperty(index = 1, type = STRING)
    private String rowId;
}

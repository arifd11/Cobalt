package it.auties.whatsapp.model.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
@Builder
@Accessors(fluent = true)
public final class LabelEditAction implements Action {
  @JsonProperty("1")
  @JsonPropertyDescription("string")
  private String name;

  @JsonProperty("2")
  @JsonPropertyDescription("int32")
  private int color;

  @JsonProperty("3")
  @JsonPropertyDescription("int32")
  private int predefinedId;

  @JsonProperty("4")
  @JsonPropertyDescription("bool")
  private boolean deleted;

  @Override
  public String indexName() {
    return "unknown";
  }
}
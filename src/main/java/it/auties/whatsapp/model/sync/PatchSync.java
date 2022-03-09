package it.auties.whatsapp.model.sync;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
@Builder
@Accessors(fluent = true)
public class PatchSync {
  @JsonProperty("1")
  @JsonPropertyDescription("SyncdVersion")
  private VersionSync version;

  @JsonProperty("2")
  @JsonPropertyDescription("SyncdMutation")
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @Default
  private List<MutationSync> mutations = new ArrayList<>();

  @JsonProperty("3")
  @JsonPropertyDescription("ExternalBlobReference")
  private ExternalBlobReference externalMutations;

  @JsonProperty("4")
  @JsonPropertyDescription("bytes")
  private byte[] snapshotMac;

  @JsonProperty("5")
  @JsonPropertyDescription("bytes")
  private byte[] patchMac;

  @JsonProperty("6")
  @JsonPropertyDescription("KeyId")
  private KeyId keyId;

  @JsonProperty("7")
  @JsonPropertyDescription("ExitCode")
  private ExitCode exitCode;

  @JsonProperty("8")
  @JsonPropertyDescription("uint32")
  private int deviceIndex;

  public boolean hasVersion(){
    return version != null && version.version() != 0;
  }

  public boolean hasExternalMutations(){
    return externalMutations != null;
  }
}
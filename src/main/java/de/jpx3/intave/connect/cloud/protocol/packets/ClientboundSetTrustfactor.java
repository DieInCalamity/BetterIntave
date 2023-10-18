package de.jpx3.intave.connect.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.access.player.trust.TrustFactor;
import de.jpx3.intave.connect.cloud.protocol.Direction;
import de.jpx3.intave.connect.cloud.protocol.Identity;
import de.jpx3.intave.connect.cloud.protocol.JsonPacket;
import de.jpx3.intave.connect.cloud.protocol.listener.Clientbound;
import org.bukkit.entity.Player;

public final class ClientboundSetTrustfactor extends JsonPacket<Clientbound> {
  private Identity id;
  private TrustFactor trustFactor;

  public ClientboundSetTrustfactor() {
    super(Direction.CLIENTBOUND, "SET_TRUSTFACTOR", "1");
  }

  public ClientboundSetTrustfactor(Player player, TrustFactor trustFactor) {
    super(Direction.CLIENTBOUND, "SET_TRUSTFACTOR", "1");
    this.id = Identity.from(player);
    this.trustFactor = trustFactor;
  }

  @Override
  public void serialize(JsonWriter jsonWriter) {
    try {
      jsonWriter.beginObject();
      jsonWriter.name("id");
      id.serialize(jsonWriter);
      jsonWriter.name("factor").value(trustFactor.name());
      jsonWriter.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deserialize(JsonReader jsonReader) {
    try {
      jsonReader.beginObject();
      while (jsonReader.hasNext()) {
        switch (jsonReader.nextName()) {
          case "id":
            id = Identity.from(jsonReader);
            break;
          case "factor":
            trustFactor = TrustFactor.valueOf(jsonReader.nextString());
            break;
        }
      }
      jsonReader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Identity id() {
    return id;
  }

  public TrustFactor trustFactor() {
    return trustFactor;
  }
}

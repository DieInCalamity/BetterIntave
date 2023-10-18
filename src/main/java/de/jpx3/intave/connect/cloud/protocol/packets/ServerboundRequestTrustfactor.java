package de.jpx3.intave.connect.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;
import de.jpx3.intave.connect.cloud.protocol.Identity;
import de.jpx3.intave.connect.cloud.protocol.JsonPacket;

import static de.jpx3.intave.connect.cloud.protocol.Direction.SERVERBOUND;

public final class ServerboundRequestTrustfactor extends JsonPacket<Serverbound> {
  private Identity id;

  public ServerboundRequestTrustfactor() {
    super(SERVERBOUND, "REQUEST_TRUSTFACTOR", "1");
  }

  public ServerboundRequestTrustfactor(Identity id) {
    super(SERVERBOUND, "REQUEST_TRUSTFACTOR", "1");
    this.id = id;
  }

  @Override
  public void serialize(JsonWriter writer) {
    try {
      writer.beginObject();
      writer.name("id");
      id.serialize(writer);
      writer.endObject();
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
        }
      }
      jsonReader.endObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

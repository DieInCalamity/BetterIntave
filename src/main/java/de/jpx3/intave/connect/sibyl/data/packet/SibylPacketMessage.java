package de.jpx3.intave.connect.sibyl.data.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SibylPacketMessage extends SibylPacket {
  private int debugId;

  public SibylPacketMessage() {
    super("out-message");
  }

  @Override
  public void buildFrom(JsonElement element) {

  }

  @Override
  public JsonElement asJsonElement() {
    JsonObject object = new JsonObject();
    object.addProperty("id", debugId);
    return object;
  }

  public int debugId() {
    return debugId;
  }

  public void setDebugId(int debugId) {
    this.debugId = debugId;
  }
}

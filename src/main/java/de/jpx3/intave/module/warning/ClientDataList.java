package de.jpx3.intave.module.warning;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ClientDataList {
  private static final Resource CACHED_RESOURCE = Resources.cacheResourceChain("https://service.intave.de/clientdata", "clientdata", TimeUnit.DAYS.toMillis(14));
  private final List<ClientData> content;

  public ClientDataList(List<ClientData> content) {
    this.content = content;
  }

  public List<ClientData> content() {
    return content;
  }

  public static ClientDataList generate() {
    if (!CACHED_RESOURCE.available()) {
      return new ClientDataList(new ArrayList<>());
    }
    return new ClientDataList(parseClientData(CACHED_RESOURCE.asString()));
  }

  private static List<ClientData> parseClientData(String rawJson) {
    try {
      List<ClientData> content = new ArrayList<>();
      JsonReader jsonReader = new JsonReader(new StringReader(rawJson));
      jsonReader.setLenient(true);
      JsonArray jsonArray = new JsonParser().parse(jsonReader).getAsJsonArray();
      for (JsonElement jsonElement : jsonArray) {
        content.add(ClientData.parseFrom(jsonElement));
      }
      return content;
    } catch (Exception exception) {
      return Collections.emptyList();
    }
  }
}

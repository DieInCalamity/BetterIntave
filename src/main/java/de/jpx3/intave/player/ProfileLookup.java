package de.jpx3.intave.player;

import com.google.common.collect.Maps;
import de.jpx3.intave.cleanup.GarbageCollector;
import de.jpx3.intave.executor.BackgroundExecutor;
import de.jpx3.intave.resource.WebResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class ProfileLookup {
  private static final String NAME_TO_ID_LOOKUP_URL =
    "https://api.mojang.com/users/profiles/minecraft/%s";
  private static final Map<String, UUID> requestCache =
    GarbageCollector.watch(Maps.newConcurrentMap());

  public static void lookupIdFromName(String name, Consumer<UUID> lazyReturn) {
    if (requestCache.containsKey(name)) {
      lazyReturn.accept(requestCache.get(name));
      return;
    }
    BackgroundExecutor.execute(() ->
      lazyReturn.accept(
        requestCache.computeIfAbsent(name, ProfileLookup::loadIfFromName)
      )
    );
  }

  private static UUID loadIfFromName(String name) {
    try {
      String url = String.format(NAME_TO_ID_LOOKUP_URL, name);
      WebResource webResource = new WebResource(url);
      String profileResponse = webResource.asString();
      if (profileResponse.isEmpty()) {
        return null;
      }
      JSONParser parser = new JSONParser();
      JSONObject uuidObject = (JSONObject) parser.parse(profileResponse);
      return asId(uuidObject.get("id").toString());
    } catch (MalformedURLException | ParseException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  private static UUID asId(String id) {
    return UUID.fromString(
      id.substring(0, 8) + "-" + id.substring(8, 12) + "-" +
        id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +
        id.substring(20, 32)
    );
  }
}

package net.labymod.addons.resourcepacks.core.controller.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * We need this as the resourcepacks24 api returns the resource pack tags in a serialized json
 * array
 */
public class ResourcePackTagAdapter extends TypeAdapter<List<String>> {

  // we need a gson factory...
  private static final Gson GSON = new Gson();

  @Override
  public void write(JsonWriter out, List<String> value) throws IOException {
    out.beginArray();
    for (String s : value) {
      out.value(s);
    }

    out.endArray();
  }

  @Override
  public List<String> read(JsonReader reader) throws IOException {
    String string = reader.nextString();
    JsonElement element = GSON.fromJson(string, JsonElement.class);
    if (!element.isJsonArray()) {
      throw new IOException(
          "Expected tags to be a JsonArray but it is a " + element.getClass().getSimpleName());
    }

    List<String> tags = new ArrayList<>();
    for (JsonElement jsonElement : element.getAsJsonArray()) {
      tags.add(jsonElement.getAsString());
    }

    return tags;
  }
}

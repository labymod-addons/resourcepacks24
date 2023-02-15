/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.labymod.addons.resourcepacks24.core.controller.adapter;

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

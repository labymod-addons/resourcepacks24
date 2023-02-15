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

package net.labymod.addons.resourcepacks24.core.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks24.core.util.callback.CachedCallbackCollection;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.TextFormat;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.io.web.result.ResultCallback;

public class ResourcePackFeed {

  protected final List<ResourcePackPage> pages;
  protected final CachedCallbackCollection<ResultCallback<ResourcePackPage>> callbacks;
  protected final String translationKey;
  private final ResourcePacksController controller;
  private final Type type;
  private final String url;
  protected Component displayName;
  protected int lastPage = -1;

  public ResourcePackFeed(ResourcePacksController controller, Type type, String translationKey,
      String url) {
    this.controller = controller;
    this.type = type;
    this.url = url;
    this.translationKey = translationKey;

    this.pages = new ArrayList<>();
    this.callbacks = CachedCallbackCollection.create();
  }

  public ResourcePackPage getPage(int page) {
    if (page < 1 || (this.lastPage != -1 && page > this.lastPage)) {
      return null;
    }

    for (ResourcePackPage resourcePackPage : this.pages) {
      if (resourcePackPage.getNumber() == page) {
        return resourcePackPage;
      }
    }

    return null;
  }

  public ResourcePackPage getOrLoadPage(int page, ResultCallback<ResourcePackPage> callback) {
    ResourcePackPage cachedPage = this.getPage(page);
    if (cachedPage != null) {
      return cachedPage;
    }

    if (page < 1 || (this.lastPage != -1 && page > this.lastPage)) {
      return null;
    }

    if (this.callbacks.addSingleton(page, callback)) {
      return null;
    }

    List<OnlineResourcePack> resourcePacks = new ArrayList<>();
    this.controller.load(this.url + "?page=" + page, result -> {
          if (result.hasException()) {
            if (page == 1) {
              this.lastPage = 0;
            }

            this.callbacks.fire(page, Result.ofException(result.exception()));
            return null;
          }

          if (!result.isPresent()) {
            this.callbacks.fire(page, Result.empty());
            return null;
          }

          return this.getJsonArray(result.get());
        }, resourcePacks::add, () -> {
          int size = resourcePacks.size();
          if (size == 0) {
            this.lastPage = page - 1;
            this.callbacks.fire(page, Result.empty());
            return;
          }

          ResourcePackPage resourcePackPage = this.toPage(page, size, resourcePacks);
          this.pages.add(resourcePackPage);

          this.callbacks.fire(page, Result.of(resourcePackPage));
        }
    );

    return null;
  }

  protected ResourcePackPage toPage(int page, int size, List<OnlineResourcePack> resourcePacks) {
    ResourcePackPage resourcePackPage = new ResourcePackPage(page, size);
    resourcePackPage.putAll(resourcePacks);
    return resourcePackPage;
  }

  public String getId() {
    return this.type.name();
  }

  public Component displayName() {
    if (this.displayName == null) {
      this.displayName = Component.translatable(
          this.translationKey + "." + TextFormat.SNAKE_CASE.toLowerCamelCase(this.getId())
      );
    }

    return this.displayName;
  }

  public int getLastPage() {
    return this.lastPage;
  }

  public Type type() {
    return this.type;
  }

  private JsonArray getJsonArray(JsonElement jsonElement) {
    if (jsonElement.isJsonArray()) {
      return jsonElement.getAsJsonArray();
    }

    if (!jsonElement.isJsonObject()) {
      return null;
    }

    JsonObject jsonObject = jsonElement.getAsJsonObject();
    if (!jsonObject.has("data")) {
      return null;
    }

    JsonElement data = jsonObject.get("data");
    if (!data.isJsonArray()) {
      return null;
    }

    if (jsonObject.has("meta") && jsonObject.get("meta").isJsonObject()) {
      JsonObject meta = jsonObject.get("meta").getAsJsonObject();
      if (meta.has("last_page") && meta.get("last_page").isJsonPrimitive()) {
        this.lastPage = meta.get("last_page").getAsInt();
      }
    }

    return data.getAsJsonArray();
  }

  public enum Type {
    TRENDING, TOP_OF_THE_WEEK, PROMOTED, NEW, SEARCH, CATEGORY, RANDOM
  }
}

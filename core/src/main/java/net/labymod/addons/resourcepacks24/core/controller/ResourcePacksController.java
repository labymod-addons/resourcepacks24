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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackFeed.Type;
import net.labymod.addons.resourcepacks24.core.controller.adapter.ResourcePackTagAdapter;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks24.core.util.DownloadProcess;
import net.labymod.api.util.gson.UUIDTypeAdapter;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;
import net.labymod.api.util.io.web.request.types.FileRequest;
import net.labymod.api.util.io.web.result.ResultCallback;

public class ResourcePacksController {

  private static final Path RESOURCEPACKS_DIRECTORY = Paths.get("resourcepacks");
  private static final String API_KEY = "6b514bb5-cb55-4f68-8c62-3031cf871a72";
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
      .registerTypeAdapter(
          TypeToken.getParameterized(List.class, String.class).getType(),
          new ResourcePackTagAdapter()
      )
      .create();

  private final ResourcePackFeed trendingFeed;
  private final ResourcePackFeed weekFeed;
  private final ResourcePackFeed promotedFeed;
  private final ResourcePackFeed newFeed;
  private final ResourcePackFeed randomFeed;

  private final List<ResourcePackCategoryFeed> categoryFeeds;
  private final Map<Integer, OnlineResourcePack> resourcePacks;

  private final Map<Integer, DownloadProcess> downloadProcesses;

  public ResourcePacksController() {
    String translationKey = "resourcepacks.browse.feed";
    this.trendingFeed = new ResourcePackFeed(
        this,
        Type.TRENDING,
        translationKey,
        ResourcePackUrls.TRENDING
    );

    this.weekFeed = new ResourcePackFeed(
        this,
        Type.TOP_OF_THE_WEEK,
        translationKey,
        ResourcePackUrls.WEEK
    );

    this.promotedFeed = new ResourcePackFeed(
        this,
        Type.PROMOTED,
        translationKey,
        ResourcePackUrls.PROMOTED
    );

    this.newFeed = new ResourcePackFeed(
        this,
        Type.NEW,
        translationKey,
        ResourcePackUrls.NEW
    );

    this.randomFeed = new ResourcePackUglyFeed(
        this,
        Type.RANDOM,
        translationKey,
        ResourcePackUrls.RANDOM
    );

    String categoryTranslationKey = translationKey + ".category";
    this.downloadProcesses = new HashMap<>();
    this.resourcePacks = new HashMap<>();
    this.categoryFeeds = new ArrayList<>();
    Request.ofGson(JsonArray.class)
        .url(ResourcePackUrls.CATEGORIES)
        .addHeader("RP24-Token", API_KEY)
        .async()
        .execute(response -> {
          if (!response.isPresent()) {
            return;
          }

          for (JsonElement jsonElement : response.get()) {
            String name = jsonElement.getAsString();
            this.categoryFeeds.add(new ResourcePackCategoryFeed(
                this,
                String.format(ResourcePackUrls.CATEGORY, name),
                categoryTranslationKey,
                name
            ));
          }
        });
  }

  public ResourcePackFeed trendingFeed() {
    return this.trendingFeed;
  }

  public ResourcePackFeed topOfTheWeekFeed() {
    return this.weekFeed;
  }

  public ResourcePackFeed promotedFeed() {
    return this.promotedFeed;
  }

  public ResourcePackFeed newFeed() {
    return this.newFeed;
  }

  public ResourcePackFeed randomFeed() {
    return this.randomFeed;
  }

  public List<ResourcePackCategoryFeed> getCategoryFeeds() {
    return this.categoryFeeds;
  }

  public void load(
      String url,
      Function<Response<JsonElement>, JsonArray> arrayProvider,
      Consumer<OnlineResourcePack> packConsumer,
      Runnable finishConsumer
  ) {
    Request.ofGson(JsonElement.class, () -> GSON)
        .url(url)
        .addHeader("RP24-Token", API_KEY)
        .async()
        .execute(response -> {
          // Resourcepacks24 doesn't always return with the same response structure...
          JsonArray array = arrayProvider.apply(response);
          if (array == null) {
            return;
          }

          // check if resource pack has been cached already
          for (JsonElement jsonElement : array) {
            if (!jsonElement.isJsonObject()) {
              continue;
            }

            try {
              JsonObject object = jsonElement.getAsJsonObject();
              if (object.has("rp_id") && object.getAsJsonPrimitive("rp_id").isNumber()) {
                int id = object.getAsJsonPrimitive("rp_id").getAsInt();
                OnlineResourcePack resourcePack = this.resourcePacks.get(id);
                if (resourcePack == null) {
                  resourcePack = GSON.fromJson(object, OnlineResourcePack.class);
                  this.resourcePacks.put(id, resourcePack);
                }

                packConsumer.accept(resourcePack);
              }
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }

          finishConsumer.run();
        });
  }

  public void download(
      int resourcePackId,
      Consumer<Double> percentageCallback,
      ResultCallback<DownloadProcess> callback
  ) {
    Request.ofString()
        .url(ResourcePackUrls.DOWNLOAD, resourcePackId)
        .addHeader("RP24-Token", API_KEY)
        .async()
        .execute(urlResponse -> {
          FileRequest fileRequest = Request.ofFile(RESOURCEPACKS_DIRECTORY, percentageCallback);
          if (!urlResponse.isPresent()) {
            this.downloadProcesses.put(resourcePackId, new DownloadProcess(fileRequest));
            callback.acceptException(
                new IllegalStateException("Failed to download resource pack!")
            );

            return;
          }

          String url = urlResponse.get();
          DownloadProcess downloadProcess = new DownloadProcess(fileRequest.url(url));
          downloadProcess.start();
          this.downloadProcesses.put(resourcePackId, downloadProcess);
          callback.acceptRaw(downloadProcess);
        });
  }

  public DownloadProcess getDownloadProcess(int resourcePackId) {
    return this.downloadProcesses.get(resourcePackId);
  }
}

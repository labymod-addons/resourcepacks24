package net.labymod.addons.resourcepacks.core.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.UUID;
import net.labymod.addons.resourcepacks.core.controller.ResourcePackFeed.Type;
import net.labymod.addons.resourcepacks.core.controller.adapter.ResourcePackTagAdapter;
import net.labymod.addons.resourcepacks.core.controller.models.ResourcePack;
import net.labymod.api.util.gson.UUIDTypeAdapter;
import net.labymod.api.util.io.web.request.Callback;
import net.labymod.api.util.io.web.request.Request;

public class ResourcePacksController {

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

  public ResourcePacksController() {
    this.trendingFeed = new ResourcePackFeed(this, Type.TRENDING, ResourcePackUrls.TRENDING);
    this.weekFeed = new ResourcePackFeed(this, Type.WEEK, ResourcePackUrls.WEEK);
    this.promotedFeed = new ResourcePackFeed(this, Type.PROMOTED, ResourcePackUrls.PROMOTED);
    this.newFeed = new ResourcePackFeed(this, Type.NEW, ResourcePackUrls.NEW);
  }

  public ResourcePackFeed getWeekFeed() {
    return this.weekFeed;
  }

  public void load(
      String url,
      Callback<List<ResourcePack>> callback
  ) {
    Request.ofGsonList(ResourcePack.class, () -> GSON)
        .url(url)
        .addHeader("RP24-Token", API_KEY)
        .execute(callback);
  }
}

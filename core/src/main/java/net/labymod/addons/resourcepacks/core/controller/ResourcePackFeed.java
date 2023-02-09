package net.labymod.addons.resourcepacks.core.controller;

import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.resourcepacks.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks.core.util.callback.CachedCallbackCollection;
import net.labymod.api.util.io.web.result.Result;
import net.labymod.api.util.io.web.result.ResultCallback;

public class ResourcePackFeed {

  private final ResourcePacksController controller;
  private final Type type;
  private final String url;
  private final List<ResourcePackPage> pages;
  private final CachedCallbackCollection<ResultCallback<ResourcePackPage>> callbacks;
  private int lastPage = -1;

  public ResourcePackFeed(ResourcePacksController controller, Type type, String url) {
    this.controller = controller;
    this.type = type;
    this.url = url;
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

    this.controller.load(this.url + "?page=" + page, result -> {
      if (result.hasException()) {
        this.callbacks.fire(page, Result.ofException(result.exception()));
        return;
      }

      if (!result.isPresent()) {
        this.callbacks.fire(page, Result.empty());
        return;
      }

      List<OnlineResourcePack> resourcePacks = result.get();
      int size = resourcePacks.size();
      if (size == 0) {
        this.lastPage = page - 1;
        return;
      }

      ResourcePackPage resourcePackPage = new ResourcePackPage(page, size);
      resourcePackPage.putAll(resourcePacks);
      this.pages.add(resourcePackPage);

      this.callbacks.fire(page, Result.of(resourcePackPage));
    });

    return null;
  }


  public enum Type {
    TRENDING, WEEK, PROMOTED, NEW, CATEGORY
  }
}

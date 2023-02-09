package net.labymod.addons.resourcepacks.core.activity;

import java.util.function.Consumer;
import net.labymod.addons.resourcepacks.core.ResourcePacks;
import net.labymod.addons.resourcepacks.core.controller.ResourcePackFeed;
import net.labymod.addons.resourcepacks.core.controller.ResourcePackPage;
import net.labymod.addons.resourcepacks.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks.core.controller.models.ResourcePack;
import net.labymod.addons.resourcepacks.core.widgets.GridFeedWidget;
import net.labymod.addons.resourcepacks.core.widgets.resourcepacks.OnlineResourcePackWidget;
import net.labymod.api.client.gui.screen.LabyScreen;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.ScreenInstance;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.AbstractLayerActivity;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import org.jetbrains.annotations.Nullable;

@AutoActivity
@Link("resourcepacks.lss")
public class ResourcepacksOverlay extends AbstractLayerActivity {

  private final ResourcePacks resourcePacks;
  private final ResourcePacksController controller;


  private final ResourcePackFeed feed;
  private int page = 1;

  public ResourcepacksOverlay(ScreenInstance parentScreen, ResourcePacks resourcePacks,
      ResourcePacksController controller) {
    super(parentScreen);

    System.out.println("new");
    this.resourcePacks = resourcePacks;
    this.controller = controller;

    this.feed = this.controller.getWeekFeed();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.page = 1;

    DivWidget wrapper = new DivWidget();
    wrapper.addId("wrapper");

    GridFeedWidget feedWidget = new GridFeedWidget(this::load);
    feedWidget.addId("feed");
    feedWidget.doRefresh(false);

    int page = this.page;
    ResourcePackPage resourcePackPage = this.feed.getOrLoadPage(page, result -> {
      if (result.isPresent()) {
        this.load(feedWidget, result.get());
      } else {
        System.out.println("error at page " + page);
      }
    });

    if (resourcePackPage != null) {
      this.load(feedWidget, resourcePackPage);
    }

    wrapper.addChild(new ScrollWidget(feedWidget, feedWidget.getSession()));
    this.document.addChild(wrapper);
  }

  @Override
  public <T extends LabyScreen> @Nullable T renew() {
    return new ResourcepacksOverlay(this.parentScreen, this.resourcePacks,
        this.controller).generic();
  }

  private boolean load(GridFeedWidget feedWidget, Consumer<Widget> consumer) {
    int page = ++this.page;
    ResourcePackPage resourcePackPage = this.feed.getOrLoadPage(page, result -> {
      if (result.isPresent()) {
        this.load(feedWidget, result.get());
      } else {
        System.out.println("error at page " + page);
      }
    });

    if (resourcePackPage != null) {
      this.load(feedWidget, resourcePackPage);
      return true;
    }

    return false;
  }

  private void load(GridFeedWidget feedWidget, ResourcePackPage resourcePackPage) {
    for (ResourcePack resourcePack : resourcePackPage.getResourcePacks()) {
      OnlineResourcePackWidget resourcePackWidget = new OnlineResourcePackWidget(resourcePack);
      resourcePackWidget.addId("resourcepack");

      if (feedWidget.isInitialized()) {
        feedWidget.addChildInitialized(resourcePackWidget);
      } else {
        feedWidget.addChild(resourcePackWidget);
      }
    }

    if (feedWidget.isInitialized()) {
      feedWidget.updateBounds();
    }

    feedWidget.doRefresh(true);
  }
}

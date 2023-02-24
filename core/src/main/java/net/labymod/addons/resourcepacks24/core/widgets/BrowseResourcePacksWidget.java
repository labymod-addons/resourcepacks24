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

package net.labymod.addons.resourcepacks24.core.widgets;

import java.util.function.Consumer;
import net.labymod.addons.resourcepacks24.core.ResourcePacks24;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackFeed;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackFeed.Type;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks24.core.util.DownloadProcess;
import net.labymod.addons.resourcepacks24.core.util.ResourcePackPageResult;
import net.labymod.addons.resourcepacks24.core.widgets.resourcepack.ResourcePackWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;

@AutoWidget
@Link("browse.lss")
@Link("resourcepack.lss")
public class BrowseResourcePacksWidget extends FlexibleContentWidget {

  private final ListSession<ResourcePackWidget> session;
  private final ResourcePacksController controller;
  private final ResourcePacks24 resourcePacks;

  private final ResourcePackSidebarWidget sidebarWidget;
  private final GridFeedWidget<ResourcePackWidget> feedWidget;

  private final BrowseResourcePacksContainerWidget containerWidget;

  private ResourcePackFeed feed;
  private int page = 1;

  public BrowseResourcePacksWidget(
      ResourcePacks24 resourcePacks,
      ResourcePacksController controller
  ) {
    this.resourcePacks = resourcePacks;
    this.controller = controller;

    this.containerWidget = new BrowseResourcePacksContainerWidget().addId("browse-container");

    this.session = new ListSession<>();
    this.feedWidget = new GridFeedWidget<>(this::refreshGrid, this.session).addId("feed");
    this.feedWidget.doRefresh(false);

    this.sidebarWidget = new ResourcePackSidebarWidget(controller, this);

    this.feed = this.sidebarWidget.selectedFeed();

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.addContent(this.sidebarWidget);

    this.containerWidget.updateScroll(
        new ScrollWidget(this.feedWidget, this.session).addId("scroll")
    );

    this.page = 1;
    this.loadPage(this.page);

    this.addFlexibleContent(this.containerWidget);
  }

  private boolean refreshGrid(
      GridFeedWidget<ResourcePackWidget> feedWidget,
      Consumer<ResourcePackWidget> consumer
  ) {
    return this.loadPage(++this.page);
  }

  private boolean loadPage(int page) {
    ResourcePackPageResult resourcePackPage = this.feed.getOrLoadPage(page, result -> {
      if (result.isPresent()) {
        this.labyAPI.minecraft().executeOnRenderThread(() -> this.fillGrid(result));
      } else {
        if (page == 1 || this.feed.getLastPage() == 0) {
          this.labyAPI.minecraft().executeOnRenderThread(
              () -> {
                if (this.feed.type() == Type.SEARCH) {
                  this.setInformation("noResult", NamedTextColor.RED);
                } else {
                  this.setInformation("invalidResponse", NamedTextColor.RED);
                }
              }
          );
        }
      }
    });

    if (resourcePackPage != null) {
      this.fillGrid(resourcePackPage);
      return true;
    }

    if (page == 1) {
      if (this.feed.getLastPage() == 0) {
        if (this.feed.type() == Type.SEARCH) {
          this.setInformation("noResult", NamedTextColor.RED);
        } else {
          this.setInformation("invalidResponse", NamedTextColor.RED);
        }
      } else {
        this.setInformation("loading");
      }
    }

    return false;
  }

  public void selectFeed(ResourcePackFeed feed) {
    this.feed = feed;
    this.page = 1;

    this.containerWidget.showBrowse();

    this.session.setScrollPositionY(0);
    this.feedWidget.doRefresh(false);
    this.feedWidget.getChildren().clear();
    this.loadPage(this.page);
  }

  public void fillGrid(ResourcePackPageResult page) {
    if (!page.hasMessage() && page.isPresent()) {
      for (OnlineResourcePack resourcePack : page.get().getResourcePacks()) {
        DownloadProcess process = this.controller.getDownloadProcess(resourcePack.getId());
        ResourcePackWidget resourcePackWidget = new ResourcePackWidget(resourcePack);
        resourcePackWidget.setPressable(
            () -> this.containerWidget.showInfo(new ResourcePackInfoWidget(
                    resourcePack,
                    this.controller,
                    process,
                    this.containerWidget::showBrowse
                ).addId("info-" + resourcePack.getId())
            ));

        if (this.feedWidget.isInitialized()) {
          this.feedWidget.addTileInitialized(resourcePackWidget);
        } else {
          this.feedWidget.addTile(resourcePackWidget);
        }
      }
    }

    if (page.hasMessage()) {
      this.setInformationComponent(page.getMessage());
    } else if (!page.isPresent()) {
      this.setInformation("noResult", NamedTextColor.RED);
    } else {
      this.setInformationComponent(null);
    }

    if (this.feedWidget.isInitialized()) {
      this.feedWidget.updateBounds();
    }

    this.feedWidget.doRefresh(true);
  }

  private void setInformation(String prefix) {
    this.setInformation(prefix, null);
  }

  private void setInformation(String prefix, TextColor color) {
    if (prefix == null) {
      this.setInformationComponent(null);
      return;
    }

    this.setInformationComponent(Component.translatable(
        "resourcepackstwentyfour.browse.information." + prefix,
        color == null ? NamedTextColor.GRAY : color
    ));
  }

  private void setInformationComponent(Component component) {
    if (component == null) {
      this.containerWidget.hideInformation();
      return;
    }

    this.containerWidget.showInformation(component);
  }
}

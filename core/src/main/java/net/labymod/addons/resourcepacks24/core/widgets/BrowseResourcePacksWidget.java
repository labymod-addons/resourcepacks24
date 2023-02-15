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
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackPage;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks24.core.util.DownloadProcess;
import net.labymod.addons.resourcepacks24.core.widgets.resourcepack.ResourcePackWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
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

  private final ComponentWidget informationWidget;
  private DivWidget browseContainer;

  private ResourcePackFeed feed;
  private int page = 1;

  private ResourcePackInfoWidget infoWidget;

  public BrowseResourcePacksWidget(
      ResourcePacks24 resourcePacks,
      ResourcePacksController controller
  ) {
    this.resourcePacks = resourcePacks;
    this.controller = controller;

    this.informationWidget = ComponentWidget.empty().addId("information");

    this.session = new ListSession<>();
    this.feedWidget = new GridFeedWidget<>(this::refreshGrid, this.session).addId("feed");
    this.feedWidget.doRefresh(false);

    this.sidebarWidget = new ResourcePackSidebarWidget(controller, newFeed -> {
      this.feed = newFeed;
      this.page = 1;

      if (this.infoWidget != null) {
        this.browseContainer.removeChild(this.infoWidget);
        this.infoWidget = null;
        this.browseContainer.getChild("scroll").setVisible(true);
      }

      this.session.setScrollPositionY(0);
      this.feedWidget.doRefresh(false);
      this.feedWidget.getChildren().clear();
      this.loadPage(this.page);
    });

    this.feed = this.sidebarWidget.selectedFeed();

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.addContent(this.sidebarWidget);

    this.browseContainer = new DivWidget();
    this.browseContainer.addId("browse-container");

    this.page = 1;
    this.loadPage(this.page);

    Widget scroll = new ScrollWidget(this.feedWidget, this.session).addId("scroll");
    this.browseContainer.addChild(scroll);

    if (this.infoWidget != null) {
      this.browseContainer.addChild(this.infoWidget);
      scroll.setVisible(false);
    }

    this.informationWidget.setVisible(this.informationWidget.component() != Component.empty());
    this.browseContainer.addChild(this.informationWidget);

    this.addFlexibleContent(this.browseContainer);
  }

  private boolean refreshGrid(
      GridFeedWidget<ResourcePackWidget> feedWidget,
      Consumer<ResourcePackWidget> consumer
  ) {
    return this.loadPage(++this.page);
  }

  private boolean loadPage(int page) {
    ResourcePackPage resourcePackPage = this.feed.getOrLoadPage(page, result -> {
      if (result.isPresent()) {
        this.labyAPI.minecraft().executeOnRenderThread(() -> this.fillGrid(result.get()));
      } else {
        if (page == 1 || this.feed.getLastPage() == 0) {
          this.labyAPI.minecraft().executeOnRenderThread(
              () -> this.setInformationComponent("invalidResponse")
          );
        }

        if (result.hasException()) {
          result.exception().printStackTrace();
        }
      }
    });

    if (resourcePackPage != null) {
      this.fillGrid(resourcePackPage);
      return true;
    }

    if (page == 1) {
      if (this.feed.getLastPage() == 0) {
        this.setInformationComponent("invalidResponse");
      } else {
        this.setInformationComponent("loading");
      }
    }

    return false;
  }

  private void fillGrid(ResourcePackPage page) {
    for (OnlineResourcePack resourcePack : page.getResourcePacks()) {
      DownloadProcess process = this.controller.getDownloadProcess(resourcePack.getId());
      ResourcePackWidget resourcePackWidget = new ResourcePackWidget(resourcePack);
      resourcePackWidget.setPressable(() -> {
        this.browseContainer.addChildInitialized(this.infoWidget = new ResourcePackInfoWidget(
            resourcePack,
            this.controller,
            process,
            () -> {
              this.browseContainer.getChild("scroll").setVisible(true);
              this.browseContainer.removeChild(this.infoWidget);
              this.infoWidget = null;
            }
        ).addId("info-" + resourcePack.getId()));
        this.browseContainer.getChild("scroll").setVisible(false);
      });

      if (this.feedWidget.isInitialized()) {
        this.feedWidget.addTileInitialized(resourcePackWidget);
      } else {
        this.feedWidget.addTile(resourcePackWidget);
      }
    }

    this.setInformationComponent(null);
    if (this.feedWidget.isInitialized()) {
      this.feedWidget.updateBounds();
    }

    this.feedWidget.doRefresh(true);
  }

  private void setInformationComponent(String prefix) {
    if (prefix == null) {
      this.informationWidget.setVisible(false);
      this.informationWidget.setComponent(Component.empty());
    } else {
      this.informationWidget.setComponent(
          Component.translatable("resourcepacks.browse.information." + prefix)
      );

      this.informationWidget.setVisible(true);
    }
  }
}

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

import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackCategoryFeed;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackFeed;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackFeed.Type;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks24.core.util.ResourcePackPageResult;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.util.TextFormat;

@AutoWidget
public class ResourcePackSidebarWidget extends AbstractWidget<Widget> {

  private final ResourcePacksController controller;
  private final BrowseResourcePacksWidget browseWidget;
  private final List<Widget> widgets = new ArrayList<>();
  private ResourcePackFeed feed;
  private TextFieldWidget searchWidget;
  private long lastSearchCharTyped;
  private boolean categories;

  public ResourcePackSidebarWidget(
      ResourcePacksController controller,
      BrowseResourcePacksWidget browseWidget
  ) {
    this.controller = controller;
    this.browseWidget = browseWidget;
    this.feed = this.defaultFeed();

    this.createSidebarWidgets();

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    if (!this.categories && !this.controller.getCategoryFeeds().isEmpty()) {
      this.widgets.clear();
      this.createSidebarWidgets();
    }

    VerticalListWidget<Widget> listWidget = new VerticalListWidget<>();
    listWidget.addId("sidebar-container");

    for (Widget widget : this.widgets) {
      listWidget.addChild(widget);
    }

    this.addChild(new ScrollWidget(listWidget));
  }

  private ResourcePackFeed defaultFeed() {
    return this.controller.topOfTheWeekFeed();
  }

  private void createSidebarWidgets() {
    this.searchWidget = new TextFieldWidget();
    this.searchWidget.addId("search");
    this.searchWidget.placeholder(Component.translatable("labymod.ui.textfield.search"));
    this.searchWidget.submitHandler(query -> this.search(query, true));
    this.searchWidget.updateListener(query -> {
      if (!this.searchWidget.isFocused()) {
        return;
      }

      for (Widget widget : this.widgets) {
        if (widget instanceof ButtonWidget) {
          ButtonWidget button = (ButtonWidget) widget;
          button.setEnabled(true);
        }
      }

      if (query.length() < 2) {
        this.browseWidget.fillGrid(ResourcePackPageResult.ofMessage(
            Component.translatable(
                "resourcepackstwentyfour.browse.information.notEnoughCharacters",
                NamedTextColor.GRAY
            ),
            0
        ));

        return;
      }

      this.lastSearchCharTyped = System.currentTimeMillis();
      this.browseWidget.fillGrid(ResourcePackPageResult.ofMessage(
          Component.translatable(
              "resourcepackstwentyfour.browse.information.loading",
              NamedTextColor.GRAY
          ),
          0
      ));
    });

    this.widgets.add(this.searchWidget);

    this.widgets.add(this.createFeedButton(this.controller.trendingFeed()));
    this.widgets.add(this.createFeedButton(this.controller.topOfTheWeekFeed()));
    this.widgets.add(this.createFeedButton(this.controller.promotedFeed()));
    this.widgets.add(this.createFeedButton(this.controller.newFeed()));
    this.widgets.add(this.createFeedButton(this.controller.randomFeed()));

    List<ResourcePackCategoryFeed> categories = this.controller.getCategoryFeeds();
    if (categories.isEmpty()) {
      this.categories = false;
      return;
    }

    this.categories = true;
    this.widgets.add(
        ComponentWidget.i18n("resourcepackstwentyfour.browse.feed.splitter").addId("splitter")
    );

    for (ResourcePackCategoryFeed categoryFeed : categories) {
      this.widgets.add(this.createFeedButton(categoryFeed));
    }
  }

  private void search(String query, boolean submit) {
    this.lastSearchCharTyped = 0L;
    if (query.length() == 0) {
      if (!submit) {
        return;
      }

      this.selectFeed(this.defaultFeed());
      this.searchWidget.setFocused(false);
      return;
    }

    this.selectFeed(this.controller.search(query));
  }

  private ButtonWidget createFeedButton(ResourcePackFeed feed) {
    String name = feed.getId();
    ButtonWidget feedButton = ButtonWidget.component(feed.displayName());
    feedButton.addId("feed-" + TextFormat.SNAKE_CASE.toDashCase(name));
    feedButton.setPressable(() -> this.selectFeed(feed));

    feedButton.setEnabled(this.feed != feed);
    return feedButton;
  }

  @Override
  public void tick() {
    super.tick();
    if (this.lastSearchCharTyped == 0L) {
      return;
    }

    if (System.currentTimeMillis() - this.lastSearchCharTyped > 500L) {
      if (!this.searchWidget.isFocused()) {
        return;
      }

      this.search(this.searchWidget.getText(), false);
    }
  }

  private boolean selectFeed(ResourcePackFeed feed) {
    if (this.feed == feed) {
      return false;
    }

    String id = "feed-" + TextFormat.SNAKE_CASE.toDashCase(feed.getId());
    for (Widget widget : this.widgets) {
      if (widget instanceof ButtonWidget) {
        ButtonWidget button = (ButtonWidget) widget;
        button.setEnabled(!button.hasId(id));
      }
    }

    if (feed.type() != Type.SEARCH) {
      this.lastSearchCharTyped = 0L;
      this.searchWidget.setText("");
      this.searchWidget.setFocused(false);
    }

    this.feed = feed;
    this.browseWidget.selectFeed(feed);
    return true;
  }

  public ResourcePackFeed selectedFeed() {
    return this.feed;
  }
}

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
import java.util.function.Consumer;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackCategoryFeed;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePackFeed;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.api.client.component.Component;
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
  private final Consumer<ResourcePackFeed> selectConsumer;
  private final List<Widget> widgets = new ArrayList<>();
  private ResourcePackFeed feed;
  private boolean categories;

  public ResourcePackSidebarWidget(
      ResourcePacksController controller,
      Consumer<ResourcePackFeed> selectConsumer
  ) {
    this.controller = controller;
    this.selectConsumer = selectConsumer;
    this.feed = controller.topOfTheWeekFeed();

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

  private void createSidebarWidgets() {
    TextFieldWidget searchField = new TextFieldWidget();
    searchField.addId("search");
    searchField.placeholder(Component.translatable("labymod.ui.textfield.search"));
    searchField.submitHandler(this::search);
    this.widgets.add(searchField);

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
        ComponentWidget.i18n("resourcepacks.browse.feed.splitter").addId("splitter")
    );

    for (ResourcePackCategoryFeed categoryFeed : categories) {
      this.widgets.add(this.createFeedButton(categoryFeed));
    }
  }

  private void search(String query) {

  }

  private ButtonWidget createFeedButton(ResourcePackFeed feed) {
    String name = feed.getId();
    ButtonWidget feedButton = ButtonWidget.component(feed.displayName());
    feedButton.addId("feed-" + TextFormat.SNAKE_CASE.toDashCase(name));
    feedButton.setPressable(() -> {
      this.selectFeed(feed);
    });
    feedButton.setEnabled(this.feed != feed);

    return feedButton;
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

    this.feed = feed;
    this.selectConsumer.accept(feed);
    return true;
  }

  public ResourcePackFeed selectedFeed() {
    return this.feed;
  }
}

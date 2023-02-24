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

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;

public class BrowseResourcePacksContainerWidget extends AbstractWidget<Widget> {

  private final ComponentWidget informationWidget;
  private ScrollWidget feedScrollWidget;
  private ResourcePackInfoWidget resourcePackInfoWidget;

  public BrowseResourcePacksContainerWidget() {
    this.informationWidget = ComponentWidget.empty().addId("information");
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.addChild(this.feedScrollWidget);
    if (this.resourcePackInfoWidget != null) {
      this.showInfo(this.resourcePackInfoWidget);
      this.hideInformationWidget();
    } else {
      this.informationWidget.setVisible(this.informationWidget.component() != Component.empty());
    }

    this.addChild(this.informationWidget);
  }

  public void updateScroll(ScrollWidget scrollWidget) {
    if (this.feedScrollWidget != null) {
      scrollWidget.setVisible(this.feedScrollWidget.isVisible());
    }

    this.feedScrollWidget = scrollWidget;
  }

  public void showInfo(ResourcePackInfoWidget infoWidget) {
    this.feedScrollWidget.setVisible(false);
    this.hideInformationWidget();

    this.resourcePackInfoWidget = infoWidget;
    if (this.initialized) {
      this.addChildInitialized(infoWidget);
    } else {
      this.addChild(infoWidget);
    }
  }

  public void showBrowse() {
    this.hideInformationWidget();
    this.feedScrollWidget.setVisible(true);

    if (this.resourcePackInfoWidget != null) {
      this.removeChild(this.resourcePackInfoWidget);
      this.resourcePackInfoWidget = null;
    }
  }

  public void showInformation() {
    this.feedScrollWidget.setVisible(false);
    this.informationWidget.setVisible(true);

    if (this.resourcePackInfoWidget != null) {
      this.removeChild(this.resourcePackInfoWidget);
      this.resourcePackInfoWidget = null;
    }
  }

  public void showInformation(Component component) {
    this.informationWidget.setComponent(component.colorIfAbsent(NamedTextColor.GRAY));
    this.showInformation();
  }

  public void hideInformation() {
    this.hideInformationWidget();
    if (this.resourcePackInfoWidget != null) {
      this.resourcePackInfoWidget.setVisible(true);
    } else {
      this.feedScrollWidget.setVisible(true);
    }
  }

  private void hideInformationWidget() {
    this.informationWidget.setComponent(Component.empty());
    this.informationWidget.setVisible(false);
  }
}

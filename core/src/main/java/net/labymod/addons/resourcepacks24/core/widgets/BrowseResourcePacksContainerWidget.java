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

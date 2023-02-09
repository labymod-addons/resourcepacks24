package net.labymod.addons.resourcepacks.core.widgets.resourcepacks;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

public abstract class ResourcePackWidget<T> extends AbstractWidget<Widget> {

  protected final T resourcePack;
  protected final IconWidget coverWidget;
  protected final Widget containerWidget;
  protected final ComponentWidget nameWidget;
  protected final ComponentWidget descriptionWidget;

  protected ResourcePackWidget(T resourcePack, Icon cover, Component name, Component description) {
    this.resourcePack = resourcePack;
    this.coverWidget = new IconWidget(cover).addId("cover");
    this.nameWidget = ComponentWidget.component(name).addId("name");
    this.descriptionWidget = ComponentWidget.component(description).addId("description");

    this.containerWidget = this.createContainerWidget();
    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    this.addChild(this.coverWidget);
    this.addChild(this.containerWidget);
  }

  protected Widget createContainerWidget() {
    FlexibleContentWidget containerWidget = new FlexibleContentWidget();
    containerWidget.addId("container");
    containerWidget.addContent(this.nameWidget);
    containerWidget.addFlexibleContent(this.descriptionWidget);
    return containerWidget;
  }
}

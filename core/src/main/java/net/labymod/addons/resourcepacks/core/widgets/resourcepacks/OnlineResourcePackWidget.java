package net.labymod.addons.resourcepacks.core.widgets.resourcepacks;

import net.labymod.addons.resourcepacks.core.controller.models.OnlineResourcePack;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;

@AutoWidget
public class OnlineResourcePackWidget extends ResourcePackWidget<OnlineResourcePack> {

  public OnlineResourcePackWidget(OnlineResourcePack resourcePack) {
    super(resourcePack, resourcePack.icon(), resourcePack.name(), resourcePack.description());
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
  }
}

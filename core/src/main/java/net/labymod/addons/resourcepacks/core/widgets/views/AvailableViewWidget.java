package net.labymod.addons.resourcepacks.core.widgets.views;

import net.labymod.addons.resourcepacks.core.widgets.resourcepacks.OfflineResourcePackWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;

public class AvailableViewWidget extends ViewWidget<OfflineResourcePackWidget> {

  public AvailableViewWidget() {
    super(Component.text("Available"));
  }

  @Override
  protected void fillGrid(TilesGridWidget<OfflineResourcePackWidget> grid) {

  }
}

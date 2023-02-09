package net.labymod.addons.resourcepacks.core.widgets.views;

import net.labymod.addons.resourcepacks.core.widgets.resourcepacks.OfflineResourcePackWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;

public class SelectedViewWidget extends ViewWidget<OfflineResourcePackWidget> {

  protected SelectedViewWidget() {
    super(Component.text("Selected"));
  }

  @Override
  protected void fillGrid(TilesGridWidget<OfflineResourcePackWidget> grid) {

  }
}

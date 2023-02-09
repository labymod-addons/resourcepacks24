package net.labymod.addons.resourcepacks.core.widgets.views;

import net.labymod.addons.resourcepacks.core.widgets.resourcepacks.ResourcePackWidget;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;

public abstract class ViewWidget<T extends ResourcePackWidget> extends FlexibleContentWidget {

  private final ListSession<T> session;
  private final Component title;

  protected ViewWidget(Component title) {
    this.title = title;
    this.session = new ListSession<>();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    HorizontalListWidget header = new HorizontalListWidget();
    header.addId("header");
    this.fillHeader(header);
    this.addContent(header);

    TilesGridWidget<T> grid = this.createGrid();
    grid.addId("grid");
    this.fillGrid(grid);
    this.addFlexibleContent(grid);
  }

  protected TilesGridWidget<T> createGrid() {
    return new TilesGridWidget<>();
  }

  protected void fillHeader(HorizontalListWidget header) {
    ComponentWidget titleWidget = ComponentWidget.component(this.title);
    titleWidget.addId("title");
    header.addEntry(titleWidget);
  }

  protected abstract void fillGrid(TilesGridWidget<T> grid);
}

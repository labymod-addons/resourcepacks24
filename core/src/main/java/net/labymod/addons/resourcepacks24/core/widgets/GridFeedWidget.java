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

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.Bounds;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.TilesGridWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.ListWidget;

@AutoWidget
public class GridFeedWidget<T extends Widget> extends TilesGridWidget<T> {

  private final BiPredicate<GridFeedWidget<T>, Consumer<T>> refresh;
  private final ListSession<T> session;
  private final LssProperty<Float> refreshRadius = new LssProperty<>(-1.0F);
  private boolean doRefresh = true;

  public GridFeedWidget(BiPredicate<GridFeedWidget<T>, Consumer<T>> refresh,
      ListSession<T> session) {
    this.refresh = refresh;
    this.session = session;
  }

  public GridFeedWidget(BiPredicate<GridFeedWidget<T>, Consumer<T>> refresh) {
    this(refresh, new ListSession<>());
  }

  @Override
  public void updateVisibility(ListWidget<?> list, Parent parent) {
    super.updateVisibility(list, parent);
    if (!(parent instanceof ScrollWidget)) {
      return;
    }

    Bounds parentBounds = parent.bounds();
    if (this.doRefresh && this.bounds().getHeight() > 0 && this.session.getScrollPositionY()
        >= this.bounds().getHeight() - parentBounds.getHeight() - (
        this.refreshRadius.isDefaultValue() ? this.getTileHeight() * 2
            : this.refreshRadius.get())) {
      this.doRefresh = false;
      boolean update = this.refresh.test(this, this::addTileInitialized);

      if (update) {
        this.updateTiles();
        this.doRefresh = true;
      }
    }
  }

  public LssProperty<Float> refreshRadius() {
    return this.refreshRadius;
  }

  public ListSession<T> getSession() {
    return this.session;
  }

  public void doRefresh(boolean doRefresh) {
    this.doRefresh = doRefresh;
  }
}

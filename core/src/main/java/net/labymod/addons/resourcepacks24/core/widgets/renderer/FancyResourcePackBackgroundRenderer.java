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

package net.labymod.addons.resourcepacks24.core.widgets.renderer;

import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.theme.Theme;
import net.labymod.api.client.gui.screen.theme.renderer.ThemeRenderer;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.core.client.gui.screen.theme.fancy.renderer.background.FancyScreenBackgroundRenderer;

public class FancyResourcePackBackgroundRenderer extends ThemeRenderer<AbstractWidget<?>> {

  private final FancyScreenBackgroundRenderer fancyScreenBackgroundRenderer;

  public FancyResourcePackBackgroundRenderer(Theme theme) {
    super("ResourcePackBackground");
    this.fancyScreenBackgroundRenderer = new FancyScreenBackgroundRenderer(theme);
  }

  @Override
  public void renderPre(AbstractWidget<?> widget, Stack stack, MutableMouse mouse, float delta) {
    this.fancyScreenBackgroundRenderer.renderBackground(stack, false);
  }
}

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

package net.labymod.addons.resourcepacks24.core.listener;

import net.labymod.addons.resourcepacks24.core.widgets.renderer.FancyResourcePackBackgroundRenderer;
import net.labymod.api.client.gui.screen.theme.AbstractTheme;
import net.labymod.api.client.gui.screen.theme.Theme;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.theme.ThemeChangeEvent;

public class ThemeChangeListener {

  @Subscribe
  public void onThemeChange(ThemeChangeEvent event) {
    if (event.phase() != Phase.POST) {
      return;
    }

    Theme theme = event.newTheme();
    if (theme.getId().equals("fancy") && theme instanceof AbstractTheme) {
      AbstractTheme fancyTheme = (AbstractTheme) theme;
      fancyTheme.registerWidgetRenderer(new FancyResourcePackBackgroundRenderer(theme));
    }
  }
}

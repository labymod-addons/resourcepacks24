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

package net.labymod.addons.resourcepacks24.core;

import net.labymod.addons.resourcepacks24.core.activity.ResourcePacksOverlay;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks24.core.listener.ThemeChangeListener;
import net.labymod.addons.resourcepacks24.core.widgets.renderer.FancyResourcePackBackgroundRenderer;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.screen.NamedScreen;
import net.labymod.api.client.gui.screen.theme.AbstractTheme;
import net.labymod.api.client.gui.screen.theme.Theme;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class ResourcePacks24 extends LabyAddon<ResourcePacks24Configuration> {

  public static final ResourceLocation FALLBACK_ICON = Laby.references()
      .resourceLocationFactory()
      .createMinecraft("textures/misc/unknown_pack.png");

  @Override
  protected void enable() {
    this.registerSettingCategory();

    ResourcePacksController controller = new ResourcePacksController();

    ResourcePacksOverlay resourcePacksOverlay = new ResourcePacksOverlay(null, this,
        controller);
    this.labyAPI().activityOverlayRegistry().register(
        NamedScreen.RESOURCE_PACK_SETTINGS,
        ResourcePacksOverlay.class,
        parentScreen -> {
          resourcePacksOverlay.setParentScreen(parentScreen);
          return resourcePacksOverlay;
        }
    );

    this.registerListener(new ThemeChangeListener());
    Theme fancy = this.labyAPI().themeService().getThemeByName("fancy");
    if (fancy instanceof AbstractTheme) {
      AbstractTheme fancyTheme = (AbstractTheme) fancy;
      fancyTheme.registerWidgetRenderer(new FancyResourcePackBackgroundRenderer(fancy));
    }
  }

  @Override
  protected Class<ResourcePacks24Configuration> configurationClass() {
    return ResourcePacks24Configuration.class;
  }
}

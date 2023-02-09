package net.labymod.addons.resourcepacks.core;

import net.labymod.addons.resourcepacks.core.activity.ResourcepacksOverlay;
import net.labymod.addons.resourcepacks.core.controller.ResourcePacksController;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.screen.NamedScreen;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class ResourcePacks extends LabyAddon<ResourcepacksConfiguration> {

  public static final ResourceLocation FALLBACK_ICON = Laby.references()
      .resourceLocationFactory()
      .createMinecraft("textures/misc/unknown_pack.png");

  @Override
  protected void enable() {
    this.registerSettingCategory();

    ResourcePacksController controller = new ResourcePacksController();

    this.labyAPI().activityOverlayRegistry().register(
        NamedScreen.RESOURCE_PACK_SETTINGS,
        ResourcepacksOverlay.class,
        parentScreen -> new ResourcepacksOverlay(parentScreen, this, controller)
    );
  }

  @Override
  protected Class<ResourcepacksConfiguration> configurationClass() {
    return ResourcepacksConfiguration.class;
  }
}

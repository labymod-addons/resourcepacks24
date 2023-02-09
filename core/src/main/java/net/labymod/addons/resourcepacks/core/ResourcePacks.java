package net.labymod.addons.resourcepacks.core;

import net.labymod.addons.resourcepacks.core.activity.ResourcePacksOverlay;
import net.labymod.addons.resourcepacks.core.controller.ResourcePacksController;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.screen.NamedScreen;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class ResourcePacks extends LabyAddon<ResourcePacksConfiguration> {

  public static final ResourceLocation FALLBACK_ICON = Laby.references()
      .resourceLocationFactory()
      .createMinecraft("textures/misc/unknown_pack.png");

  @Override
  protected void enable() {
    this.registerSettingCategory();

    ResourcePacksController controller = new ResourcePacksController();

    this.labyAPI().activityOverlayRegistry().register(
        NamedScreen.RESOURCE_PACK_SETTINGS,
        ResourcePacksOverlay.class,
        parentScreen -> new ResourcePacksOverlay(parentScreen, this, controller)
    );
  }

  @Override
  protected Class<ResourcePacksConfiguration> configurationClass() {
    return ResourcePacksConfiguration.class;
  }
}

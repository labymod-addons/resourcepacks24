package net.labymod.addons.resourcepacks.core.controller;

import java.util.List;
import net.labymod.addons.resourcepacks.core.controller.models.ResourcePack;

public class ResourcePackPage {

  private final int number;
  private final ResourcePack[] resourcePacks;

  protected ResourcePackPage(int number, int size) {
    this.number = number;
    this.resourcePacks = new ResourcePack[size];
  }

  public int getNumber() {
    return this.number;
  }

  public ResourcePack[] getResourcePacks() {
    return this.resourcePacks;
  }

  public ResourcePackPage put(int index, ResourcePack resourcePack) {
    if (index < 0 || index >= this.resourcePacks.length) {
      throw new IllegalArgumentException("Index out of bounds");
    }

    this.resourcePacks[index] = resourcePack;
    return this;
  }

  public ResourcePackPage putAll(List<ResourcePack> resourcePacks) {
    for (int i = 0; i < resourcePacks.size(); i++) {
      this.put(i, resourcePacks.get(i));
    }

    return this;
  }
}

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

package net.labymod.addons.resourcepacks24.core.controller;

import java.util.List;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;

public class ResourcePackPage {

  private final int number;
  private final OnlineResourcePack[] resourcePacks;

  protected ResourcePackPage(int number, int size) {
    this.number = number;
    this.resourcePacks = new OnlineResourcePack[size];
  }

  public int getNumber() {
    return this.number;
  }

  public OnlineResourcePack[] getResourcePacks() {
    return this.resourcePacks;
  }

  public ResourcePackPage put(int index, OnlineResourcePack resourcePack) {
    if (index < 0 || index >= this.resourcePacks.length) {
      throw new IllegalArgumentException("Index out of bounds");
    }

    this.resourcePacks[index] = resourcePack;
    return this;
  }

  public ResourcePackPage putAll(List<OnlineResourcePack> resourcePacks) {
    for (int i = 0; i < resourcePacks.size(); i++) {
      this.put(i, resourcePacks.get(i));
    }

    return this;
  }
}

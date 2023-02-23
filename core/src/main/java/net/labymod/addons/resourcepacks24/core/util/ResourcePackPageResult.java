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

package net.labymod.addons.resourcepacks24.core.util;

import net.labymod.addons.resourcepacks24.core.controller.ResourcePackPage;
import net.labymod.api.client.component.Component;
import net.labymod.api.util.io.web.result.AbstractResult;

public class ResourcePackPageResult extends AbstractResult<ResourcePackPage> {

  private final int pageNumber;
  private final Component message;

  private ResourcePackPageResult(
      ResourcePackPage value,
      Component message,
      int pageNumber
  ) {
    super(value);
    this.pageNumber = pageNumber;
    this.message = message;
  }

  public static ResourcePackPageResult of(ResourcePackPage value, int pageNumber) {
    return new ResourcePackPageResult(value, null, pageNumber);
  }

  public static ResourcePackPageResult ofMessage(Component component, int pageNumber) {
    return new ResourcePackPageResult(null, component, pageNumber);
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && !this.hasMessage();
  }

  public int getNumber() {
    return this.pageNumber;
  }

  public Component getMessage() {
    return this.message;
  }

  public boolean hasMessage() {
    return this.message != null;
  }
}

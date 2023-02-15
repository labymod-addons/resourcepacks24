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

import net.labymod.api.client.component.Component;
import net.labymod.api.util.I18n;

public class ResourcePackCategoryFeed extends ResourcePackFeed {

  private final String categoryName;

  public ResourcePackCategoryFeed(
      ResourcePacksController controller,
      String url,
      String translationKey,
      String categoryName
  ) {
    super(controller, Type.CATEGORY, translationKey, url);

    this.categoryName = categoryName;
  }

  public String getCategoryName() {
    return this.categoryName;
  }

  @Override
  public String getId() {
    return super.getId() + "_" + this.getCategoryName();
  }

  @Override
  public Component displayName() {
    if (this.displayName == null) {
      String translation = I18n.getTranslation(this.translationKey + "." + this.categoryName);
      if (translation == null) {
        this.displayName = Component.text(this.categoryName.toUpperCase());
      } else {
        this.displayName = Component.text(translation);
      }
    }

    return this.displayName;
  }
}

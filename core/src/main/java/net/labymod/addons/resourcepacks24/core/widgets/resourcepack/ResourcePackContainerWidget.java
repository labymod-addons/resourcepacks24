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

package net.labymod.addons.resourcepacks24.core.widgets.resourcepack;

import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.RatingWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;

@AutoWidget
public class ResourcePackContainerWidget extends FlexibleContentWidget {

  private final ResourcePackHeaderWidget headerWidget;
  private final ComponentWidget descriptionWidget;

  protected ResourcePackContainerWidget(
      ComponentWidget nameWidget,
      ComponentWidget descriptionWidget,
      RatingWidget ratingWidget
  ) {
    this.headerWidget = new ResourcePackHeaderWidget(nameWidget, ratingWidget).addId("header");
    this.descriptionWidget = descriptionWidget;

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.addContent(this.headerWidget);
    this.addFlexibleContent(this.descriptionWidget);
  }
}

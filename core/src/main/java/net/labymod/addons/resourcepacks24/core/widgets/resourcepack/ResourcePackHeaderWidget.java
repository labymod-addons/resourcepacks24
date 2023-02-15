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
public class ResourcePackHeaderWidget extends FlexibleContentWidget {

  private final ComponentWidget nameWidget;
  private final RatingWidget ratingWidget;

  protected ResourcePackHeaderWidget(ComponentWidget nameWidget, RatingWidget ratingWidget) {
    this.nameWidget = nameWidget;
    this.ratingWidget = ratingWidget;

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    this.addFlexibleContent(this.nameWidget);
    this.addContent(this.ratingWidget);
  }
}

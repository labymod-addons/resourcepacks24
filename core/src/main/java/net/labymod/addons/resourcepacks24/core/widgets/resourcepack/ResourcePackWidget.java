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

import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.RatingWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

@AutoWidget
public class ResourcePackWidget extends AbstractWidget<Widget> {

  private final IconWidget coverWidget;
  private final ResourcePackContainerWidget containerWidget;

  public ResourcePackWidget(OnlineResourcePack resourcePack) {
    this.coverWidget = new ResourcePackCoverWidget(
        resourcePack.icon(),
        ComponentWidget.component(resourcePack.size()).addId("size")
    ).addId("cover");

    this.containerWidget = new ResourcePackContainerWidget(
        ComponentWidget.component(resourcePack.name()).addId("name"),
        ComponentWidget.component(resourcePack.description()).addId("description"),
        new RatingWidget(resourcePack.getRating()).addId("rating")
    ).addId("container");

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    this.addChild(this.coverWidget);
    this.addChild(this.containerWidget);
  }
}

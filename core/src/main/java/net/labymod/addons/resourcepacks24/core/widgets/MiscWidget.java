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

package net.labymod.addons.resourcepacks24.core.widgets;

import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

@AutoWidget
public class MiscWidget extends HorizontalListWidget {

  private final Icon icon;
  private final Component title;
  private final String value;

  protected MiscWidget(Icon icon, String titleKey, Object value) {
    String title = Laby.references().internationalization().getRawTranslation(titleKey);

    this.icon = icon;
    this.title = Component.text(title.toUpperCase());

    if (value == null) {
      this.value = "undefined";
    } else {
      this.value = value.toString();
    }
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    IconWidget iconWidget = new IconWidget(this.icon);
    iconWidget.addId("misc-icon");
    this.addEntry(iconWidget);

    VerticalListWidget<ComponentWidget> textContainer = new VerticalListWidget<>();
    textContainer.addId("text-container");

    ComponentWidget titleComponent = ComponentWidget.component(this.title);
    titleComponent.addId("misc-title");
    textContainer.addChild(titleComponent);

    ComponentWidget valueComponent = ComponentWidget.text(this.value);
    valueComponent.addId("misc-value");
    textContainer.addChild(valueComponent);

    this.addEntry(textContainer);
  }
}

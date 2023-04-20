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

package net.labymod.addons.resourcepacks24.core.activity;

import net.labymod.addons.resourcepacks24.core.ResourcePacks24;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks24.core.widgets.BrowseResourcePacksWidget;
import net.labymod.api.Constants.NamedThemeResource;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.mouse.MutableMouse;
import net.labymod.api.client.gui.screen.LabyScreen;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.ScreenInstance;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.AbstractLayerActivity;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.render.matrix.Stack;
import org.jetbrains.annotations.Nullable;

@AutoActivity
@Link("resourcepack-overview.lss")
public class ResourcePacksOverlay extends AbstractLayerActivity {

  private static final MutableMouse DUMMY_MOUSE = new MutableMouse();

  private final ResourcePacks24 resourcePacks;
  private final ResourcePacksController controller;

  private final BrowseResourcePacksWidget browseWidget;
  private DivWidget innerOverlayWrapper;

  private boolean overlayVisible;

  public ResourcePacksOverlay(ScreenInstance parentScreen, ResourcePacks24 resourcePacks,
      ResourcePacksController controller) {
    super(parentScreen);

    this.resourcePacks = resourcePacks;
    this.controller = controller;

    this.browseWidget = new BrowseResourcePacksWidget(resourcePacks, controller);
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    ButtonWidget browseButton = ButtonWidget.i18n("resourcepackstwentyfour.browse.button");
    browseButton.addId("browse-button");
    browseButton.setPressable(() -> {
      this.overlayVisible = !this.overlayVisible;
      this.reload();
    });

    this.document.addChild(browseButton);
    if (!this.overlayVisible) {
      return;
    }

    DivWidget overlayWrapper = new DivWidget();
    overlayWrapper.addId("overlay-wrapper");

    this.innerOverlayWrapper = new DivWidget();
    this.innerOverlayWrapper.addId("inner-overlay-wrapper");

    HorizontalListWidget browseHeader = new HorizontalListWidget();
    browseHeader.addId("browse-header");

    DivWidget titleWrapper = new DivWidget();
    titleWrapper.addId("title-wrapper");

    ComponentWidget title = ComponentWidget.i18n("resourcepackstwentyfour.settings.name");
    title.addId("title");
    titleWrapper.addChild(title);
    browseHeader.addEntry(titleWrapper);

    IconWidget closeButton = new IconWidget(Icon.sprite8(NamedThemeResource.COMMON, 2, 4));
    closeButton.addId("close-button");
    closeButton.setPressable(() -> {
      this.overlayVisible = false;
      this.reload();
    });

    browseHeader.addEntry(closeButton);
    this.innerOverlayWrapper.addChild(browseHeader);

    DivWidget browseWrapper = new DivWidget();
    browseWrapper.addId("browse-wrapper");

    browseWrapper.addChild(this.browseWidget);
    this.innerOverlayWrapper.addChild(browseWrapper);
    overlayWrapper.addChild(this.innerOverlayWrapper);

    this.document.addChild(overlayWrapper);
  }

  @Override
  public boolean mouseClicked(MutableMouse mouse, MouseButton mouseButton) {
    if (this.overlayVisible && this.innerOverlayWrapper != null) {
      return this.innerOverlayWrapper.mouseClicked(mouse, mouseButton);
    }

    return super.mouseClicked(mouse, mouseButton);
  }

  @Override
  public void render(Stack stack, MutableMouse mouse, float partialTicks) {
    super.render(stack, mouse, partialTicks);
  }

  @Override
  public boolean keyPressed(Key key, InputType type) {
    if (this.overlayVisible && key == Key.ESCAPE) {
      this.overlayVisible = false;
      this.reload();
      return true;
    }

    return super.keyPressed(key, type);
  }

  public void setParentScreen(ScreenInstance previousScreen) {
    this.parentScreen = previousScreen;
  }
}

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

import java.util.List;
import net.labymod.addons.resourcepacks24.core.controller.ResourcePacksController;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack.Screenshot;
import net.labymod.addons.resourcepacks24.core.util.DownloadProcess;
import net.labymod.addons.resourcepacks24.core.util.DownloadProcess.State;
import net.labymod.addons.resourcepacks24.core.util.FileSizeConverter;
import net.labymod.api.Constants.NamedThemeResource;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.lss.style.modifier.attribute.AttributeState;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.RatingWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.CompletableResourceLocation;

@AutoWidget
public class ResourcePackInfoWidget extends AbstractWidget<Widget> {

  private final OnlineResourcePack resourcePack;
  private final ResourcePacksController controller;
  private final Runnable backRunnable;
  private DownloadProcess process;

  public ResourcePackInfoWidget(OnlineResourcePack resourcePack, ResourcePacksController controller,
      DownloadProcess process, Runnable backRunnable) {
    this.resourcePack = resourcePack;
    this.controller = controller;
    this.process = process;
    this.backRunnable = backRunnable;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.children.clear();

    VerticalListWidget<Widget> container = new VerticalListWidget<>();
    container.addId("container");

    DivWidget detailsContainer = new DivWidget();
    detailsContainer.addId("details-container");

    IconWidget iconWidget = new IconWidget(this.resourcePack.icon());
    iconWidget.addId("icon");
    detailsContainer.addChild(iconWidget);

    DivWidget detailsInfoWrapper = new DivWidget();
    detailsInfoWrapper.addId("details-info-wrapper");

    VerticalListWidget<Widget> detailsInfoContainer = new VerticalListWidget<>();
    detailsInfoContainer.addId("details-info-container");

    ComponentWidget nameWidget = ComponentWidget.component(this.resourcePack.name());
    nameWidget.addId("name");
    detailsInfoContainer.addChild(nameWidget);

    RatingWidget ratingWidget = new RatingWidget(this.resourcePack.getRating());
    ratingWidget.addId("rating");
    detailsInfoContainer.addChild(ratingWidget);
    detailsInfoWrapper.addChild(detailsInfoContainer);
    detailsContainer.addChild(detailsInfoWrapper);
    container.addChild(detailsContainer);

    HorizontalListWidget miscContainer = new HorizontalListWidget();
    miscContainer.addId("misc-container");

    miscContainer.addEntry(new MiscWidget(
        Icon.sprite32(NamedThemeResource.FLINT, 0, 1),
        "labymod.addons.store.profile.downloads",
        String.format("%,d", this.resourcePack.getDownloads())
    ).addId("downloads"));

    miscContainer.addEntry(new MiscWidget(
        Icon.sprite32(NamedThemeResource.FLINT, 2, 1),
        "Size",
        FileSizeConverter.convertToHumanReadableString(this.resourcePack.getSize())
    ).addId("size"));

    miscContainer.addEntry(new MiscWidget(
        Icon.sprite32(NamedThemeResource.FLINT, 3, 1),
        "Category",
        this.resourcePack.getCategory().toUpperCase()
    ).addId("category"));

    container.addChild(miscContainer);

    ComponentWidget descriptionTitleWidget = ComponentWidget.text("Description");
    descriptionTitleWidget.addId("description-title");
    container.addChild(descriptionTitleWidget);

    ComponentWidget descriptionWidget = ComponentWidget.component(this.resourcePack.description());
    descriptionWidget.addId("description");
    container.addChild(descriptionWidget);

    List<Screenshot> screenshots = this.resourcePack.getScreenshots();
    if (!screenshots.isEmpty()) {
      ComponentWidget screenshotsTitleWidget = ComponentWidget.text("Screenshots");
      screenshotsTitleWidget.addId("screenshots-title");
      container.addChild(screenshotsTitleWidget);

      VerticalListWidget<IconWidget> screenshotsContainer = new VerticalListWidget<>();
      screenshotsContainer.addId("screenshots-container");
      for (Screenshot screenshot : screenshots) {
        CompletableResourceLocation resourceLocation = screenshot.getCompletableResourceLocation();
        if (resourceLocation == null) {
          continue;
        }

        IconWidget screenshotWidget = new IconWidget(Icon.completable(resourceLocation));
        screenshotWidget.addId("screenshot");
        if (!resourceLocation.hasResult()) {
          resourceLocation.addCompletableListener(() -> {
            screenshotWidget.setVisible(resourceLocation.hasResult());
          });
        }

        screenshotsContainer.addChild(screenshotWidget);
      }

      container.addChild(screenshotsContainer);
    }

    this.addChild(new ScrollWidget(container));

    DivWidget topContainer = new DivWidget();
    topContainer.addId("top-container");

    ButtonWidget backButton = ButtonWidget.icon(Icon.sprite8(NamedThemeResource.COMMON, 4, 4));
    backButton.addId("back-button");
    backButton.setPressable(this.backRunnable::run);
    topContainer.addChild(backButton);

    ButtonWidget downloadButton = ButtonWidget.text("");
    downloadButton.addId("download-button");
    this.updateDownloadButton(downloadButton);
    downloadButton.setPressable(() -> {
      if (!downloadButton.isAttributeStateEnabled(AttributeState.ENABLED)) {
        return;
      }

      downloadButton.setEnabled(false);
      if (this.process == null) {
        this.controller.download(this.resourcePack.getId(), progress -> this.labyAPI.minecraft()
                .executeOnRenderThread(
                    () -> downloadButton.updateComponent(Component.text(progress.intValue() + "%"))),
            result -> this.labyAPI.minecraft().executeOnRenderThread(() -> {
              if (result.hasException()) {
                Throwable cause = result.exception().getCause();
                System.out.println("Failed to download resource pack: " + cause);
                downloadButton.updateComponent(Component.text("Failed!"));
                return;
              }

              this.process = result.get();
              this.process.setCallback(process -> {
                this.updateDownloadButton(downloadButton);
              });

              this.updateDownloadButton(downloadButton);
            }));
      } else if (this.process.state() == State.NONE) {
        this.process.start();
        this.process.setCallback(process -> this.labyAPI.minecraft().executeOnRenderThread(() -> {
          this.updateDownloadButton(downloadButton);
        }));

        this.updateDownloadButton(downloadButton);
      }
    });

    topContainer.addChild(downloadButton);
    this.addChild(topContainer);
  }

  private void updateDownloadButton(ButtonWidget buttonWidget) {
    if (this.process != null) {
      boolean enabled = true;
      switch (this.process.state()) {
        case DOWNLOADING:
          enabled = false;
          buttonWidget.updateComponent(Component.text("Downloading..."));
          break;
        case FAILED:
          enabled = false;
          buttonWidget.updateComponent(Component.text("Failed!"));
          break;
        case FINISHED:
          enabled = false;
          buttonWidget.updateComponent(Component.text("Finished"));
          break;
      }

      buttonWidget.setEnabled(enabled);
      return;
    }

    buttonWidget.updateComponent(Component.text("Download"));
  }

  private void download(DownloadProcess process) {

  }

  public OnlineResourcePack resourcePack() {
    return this.resourcePack;
  }
}

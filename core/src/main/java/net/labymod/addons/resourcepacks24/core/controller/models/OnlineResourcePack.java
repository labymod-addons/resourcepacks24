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

package net.labymod.addons.resourcepacks24.core.controller.models;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;
import net.labymod.addons.resourcepacks24.core.ResourcePacks24;
import net.labymod.addons.resourcepacks24.core.controller.adapter.ResourcePackTagAdapter;
import net.labymod.addons.resourcepacks24.core.util.FileSizeConverter;
import net.labymod.api.Laby;
import net.labymod.api.Textures;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.CompletableResourceLocation;
import net.labymod.api.client.resources.texture.TextureRepository;

public class OnlineResourcePack {

  private int size;
  private String thumbnail;
  private String description;
  private String category;
  private double rating;
  private UUID creator;
  private List<Screenshot> screenshots;

  @SerializedName("rp_id")
  private int id;

  @SerializedName("website_name")
  private String onlineName;

  @SerializedName("ingame_name")
  private String ingameName;

  @SerializedName("download")
  private int downloads;

  @JsonAdapter(ResourcePackTagAdapter.class)
  private List<String> tags;

  @SerializedName("created_at")
  private String createdAt;

  @SerializedName("updated_at")
  private String updatedAt;

  private transient Icon icon;
  private transient Component nameComponent;
  private transient Component descriptionComponent;
  private transient Component sizeComponent;

  public int getId() {
    return this.id;
  }

  public String getOnlineName() {
    return this.onlineName;
  }

  public String getIngameName() {
    return this.ingameName;
  }

  public int getSize() {
    return this.size;
  }

  public String getThumbnail() {
    return this.thumbnail;
  }

  public int getDownloads() {
    return this.downloads;
  }

  public String getDescription() {
    return this.description;
  }

  public String getCategory() {
    return this.category;
  }

  public double getRating() {
    return this.rating;
  }

  public List<String> getTags() {
    return this.tags;
  }

  public List<Screenshot> getScreenshots() {
    return this.screenshots;
  }

  public UUID getCreator() {
    return this.creator;
  }

  public String getCreatedAtRaw() {
    return this.createdAt;
  }

  public String getUpdatedAtRaw() {
    return this.updatedAt;
  }

  public Icon icon() {
    if (this.icon == null) {
      if (this.thumbnail == null) {
        this.icon = Icon.texture(ResourcePacks24.FALLBACK_ICON);
      } else {
        this.icon = Icon.url(this.thumbnail, ResourcePacks24.FALLBACK_ICON);
      }
    }

    return this.icon;
  }

  public Component name() {
    if (this.nameComponent == null) {
      if (this.ingameName == null) {
        this.ingameName = "";
      }

      // parse annoying stuff out of the name
      String ingameName = this.ingameName.trim();
      if (ingameName.charAt(0) == '\u00A7' && ingameName.indexOf(' ') == 3) {
        ingameName = ingameName.substring(2);
      }

      if (ingameName.startsWith("!")) {
        ingameName = ingameName.substring(1);
      }

      this.nameComponent = Component.text(ingameName.trim());
    }

    return this.nameComponent;
  }

  public Component description() {
    if (this.descriptionComponent == null) {
      if (this.description == null) {
        this.description = "";
      }

      this.descriptionComponent = Component.text(this.description);
    }

    return this.descriptionComponent;
  }

  public Component size() {
    if (this.sizeComponent == null) {
      this.sizeComponent = Component.text(
          FileSizeConverter.convertToHumanReadableString(this.size)
      );
    }

    return this.sizeComponent;
  }

  public static class Screenshot {

    private static final TextureRepository TEXTURE_REPOSITORY = Laby.references()
        .textureRepository();

    @SerializedName("image_url")
    private String url;

    private transient CompletableResourceLocation completableResourceLocation;

    public String getUrl() {
      return this.url;
    }

    public Icon getIcon() {
      if (this.url == null) {
        return null;
      }

      return Icon.completable(this.getCompletableResourceLocation());
    }

    public CompletableResourceLocation getCompletableResourceLocation() {
      if (this.url == null) {
        return null;
      }

      if (this.completableResourceLocation == null) {
        this.completableResourceLocation = TEXTURE_REPOSITORY.loadCacheResourceAsync(
            "resourcepacks24",
            "url/" + this.url.hashCode(),
            this.url,
            Textures.EMPTY
        );
      }

      return this.completableResourceLocation;
    }
  }
}

package net.labymod.addons.resourcepacks.core.controller.models;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;
import net.labymod.addons.resourcepacks.core.ResourcePacks;
import net.labymod.addons.resourcepacks.core.controller.adapter.ResourcePackTagAdapter;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;

public class ResourcePack {

  private int size;
  private String thumbnail;
  private String description;
  private String category;
  private double rating;
  private UUID creator;

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
      this.icon = Icon.url(this.thumbnail, ResourcePacks.FALLBACK_ICON);
    }

    return this.icon;
  }

  public Component name() {
    if (this.nameComponent == null) {
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
      this.descriptionComponent = Component.text(this.description);
    }

    return this.descriptionComponent;
  }
}

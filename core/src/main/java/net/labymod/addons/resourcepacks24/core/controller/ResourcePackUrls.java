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

public class ResourcePackUrls {

  private static final String BASE = "https://resourcepacks24.de/api/";
  public static final String TRENDING = BASE + "feed/trending";
  public static final String PROMOTED = BASE + "feed/promotion";
  public static final String WEEK = BASE + "feed/resourcepack-of-the-week";
  public static final String NEW = BASE + "resourcepacks/new";
  public static final String RANDOM = BASE + "resourcepacks/random";

  public static final String CATEGORIES = BASE + "category/";
  public static final String CATEGORY = CATEGORIES + "%s";

  public static final String DOWNLOAD = BASE + "download/%s";
  public static final String SEARCH = BASE + "search/%s";

  private ResourcePackUrls() {
  }
}

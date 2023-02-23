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

import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.resourcepacks24.core.controller.models.OnlineResourcePack;
import net.labymod.addons.resourcepacks24.core.util.ResourcePackPageResult;
import net.labymod.api.client.component.Component;

/**
 * Well, this is needed as for example the random resourcepacks endpoint returns an array of
 * resourcepacks with up to 250 entries. First of all, if the array is filled completely 250 widgets
 * get initialized simultaneously. On top of that, the endpoint returns the same json array every
 * time you request it (at least today, 13.02.2023), even if the page parameter changed. This class
 * is used to mitigate both of these issues.
 */
public class ResourcePackUglyFeed extends ResourcePackFeed {

  public ResourcePackUglyFeed(
      ResourcePacksController controller,
      Type type,
      String translationKey,
      String url
  ) {
    super(controller, type, translationKey, url);
  }

  @Override
  protected ResourcePackPageResult toPage(
      int pageNumber,
      int size,
      List<OnlineResourcePack> resourcePacks
  ) {
    List<OnlineResourcePack> resourcePackPage = new ArrayList<>();
    ResourcePackPageResult desiredPage = null;
    ResourcePackPageResult lastPage = null;

    int lastIndex = resourcePacks.size() - 1;
    for (int i = 0; i <= lastIndex; i++) {
      resourcePackPage.add(resourcePacks.get(i));

      int currentPageSize = resourcePackPage.size();
      if (currentPageSize == 20 || i == lastIndex) {
        ResourcePackPage page = new ResourcePackPage(pageNumber, currentPageSize);
        page.putAll(resourcePackPage);

        ResourcePackPageResult result = ResourcePackPageResult.of(page, pageNumber);
        lastPage = result;
        if (desiredPage == null) {
          desiredPage = result;
        } else {
          this.pages.add(result);
        }

        resourcePackPage.clear();
        pageNumber++;
      }
    }

    if (lastPage == null) {
      lastPage = ResourcePackPageResult.ofMessage(
          Component.text("No resourcepacks found"),
          pageNumber
      );

      desiredPage = lastPage;
    }

    // set the last page so the same response isn't retrieved multiple times.
    this.lastPage = lastPage.getNumber();
    return desiredPage;
  }
}

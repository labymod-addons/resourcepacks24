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

package net.labymod.addons.resourcepacks24.core.util.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CachedMultitonCallback<C extends Consumer> extends CachedCallback<C> {

  private final List<C> callbacks;

  protected CachedMultitonCallback(String identifier, C callback) {
    super(identifier);

    this.callbacks = new ArrayList<>();
    this.callbacks.add(callback);
  }

  @Override
  public void add(C callback) {
    this.callbacks.add(callback);
  }

  @Override
  public <T> void fire(T value) {
    for (C callback : this.callbacks) {
      callback.accept(value);
    }
  }
}

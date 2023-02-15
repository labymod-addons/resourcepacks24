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

public class CachedCallbackCollection<C extends Consumer> {

  private final List<CachedCallback<C>> callbacks;

  private CachedCallbackCollection() {
    this.callbacks = new ArrayList<>();
  }

  public static <C extends Consumer> CachedCallbackCollection<C> create() {
    return new CachedCallbackCollection<>();
  }

  public boolean addSingleton(Object identifier, C callback) {
    String identifierString = identifier.toString();
    CachedCallback<C> cachedCallback = this.get(identifierString);
    if (cachedCallback == null) {
      this.callbacks.add(new CachedSingletonCallback<>(
          identifierString,
          callback
      ));

      return false;
    }

    if (!(cachedCallback instanceof CachedSingletonCallback<?>)) {
      throw new IllegalStateException(
          "Callback with identifier " + identifierString + " is not a singleton");
    }

    cachedCallback.add(callback);
    return true;
  }

  public boolean addMultiton(Object identifier, C callback) {
    String identifierString = identifier.toString();
    CachedCallback<C> cachedCallback = this.get(identifierString);
    if (cachedCallback == null) {
      this.callbacks.add(new CachedMultitonCallback<>(
          identifierString,
          callback
      ));

      return false;
    }

    if (!(cachedCallback instanceof CachedMultitonCallback<?>)) {
      throw new IllegalStateException(
          "Callback with identifier " + identifierString + " is not a multiton");
    }

    cachedCallback.add(callback);
    return true;
  }

  public <T> void fire(Object identifier, T value) {
    String identifierString = identifier.toString();
    CachedCallback<C> cachedCallback = this.get(identifierString);
    if (cachedCallback == null) {
      return;
    }

    cachedCallback.fire(value);
    this.callbacks.remove(cachedCallback);
  }

  private CachedCallback<C> get(String identifier) {
    for (CachedCallback<C> callback : this.callbacks) {
      if (callback.getIdentifier().equals(identifier)) {
        return callback;
      }
    }

    return null;
  }
}

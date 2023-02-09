package net.labymod.addons.resourcepacks.core.util.callback;

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

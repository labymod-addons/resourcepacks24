package net.labymod.addons.resourcepacks.core.util.callback;

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

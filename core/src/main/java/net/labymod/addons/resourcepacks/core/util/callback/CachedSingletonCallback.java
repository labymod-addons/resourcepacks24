package net.labymod.addons.resourcepacks.core.util.callback;

import java.util.function.Consumer;

public class CachedSingletonCallback<C extends Consumer> extends CachedCallback<C> {

  private C callback;

  protected CachedSingletonCallback(String identifier, C callback) {
    super(identifier);

    this.callback = callback;
  }

  @Override
  public void add(C callback) {
    this.callback = callback;
  }

  @Override
  public <T> void fire(T value) {
    this.callback.accept(value);
  }
}

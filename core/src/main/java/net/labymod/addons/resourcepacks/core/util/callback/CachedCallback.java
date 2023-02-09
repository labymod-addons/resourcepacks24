package net.labymod.addons.resourcepacks.core.util.callback;

import java.util.function.Consumer;

public abstract class CachedCallback<T extends Consumer> {

  private final String identifier;

  protected CachedCallback(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public abstract void add(T callback);

  public abstract <T> void fire(T value);
}

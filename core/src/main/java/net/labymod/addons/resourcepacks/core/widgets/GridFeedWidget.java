package net.labymod.addons.resourcepacks.core.widgets;

import static net.labymod.api.client.gui.screen.widget.attributes.bounds.BoundsType.INNER;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.action.ListSession;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.Bounds;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.BoundsType;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.ListWidget;
import net.labymod.api.util.bounds.ModifyReason;
import net.labymod.api.util.bounds.Rectangle;
import net.labymod.api.util.math.MathHelper;

@AutoWidget
public class GridFeedWidget extends ListWidget<Widget> {

  private static final ModifyReason ENTRY_BOUNDS = ModifyReason.of("entryBounds");

  private final BiPredicate<GridFeedWidget, Consumer<Widget>> refresh;
  private final ListSession<?> session;
  private final LssProperty<Float> spaceBetweenEntries = new LssProperty<>(0.0F);
  private final LssProperty<Integer> tilesPerLine = new LssProperty<>(3);
  private final LssProperty<Float> tileHeight = new LssProperty<>(-1.0F);
  private final LssProperty<Float> refreshRadius = new LssProperty<>(-1.0F);
  private boolean doRefresh = true;

  public GridFeedWidget(BiPredicate<GridFeedWidget, Consumer<Widget>> refresh,
      ListSession<?> session) {
    this.refresh = refresh;
    this.session = session;
    this.translateX().addChangeListener((type, oldValue, newValue) -> this.updateVisibility());
    this.translateY().addChangeListener((type, oldValue, newValue) -> this.updateVisibility());
  }

  public GridFeedWidget(BiPredicate<GridFeedWidget, Consumer<Widget>> refresh) {
    this(refresh, new ListSession<>());
  }

  @Override
  public void onBoundsChanged(Rectangle previousRect, Rectangle newRect) {
    super.onBoundsChanged(previousRect, newRect);

    // Update the bounds of the entries
    this.updateGrid();
  }

  @Override
  public float getContentHeight(BoundsType type) {
    int tilesPerLine = this.tilesPerLine.get();
    int lines = MathHelper.ceil((float) this.children.size() / (float) tilesPerLine);
    return this.getTileHeight() * lines + this.spaceBetweenEntries.get() * (lines - 1);
  }

  @Override
  public void updateVisibility(ListWidget<?> list, Parent parent) {
    super.updateVisibility(list, parent);
    if (this.parent == null) {
      return;
    }

    Bounds parentBounds = parent.bounds();
    float translateY = list.getTranslateY();

    // Update visibility
    for (Widget child : this.children) {
      if (child.isDragging()) {
        child.setVisible(true);
        continue;
      }

      Bounds tileBounds = child.bounds();
      boolean isInRectangle = parentBounds.isYInRectangle(tileBounds.getY() + translateY)
          || parentBounds.isYInRectangle(tileBounds.getY() + tileBounds.getHeight() + translateY);

      child.setVisible(isInRectangle);
    }

    if (!(parent instanceof ScrollWidget)) {
      return;
    }

    if (this.doRefresh && this.bounds().getHeight() > 0 && this.session.getScrollPositionY()
        >= this.bounds().getHeight() - parentBounds.getHeight() - (
        this.refreshRadius.isDefaultValue() ? this.getTileHeight() * 2
            : this.refreshRadius.get())) {
      this.doRefresh = false;
      boolean update = this.refresh.test(this, widget -> {
        this.addChildInitialized(widget);
      });

      if (update) {
        this.updateGrid();
        this.doRefresh = true;
      }
    }
  }

  public float getSpaceAvailable() {
    float spaceBetween = this.spaceBetweenEntries.get();
    Bounds bounds = this.bounds();
    if (spaceBetween == 0) {
      return bounds.getWidth();
    }

    float width = bounds.getWidth(INNER);
    return width - (spaceBetween * (this.tilesPerLine.get() - 1));
  }

  public float getTileWidth() {
    return this.getSpaceAvailable() / this.tilesPerLine.get();
  }

  public float getTileHeight() {
    return this.tileHeight.get() < 0 ? this.getTileWidth() : this.tileHeight.get();
  }

  public LssProperty<Float> spaceBetweenEntries() {
    return this.spaceBetweenEntries;
  }

  public LssProperty<Integer> tilesPerLine() {
    return this.tilesPerLine;
  }

  public LssProperty<Float> tileHeight() {
    return this.tileHeight;
  }

  public LssProperty<Float> refreshRadius() {
    return this.refreshRadius;
  }

  public ListSession<?> getSession() {
    return this.session;
  }

  public void doRefresh(boolean doRefresh) {
    this.doRefresh = doRefresh;
  }

  protected void updateGrid() {
    float gridTileWidth = this.getTileWidth();
    float gridTileHeight = this.getTileHeight();
    int indexX = 0;
    int indexY = 0;

    for (Widget child : this.children) {
      Bounds bounds = child.bounds();
      float spaceBetweenEntries = this.spaceBetweenEntries.get();
      bounds.setOuterPosition(
          this.bounds().getX() + indexX * (gridTileWidth + spaceBetweenEntries),
          this.bounds().getY() + indexY * (gridTileHeight + spaceBetweenEntries),
          ENTRY_BOUNDS
      );

      bounds.setOuterSize(gridTileWidth, gridTileHeight, ENTRY_BOUNDS);

      indexX += 1;
      if (indexX >= this.tilesPerLine.get()) {
        indexX = 0;
        indexY += 1;
      }
    }

    this.updateVisibility();
  }

  private void updateVisibility() {
    this.updateVisibility(this, this.parent);
  }
}

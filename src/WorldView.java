import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView
{
    private PApplet screen;
    private WorldModel world;
    private int tileWidth;
    private int tileHeight;
    private Viewport viewport;

    public WorldView(
            int numRows,
            int numCols,
            PApplet screen,
            WorldModel world,
            int tileWidth,
            int tileHeight)
    {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.setViewport(new Viewport(numRows, numCols));
    }

    public void shiftView(int colDelta, int rowDelta) {
        int newCol = Functions.clamp(this.getViewport().getCol() + colDelta, 0,
                this.world.getNumCols() - this.getViewport().getNumCols());
        int newRow = Functions.clamp(this.getViewport().getRow() + rowDelta, 0,
                this.world.getNumRows() - this.getViewport().getNumRows());

        this.getViewport().shift(newCol, newRow);
    }

    private void drawEntities() {
        for (Entity entity : this.world.getEntities()) {
            Point pos = entity.getPosition();

            if (this.getViewport().contains(pos)) {
                Point viewPoint = this.getViewport().worldToViewport(pos.getX(), pos.getY());
                this.screen.image(entity.getCurrentImage(),
                        viewPoint.getX() * this.tileWidth,
                        viewPoint.getY() * this.tileHeight);
            }
        }
    }

    public void drawViewport() {
        this.drawBackground();
        this.drawEntities();
    }

    private void drawBackground() {
        for (int row = 0; row < this.getViewport().getNumRows(); row++) {
            for (int col = 0; col < this.getViewport().getNumCols(); col++) {
                Point worldPoint = this.getViewport().viewportToWorld(col, row);
                Optional<PImage> image =
                        world.getBackgroundImage(this.world, worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth,
                            row * this.tileHeight);
                }
            }
        }
    }

    public Viewport getViewport() {
        return viewport;
    }

    private void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}

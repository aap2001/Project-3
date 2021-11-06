import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background background[][];
    private Entity occupancy[][];
    private Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.setNumRows(numRows);
        this.setNumCols(numCols);
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.setEntities(new HashSet<>());

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.getEntities().add(entity);
        }
    }

    private boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.getNumRows() && pos.getX() >= 0
                && pos.getX() < this.getNumCols();
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    private void setBackgroundCell(
            Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public void setBackground(
            Point pos, Background background) {
        if (this.withinBounds(pos)) {
            this.setBackgroundCell(pos, background);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    private void setOccupancyCell(
            Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    private Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (world.withinBounds(pos)) {
            return Optional.of(world.getBackgroundCell(pos).getCurrentImage());
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<Entity> findNearest(
            Point pos, List<EntityKind> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (EntityKind kind : kinds) {
            for (Entity entity : this.getEntities()) {
                if (entity.getKind() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return Functions.nearestEntity(ofType, pos);
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.getPosition());
    }

    private void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.getEntities().remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            this.removeEntityAt(pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public int getNumRows() {
        return numRows;
    }

    private void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    private void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    private void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }
}

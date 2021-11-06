import java.util.List;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background
{
    private String id;
    private List<PImage> images;
    private int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.setImages(images);
    }

    public PImage getCurrentImage() {
            return (this).getImages().get(
                    (this).getImageIndex());

    }

    private List<PImage> getImages() {
        return images;
    }

    private void setImages(List<PImage> images) {
        this.images = images;
    }

    private int getImageIndex() {
        return imageIndex;
    }

    private void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }
}

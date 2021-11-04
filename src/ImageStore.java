import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore
{
    private Map<String, List<PImage>> images;
    private List<PImage> defaultImages;

    public ImageStore(PImage defaultImage) {
        this.setImages(new HashMap<>());
        setDefaultImages(new LinkedList<>());
        getDefaultImages().add(defaultImage);
    }

    public Map<String, List<PImage>> getImages() {
        return images;
    }

    private void setImages(Map<String, List<PImage>> images) {
        this.images = images;
    }

    public List<PImage> getDefaultImages() {
        return defaultImages;
    }

    private void setDefaultImages(List<PImage> defaultImages) {
        this.defaultImages = defaultImages;
    }
}

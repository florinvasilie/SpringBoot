package app.server.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fvasilie on 11/1/2016.
 */
public class ImagesStored {
    private List<BufferedImage> images = new ArrayList<BufferedImage>();
    private List<String> paths = new ArrayList<String>();

    public List<BufferedImage> getImages() {
        return images;
    }
    public void setImages(List<BufferedImage> images) {
        this.images = images;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}

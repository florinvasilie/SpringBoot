package app.server.model;

import java.util.List;

/**
 * Created by fvasilie on 11/4/2016.
 */
public class Region {
    public String boundingBox;
    public List<Line> lines;

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
}

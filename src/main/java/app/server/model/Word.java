package app.server.model;

/**
 * Created by fvasilie on 11/4/2016.
 */
public class Word {
    public String boundingBox;

    public String text;

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

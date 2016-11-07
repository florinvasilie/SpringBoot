package app.server.model;

import java.util.List;

/**
 * Created by fvasilie on 11/4/2016.
 */
public class Line {

    public boolean isVertical;

    public List<Word> words;

    public String boundingBox;

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }
}

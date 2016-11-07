package app.server.model;

import java.util.List;

/**
 * Created by fvasilie on 11/4/2016.
 */
public class OCR {
    public boolean isAngleDetected;

    public float textAngle;

    public String orientation;

    public String language;

    public List<Region> regions;

    public boolean isAngleDetected() {
        return isAngleDetected;
    }

    public void setAngleDetected(boolean angleDetected) {
        isAngleDetected = angleDetected;
    }

    public float getTextAngle() {
        return textAngle;
    }

    public void setTextAngle(float textAngle) {
        this.textAngle = textAngle;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}

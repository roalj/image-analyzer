package entities;

import com.amazonaws.services.rekognition.model.Label;

import java.util.List;

public class AnalysisEntity {
    private Integer imageId;
    private Integer numberOfFaces;
    private List<String> celebrities;
    private List<Label> scenes;

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getNumberOfFaces() {
        return numberOfFaces;
    }

    public void setNumberOfFaces(Integer numberOfFaces) {
        this.numberOfFaces = numberOfFaces;
    }

    public List<String> getCelebrities() {
        return celebrities;
    }

    public void setCelebrities(List<String> celebrities) {
        this.celebrities = celebrities;
    }

    public List<Label> getScenes() {
        return scenes;
    }

    public void setScenes(List<Label> scenes) {
        this.scenes = scenes;
    }
}

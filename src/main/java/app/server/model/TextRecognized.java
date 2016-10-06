package app.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by fvasilie on 9/26/2016.
 */
public class TextRecognized extends ResourceSupport {

    private String text;
    private String userName;

    @JsonCreator
    public TextRecognized(@JsonProperty("text") String text, @JsonProperty("userName") String userName){

        this.text = text;
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }
}

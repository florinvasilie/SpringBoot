package app.client.controller;

import app.server.model.TextRecognized;
import app.server.tools.FileTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by fvasilie on 9/26/2016.
 */

@Controller
public class ClientController {

    @Autowired
    FileTool fileTool;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String loadPage(){

        return "uploadForm";
    }
}

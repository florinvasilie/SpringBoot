package app.client.controller;

import app.server.tools.FileTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by fvasilie on 9/26/2016.
 */
@Controller
public class FormController {

    @Autowired
    FileTool fileTool;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String loadPage(){

        return "uploadForm";
    }
}

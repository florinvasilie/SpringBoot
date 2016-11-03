package app.server.controller;

import app.server.model.ImagesStored;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by fvasilie on 11/2/2016.
 */

@Controller
public class PdfFileController {



    @RequestMapping(value = "/pdffiles", method = RequestMethod.GET)
    public ModelAndView viewAllFiles(@ModelAttribute("imageStored") ImagesStored imagesStored){


        return new ModelAndView("pdfFiles","imageStored",imagesStored);
    }

}

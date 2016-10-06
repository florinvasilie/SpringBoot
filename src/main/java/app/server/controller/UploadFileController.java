package app.server.controller;

import app.server.model.TextRecognized;
import app.server.service.ImageProcessing;
import app.server.tools.FileTool;
import app.server.tools.ImageTool;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by fvasilie on 9/19/2016.
 */
@RestController
public class UploadFileController {

    @Autowired
    private ImageTool imageTool;

    @Autowired
    private ImageProcessing imageProcessing;

    @Autowired
    private FileTool fileTool;


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public HttpEntity<TextRecognized> textRecognizedHttpEntity(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {


        fileTool.createDirIfDoesNotExist();
        String imageExtension = imageTool.ifValidExtensionReturnExtension(file.getOriginalFilename());

        if (imageExtension.isEmpty())
            return new ResponseEntity<TextRecognized> (new TextRecognized("Format neacceptat!","Florin"), HttpStatus.OK);
        else imageTool.setImageExtension(imageExtension);

        System.out.println(imageExtension);
        BufferedImage imageFile = ImageIO.read(file.getInputStream());

        imageTool.setImageName("OriginalPhoto");
        System.out.println(imageFile);
        BufferedImage kmeans = imageProcessing.kmeansPython(imageFile);


//        ProcessBuilder builder = new ProcessBuilder("C:\\Users\\fvasilie\\AppData\\Local\\Continuum\\Anaconda3\\python.exe",);

        BufferedImage imageFileResized = imageTool.resizeIfNecessary(kmeans,1024,1024);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat matImage, grayScale, gaussianBlur, adaptiveThreshold, finalImage;

        matImage = imageTool.BufferedImageToMat(imageFileResized);
        grayScale = imageProcessing.grayScale(matImage);
        gaussianBlur = imageProcessing.gaussianBlur(grayScale,3);
        adaptiveThreshold = imageProcessing.adaptiveThreshold(gaussianBlur);

        double angle = imageProcessing.findAngleLines(adaptiveThreshold);
        finalImage = imageProcessing.imageRotation(adaptiveThreshold, angle);
        imageTool.setImageName("PhotoForOCR");
        String result = imageProcessing.getTextFromImage(finalImage,"ron");


        System.out.println(result);
        result = result.replaceAll("(\r\n|\n)", " <br> ");
        TextRecognized textRecognized = new TextRecognized(result,"Florin"); //luat nume din sesiune


    //    textRecognized.add(linkTo(methodOn(UploadFileController.class).textRecognizedHttpEntity(file)).withSelfRel());


//

        return new ResponseEntity<TextRecognized>(textRecognized, HttpStatus.OK);
//        model.addAttribute("message",result);


    }

}

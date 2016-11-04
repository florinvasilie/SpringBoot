package app.server.controller;

import app.server.model.ImagesStored;
import app.server.service.ImageProcessing;
import app.server.tools.FileTool;
import app.server.tools.ImageTool;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.List;


/**
 * Created by fvasilie on 9/19/2016.
 */
@Controller
public class UploadFileController {

    @Autowired
    private ImageTool imageTool;

    @Autowired
    private ImageProcessing imageProcessing;

    @Autowired
    private FileTool fileTool;


//    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces="application/json")
//    public HttpEntity<TextRecognized> textRecognizedHttpEntity(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        fileTool.createDirIfDoesNotExist();
//        String imageExtension = imageTool.ifValidExtensionReturnExtension(file.getOriginalFilename());
//
//        if (imageExtension.isEmpty())
//            return new ResponseEntity<TextRecognized> (new TextRecognized("Format neacceptat!","Florin"), HttpStatus.OK);
//        else imageTool.setImageExtension(imageExtension);
//
//        List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
//        if (imageTool.getImageExtension().equals("PDF")) {
//            bufferedImages = fileTool.pdfToImage(imageTool.getImageExtension(), file.getInputStream());
//
//            return null;
//        }
////        PDDocument document = PDDocument.load(file.getInputStream());
//
//        BufferedImage imageFile = ImageIO.read(file.getInputStream());
//        Mat image = imageTool.BufferedImageToMat(imageFile);
//        List<Mat> textRegions = imageProcessing.detectTextAreas(image);
//        String result = imageProcessing.getTextFromImage(textRegions,"ron");
//
//        System.out.println(result);
//        result = result.replaceAll("(\r\n|\n)", " <br> ");
//        TextRecognized textRecognized = new TextRecognized(result,"Florin"); //luat nume din sesiune
//
//        return new ResponseEntity<TextRecognized>(textRecognized, HttpStatus.OK);
//    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView uploadFile (@RequestParam("file") MultipartFile multipartFile, Model model){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        fileTool.setDirName("files\\");
        fileTool.createDirIfDoesNotExist();
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

        String result ="";
        if (!imageTool.checkExtension(multipartFile.getOriginalFilename()))
             model.addAttribute("error","Format neacceptat!");
        else {
            if (imageTool.getImageExtension().toUpperCase().equals("pdf".toUpperCase())){
                try {
                    List<BufferedImage> bufferedImages = fileTool.pdfToImage("JPG",multipartFile.getInputStream());
                    fileTool.setDirName("files\\PdfFile\\");
                    fileTool.createDirIfDoesNotExist();
                    for (int i=0; i<bufferedImages.size(); i++){
                        ImageIO.write(bufferedImages.get(i),"JPG", new File( fileTool.getNewDirPath() + "\\PDF"+ i +".jpg"));
                    }
                    ImagesStored imagesStored = new ImagesStored();
                    imagesStored.setImages(bufferedImages);

                    return new ModelAndView("redirect:/pdffiles","imagesStored",imagesStored);
//                    return new ModelAndView("imagesFromPdf", "imagesStored", "Pdf file");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            BufferedImage imageFile =ImageIO.read(multipartFile.getInputStream());
            System.out.println(imageFile.getHeight()+ " ----------------  " + imageFile.getWidth());


//            int newImageWidth = imageFile.getWidth() * 2;
//            int newImageHeight = imageFile.getHeight() * 2;
//            BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, imageFile.getType());
//            Graphics2D g = resizedImage.createGraphics();
//            g.drawImage(imageFile, 0, 0, newImageWidth , newImageHeight , null);
//            g.dispose();
//
//
//            System.out.println(resizedImage.getHeight()+ " ----------------  " + resizedImage.getWidth());
//            ImageIO.write(resizedImage,"JPG", new File("C:\\Users\\fvasilie\\Desktop\\resizedImage.jpg"));



            Mat matImage = imageTool.BufferedImageToMat(imageFile);
            List<Mat> images = imageProcessing.detectTextAreas(matImage);
            System.out.println(images.size());
//            if (true) return new ModelAndView("uploadOk","message",result);
            for (int i=0; i<images.size();i++){

                BufferedImage kmeans = imageProcessing.kmeansPython(images.get(i),i);
                BufferedImage imageFileResized = imageTool.resize(kmeans, kmeans.getType(),(int)(kmeans.getWidth()*1.8),(int)(kmeans.getHeight()*1.8),1.8,1.8);

                Mat matImage1, grayScale, gaussianBlur, adaptiveThreshold, finalImage;

                matImage1 = imageTool.BufferedImageToMat(imageFileResized);
                grayScale = imageProcessing.grayScale(matImage1);
                gaussianBlur = imageProcessing.gaussianBlur(grayScale,3);
                adaptiveThreshold = imageProcessing.adaptiveThreshold(gaussianBlur);

//                double angle = imageProcessing.findAngleLines(adaptiveThreshold);
//                finalImage = imageProcessing.imageRotation(adaptiveThreshold, angle);

                imageTool.setImageName("PhotoForOCR"+i);
                fileTool.saveImage(adaptiveThreshold,imageTool.getImageName()+ "." + imageTool.getImageExtension(),fileTool.getNewDirPath());
                BufferedImage imgForTesseract = null;

//                try {
//                    imgForTesseract = ImageIO.read(new File(fileTool.getNewDirPath() + imageTool.getImageName() + "." + imageTool.getImageExtension()));
//
//
//                }catch (IOException e) {}
//
//                Tesseract instance = new Tesseract();
//                instance.setLanguage("ron");
//
//                try {
//                    result += instance.doOCR(imgForTesseract);
//                } catch (TesseractException e) {
//                    System.err.println(e.getMessage());
//                }

                result +="\n";
                System.out.println(i);
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (result.isEmpty())
            result = "Eroare recunoastere text!";
        else
            result = result.replaceAll("(\r\n|\n)", " <br> ");

        return new ModelAndView("uploadOk","message",result);
    }
}

package app.server.service;

import app.server.tools.FileTool;
import app.server.tools.ImageTool;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 * Created by fvasilie on 9/21/2016.
 */
@Service
public class ImageProcessing {

    @Autowired
    private ImageTool imageTool;

    @Autowired
    private FileTool fileTool;

    public BufferedImage kmeansPython(BufferedImage imageFile){
        System.out.println("in functie "+ imageFile);

        try {
            ImageIO.write(imageFile,imageTool.getImageExtension(),new File(fileTool.getNewDirPath()+imageTool.getImageName()+"."+imageTool.getImageExtension()));
//            ImageIO.write(imageFile,"JPG",new File("C:\\Users\\fvasilie\\Desktop\\diacriticeMIN.jpg"));
            System.out.println("Salvare poza originala");
            System.out.println((fileTool.getNewDirPath()+imageTool.getImageName())+imageTool.getImageExtension());
        } catch (IOException e) {
            System.out.println("Nu a salvat original PHOTO");
            e.printStackTrace();
        }
//        fileTool.saveImage();

        System.out.println("--- Process ---");
        Process exec = null;
        try {

            System.out.println(fileTool.getProjectPath() + "python\\color_cuantization.py");

            System.out.println(fileTool.getNewDirPath() + imageTool.getImageName() + imageTool.getImageExtension());

            System.out.println(fileTool.getNewDirPath() + "kmeansPhoto" + imageTool.getImageExtension());


            exec = Runtime.getRuntime().exec(new String[] { "C:\\Users\\fvasilie\\AppData\\Local\\Continuum\\Anaconda3\\python.exe", fileTool.getProjectPath() + "python\\color_cuantization.py",
                    fileTool.getNewDirPath() + imageTool.getImageName() + "." + imageTool.getImageExtension(), fileTool.getNewDirPath() + "kmeansPhoto" + "." + imageTool.getImageExtension()});
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            exec.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--- EndProcess ---");

        BufferedImage kmeansImage = null;

        try {
            kmeansImage = ImageIO.read(new File(fileTool.getNewDirPath() + "kmeansPhoto" + "." + imageTool.getImageExtension()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kmeansImage;

    }

    public Mat grayScale(Mat imageFile){
        Imgproc.cvtColor(imageFile,imageFile, Imgproc.COLOR_RGB2GRAY);

        System.out.println("GrayScale         OK");
        return imageFile;
    }

    //applies after grayScale
    public Mat gaussianBlur(Mat imageFile, int steps){
        Imgproc.GaussianBlur(imageFile,imageFile,new Size(steps,steps),0);
        System.out.println("Blur              OK");
        return imageFile;
    }

    //applies after gaussianBlur
    public Mat adaptiveThreshold(Mat imageFile){
        Imgproc.adaptiveThreshold(imageFile,imageFile,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,33,11);
        System.out.println("Thresh            OK");
        return imageFile;
    }

    //applies after adaptiveThreshold
    public double findAngleLines(Mat source){

        Size size = source.size();
        Core.bitwise_not(source, source);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(source, lines, 1, Math.PI / 180, 100, size.width / 2.f, 20);
        double angle = 0.;
        for(int i = 0; i<lines.height(); i++){
            for(int j = 0; j<lines.width();j++){
                angle += Math.atan2(lines.get(i, j)[3] - lines.get(i, j)[1], lines.get(i, j)[2] - lines.get(i, j)[0]);

            }
        }
        angle /= lines.size().area();
        angle = angle * 180 / Math.PI;
        System.out.println("Angle             OK"+angle);

        return angle;
    }

    public Mat imageRotation (Mat imageFile, double angle){

//        Core.bitwise_not(imageFile,imageFile);
        Point center = new Point(imageFile.width()/2, imageFile.height()/2);

        Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        Size size = new Size(imageFile.width(), imageFile.height());

        Imgproc.warpAffine(imageFile, imageFile, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        Core.bitwise_not(imageFile, imageFile);
        System.out.println("Rotation          OK"+angle);

        return imageFile;
    }


    public Mat openMatImage(String path, String imageName){
        Mat imageFile = Imgcodecs.imread(path+imageName);

        return imageFile;
    }
    public String getTextFromImage(Mat imageFile, String language){

        fileTool.saveImage(imageFile,imageTool.getImageName()+ "." + imageTool.getImageExtension(),fileTool.getNewDirPath());
        BufferedImage finalImage = null;

        try {
            finalImage = ImageIO.read(new File(fileTool.getNewDirPath() + imageTool.getImageName() + "." + imageTool.getImageExtension()));

        }catch (IOException e) {}

        ITesseract instance = new Tesseract();
        instance.setLanguage(language);
        String result="";
        try {
            result = instance.doOCR(finalImage);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }

        if (result.isEmpty()) return "Eroare! Va rugam reincercati!";

        return result;
    }


}

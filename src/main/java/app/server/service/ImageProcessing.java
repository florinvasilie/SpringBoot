package app.server.service;

import app.server.tools.FileTool;
import app.server.tools.ImageTool;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public BufferedImage kmeansPython(Mat imageFile, int contor ){

        imageTool.setImageName("OriginalPhoto" + contor);
        System.out.println("EXTENSIE");
        System.out.println(imageTool.getImageExtension());
        System.out.println(fileTool.getNewDirPath()+imageTool.getImageName() +"."+imageTool.getImageExtension());
        System.out.println();

        Imgcodecs.imwrite(fileTool.getNewDirPath()+imageTool.getImageName() +"."+imageTool.getImageExtension(), imageFile);

        System.out.println("--- Process ---");
        Process exec = null;
        try {

            System.out.println(fileTool.getProjectPath() + "python\\color_cuantization.py");

            System.out.println(fileTool.getNewDirPath() + imageTool.getImageName() +  imageTool.getImageExtension());

            exec = Runtime.getRuntime().exec(new String[] { "C:\\Users\\fvasilie\\AppData\\Local\\Continuum\\Anaconda3\\python.exe", fileTool.getProjectPath() + "python\\color_cuantization.py",
                    fileTool.getNewDirPath() + imageTool.getImageName() + "." + imageTool.getImageExtension(), fileTool.getNewDirPath() + "kmeansPhoto" + contor +"." + imageTool.getImageExtension()});
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
            kmeansImage = ImageIO.read(new File(fileTool.getNewDirPath() + "kmeansPhoto"+ contor + "." + imageTool.getImageExtension()));
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

    public List<Mat> detectTextAreas(Mat imageFile){

        List<Mat> regions = new ArrayList<Mat>();
        Mat originalImage = imageFile.clone();
        Mat imageCountours = imageFile.clone();
        Mat img2gray = new Mat();
        Mat mask = new Mat();
        Mat dilated = new Mat();
        Mat kernel = new Mat();
        Mat hierarchy = new Mat();

        Imgproc.cvtColor(imageFile,img2gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(img2gray,mask,180,255,Imgproc.THRESH_BINARY_INV);
        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
        Imgproc.dilate(mask,dilated,kernel, new Point(),9);

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(dilated, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        for (MatOfPoint contour : contours) {

            Rect rec = Imgproc.boundingRect(contour);
            if (rec.width < 55  || rec.height < 55 )
                continue;

            Point pt1 = new Point(rec.x + 18 , rec.y + 18);
            Point pt2 = new Point(rec.x + rec.width - 1, rec.y + rec.height - 1);
            Scalar color = new Scalar(255,0,255);
            Imgproc.rectangle(imageCountours,pt1,pt2,color,2);

            rec.x = rec.x + 21;
            rec.y = rec.y + 21;
            rec.width = rec.width - 24;
            rec.height = rec.height - 24;
            Mat result = originalImage.submat(rec);


            regions.add(result);
        }
        Imgcodecs.imwrite("C:\\Users\\fvasilie\\Desktop\\contours.jpg", imageCountours);

        return regions;
    }

    public String getTextFromImage(List<Mat> textRegions, String language){
        String result = "";

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        for(int i=0; i< textRegions.size(); i++){


//            BufferedImage kmeans = kmeansPython(imageTool.MatToBufferedImage(textRegions.get(i)));
//            BufferedImage imageFileResized = imageTool.resize(kmeans, kmeans.getType(),(int)(kmeans.getWidth()*1.8),(int)(kmeans.getHeight()*1.8),1.8,1.8);


            Mat matImage, grayScale, gaussianBlur, adaptiveThreshold, finalImage;

//            matImage = imageTool.BufferedImageToMat(textRegions.get(i));
            grayScale = grayScale(textRegions.get(i));
//            gaussianBlur = gaussianBlur(grayScale,3);
            adaptiveThreshold = adaptiveThreshold(grayScale);
//            double angle = findAngleLines(adaptiveThreshold);
//            finalImage = imageRotation(adaptiveThreshold, angle);

            imageTool.setImageName("PhotoForOCR"+i);
            fileTool.saveImage(adaptiveThreshold,imageTool.getImageName()+ "." + imageTool.getImageExtension(),fileTool.getNewDirPath());
            BufferedImage imgForTesseract = null;

            try {
                imgForTesseract = ImageIO.read(new File(fileTool.getNewDirPath() + imageTool.getImageName() + "." + imageTool.getImageExtension()));

            }catch (IOException e) {}

            Tesseract instance = new Tesseract();
            instance.setLanguage(language);

            try {
                result += instance.doOCR(imgForTesseract);
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }

            if (result.isEmpty()) return "Eroare! Va rugam reincercati!"+imageTool.getImageName();
            result +="\n\n";
        }
        return result;
    }


}

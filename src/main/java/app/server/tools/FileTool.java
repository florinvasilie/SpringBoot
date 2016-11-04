package app.server.tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fvasilie on 9/28/2016.
 */

@Service
public class FileTool {

    private String projectPath = System.getProperty("user.dir")+"\\src\\main\\resources\\";
    private String dirName="";
    private String newDirPath="";

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName){
        this.dirName = dirName;
    }

    public String getProjectPath() {
        return this.projectPath;
    }

    public String getNewDirPath() {
        return this.newDirPath = this.projectPath + this.dirName;
    }

    public void saveImage(Mat imageFile, String imageName, String path){
        Imgcodecs.imwrite(path+imageName,imageFile);
    }

    public Boolean createDirIfDoesNotExist(){
        Boolean createDir = false;
        File newDir = new File(this.projectPath + this.dirName);

        if (!newDir.exists()){
            try{
                newDir.mkdir();
                createDir = true;
            }catch (SecurityException se){}
        }

        return createDir;
    }

    public List<BufferedImage> pdfToImage (String extension, InputStream file) throws IOException {
        List<BufferedImage> imagesFromPDF = new ArrayList<BufferedImage>();
        PDDocument document = PDDocument.load(file);

        PDFRenderer pdfRenderer = new PDFRenderer(document);

        for (int page = 0; page < document.getNumberOfPages(); page++){

            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
//            ImageIO.write(bufferedImage,"JPG", new File("C:\\Users\\fvasilie\\Desktop\\PDF"+page+".jpg"));
            imagesFromPDF.add(bufferedImage);
        }

        return imagesFromPDF;
    }
}

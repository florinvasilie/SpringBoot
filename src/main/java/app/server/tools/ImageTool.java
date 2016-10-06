package app.server.tools;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by fvasilie on 9/21/2016.
 */
@Service
public class ImageTool {

    private enum validTypes{
        TIFF,       JPEG,       GIF,        PNG,

        BMP,        PDF,        JPG

    }
    private String imageName;
    private String imageExtension;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }

    public String ifValidExtensionReturnExtension(String path){
        System.out.println(path);
        for (validTypes type: validTypes.values()){
            if(path.toUpperCase().matches("^.*\\." + type.name() + "$")) return type.name().toString();
        }
        return "";
    }

    public Mat BufferedImageToMat (BufferedImage bufferedImage){

        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);

        return mat;
    }

    public BufferedImage MatToBufferedImage (Mat in){

        BufferedImage out;
        byte[] data = new byte[320 * 240 * (int)in.elemSize()];
        int type;
        in.get(0, 0, data);

        if(in.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else
            type = BufferedImage.TYPE_3BYTE_BGR;

        out = new BufferedImage(320, 240, type);

        out.getRaster().setDataElements(0, 0, 320, 240, data);

        return out;
    }

    public BufferedImage resize (BufferedImage inputImage, int imageType, int outWidth, int outHeight, double scaleWidth, double scaleHeight){
        BufferedImage outImage = null;
        if(inputImage != null) {
            outImage = new BufferedImage(outWidth, outHeight, imageType);
            Graphics2D g = outImage.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(scaleWidth, scaleHeight);
            g.drawRenderedImage(inputImage, at);
        }
        return outImage;
    }

    public BufferedImage resizeIfNecessary (BufferedImage imageFile, int width, int height){

//        if (imageFile.getWidth() < width || imageFile.getHeight() < height)
            imageFile = resize(imageFile, imageFile.getType(),(int)(imageFile.getWidth()*1.8),(int)(imageFile.getHeight()*1.8),1.8,1.8);

        return imageFile;
    }
}

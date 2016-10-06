package app.server.tools;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by fvasilie on 9/28/2016.
 */

@Service
public class FileTool {

    private String projectPath = System.getProperty("user.dir")+"\\src\\main\\resources\\";
    private String dirName = "files\\";
    private String newDirPath = projectPath + dirName;

    public String getDirName() {
        return dirName;
    }
    public String getProjectPath() {
        return projectPath;
    }

    public String getNewDirPath() {
        return newDirPath;
    }

    public void saveImage(Mat imageFile, String imageName, String path){
        Imgcodecs.imwrite(path+imageName,imageFile);
    }

    public Boolean createDirIfDoesNotExist(){
        Boolean createDir = false;
        File newDir = new File(projectPath+dirName);

        if (!newDir.exists()){
            try{
                newDir.mkdir();
                createDir = true;
            }catch (SecurityException se){}
        }

        return createDir;
    }
}

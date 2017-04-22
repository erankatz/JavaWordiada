package engine.exception.file;

import java.io.EOFException;
import java.nio.channels.FileLockInterruptionException;

/**
 * Created by eran on 22/04/2017.
 */

public class FileException extends java.io.IOException {
    private String fileName;
    private Exception exception;

    public FileException(String Filename,Exception ex){
        fileName = Filename;
        exception =ex;
    }

    @Override
    public String getMessage(){
        if (exception instanceof NoSuchFieldException)
            return ("File " + fileName + " Not exists");
        else if (exception instanceof FileLockInterruptionException)
            return "File " + fileName +" is locked by another process";
        else if (exception instanceof EOFException){
            return "File " + fileName +" is corrupted";
        } else {
            return "File Error:" + fileName;
        }
    }
}

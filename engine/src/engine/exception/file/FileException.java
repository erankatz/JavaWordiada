package engine.exception.file;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.nio.channels.FileLockInterruptionException;
import java.nio.file.NoSuchFileException;

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
        if (exception instanceof NoSuchFileException || exception instanceof FileNotFoundException)
            return ("File " + fileName + " Not exists");
        else if (exception instanceof FileLockInterruptionException) {
            return "File " + fileName + " is locked by another process";
        } else if (exception instanceof EOFException){
            return "File " + fileName +" is corrupted";
        }
            return "File Error:" + fileName;
    }
}


package engine.exception.file;

import engine.exception.EngineException;

/**
 * Created by eran on 29/04/2017.
 */
public class FileExtensionException extends EngineException {

    private String fileName;

    public FileExtensionException(String Filename){
        fileName = Filename;

    }

    @Override
    public String getMessage(){

            return "The extension of the given XML File is not valid: \n";
    }
}

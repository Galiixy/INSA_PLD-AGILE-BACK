package insa.lyon.justif.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Utils {

    /**
     * saveUploadedFile : Method to compute save an uploaded file from the web client.
     *
     * @param file The multipart file uploaded to save. (required)
     * @param destinationDirectory The destination directory where to save the uploaded file. (required)
     */
    public static void saveUploadedFile(MultipartFile file, String destinationDirectory) throws IOException {
        File f = new File(destinationDirectory);
        if (!f.exists()) {
            if (!f.mkdir()) throw new IOException("Files directory can't be created.");
        }
        byte[] bytes = file.getBytes();
        Path path = Paths.get(destinationDirectory + file.getOriginalFilename());
        Files.write(path, bytes);
    }

}

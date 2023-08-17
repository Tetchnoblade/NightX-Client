package net.aspw.client.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The type File utils.
 */
public class FileUtils {
    /**
     * Unpack file.
     *
     * @param file the file
     * @param name the name
     * @throws IOException the io exception
     */
    public static void unpackFile(File file, String name) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        IOUtils.copy(FileUtils.class.getClassLoader().getResourceAsStream(name), fos);
        fos.close();
    }
}

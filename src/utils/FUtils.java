/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author steph
 */
public class FUtils {

    public static boolean isJavaFile(String path) {

        int i = path.lastIndexOf('.');
        int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (i > p) {
            if (path.substring(i + 1).equals("java")) {
                return true;
            }
        }
        return false;
    }

    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    public static List<String> listFilesForFolder(final File folder) {
        List<String> tmp = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                try {
                    tmp.add(fileEntry.getAbsolutePath());
                    tmp.addAll(listFilesForFolder(fileEntry));
                } catch (Exception e) {
                }
            } else {
                try {

                    tmp.add(fileEntry.getAbsolutePath());
                } catch (Exception e) {
                }
            }
        }
        return tmp;
    }

    public static List<String> listFilesinFolder(final File folder) {
        List<String> tmp = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                tmp.add(fileEntry.getAbsolutePath());
            }
        }
        return tmp;
    }
}

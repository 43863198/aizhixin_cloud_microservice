package com.aizhixin.test.example;

import java.io.File;
import java.util.*;

public class FileUtil {
    public static Map<String, String> readUsernameAndPwdFromFile(String file) {
        Map<String, String> r = new HashMap<>();
        File f = new File(file);
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.BufferedReader(new java.io.FileReader(f)));
            String line = in.readLine();
            line = in.readLine();
            int p = 0;
            while (null != line) {
                p = line.indexOf(",");
                if (p > 0) {
                    r.put(line.substring(0, p), line.substring(p + 1));
                }
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public static List<Long> readUserIdFromFile(String file) {
        List<Long> ids = new ArrayList<>();
        File f = new File(file);
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.BufferedReader(new java.io.FileReader(f)));
            String line = in.readLine();
            line = in.readLine();
            while (null != line) {
                ids.add(new Long(line));
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }
}

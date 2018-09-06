package com.aizhixin.cloud.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhen.pan on 2017/12/15.
 */
public abstract class TextFileTemplate<KEY, VALUE> {

    public abstract void processLine(String line, Map<KEY, VALUE> r);

    public Map<KEY, VALUE> readTextLineFromFile(String file, boolean ignoreFirstLine) {
        Map<KEY, VALUE> r = new HashMap<>();
        File f = new File(file);
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.BufferedReader(new java.io.FileReader(f)));
            String line = in.readLine();
            if (ignoreFirstLine) {
                line = in.readLine();
            }
            while (null != line) {
                processLine(line, r);
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return r;
    }
}
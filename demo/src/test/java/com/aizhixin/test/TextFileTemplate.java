package com.aizhixin.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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

    public void outFile(String file, List<String> list) {
        java.io.BufferedWriter out = null;

        try {
            out = new java.io.BufferedWriter(new java.io.FileWriter(file));
            for (String line : list) {
                out.write(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
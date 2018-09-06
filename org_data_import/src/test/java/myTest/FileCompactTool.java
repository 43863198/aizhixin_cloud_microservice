package myTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileCompactTool {
    public Map<String, String> readFileLineAll(String file) {
        Map<String, String> lineMap = new HashMap<>();
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader(file));
            String line = in.readLine();
            int p = 0;
            while (null != line) {
                line = line.trim();
                p = line.indexOf("\t");
                if (p > 0) {
                    lineMap.put(line.substring(0, p), line.substring(p + 1));
                }
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lineMap;
    }

    public void compact (String file1, String file2) {
        Map<String, String> map1 = readFileLineAll(file1);
        Map<String, String> map2 = readFileLineAll(file2);
        if (null != map1 && null != map2) {
            String temp = null;
            for (java.util.Map.Entry<String, String> e : map1.entrySet()) {
                if (!map2.keySet().contains(e.getKey())) {
                    System.out.println(file1 + "\t ++:" + e.getKey() + "\t" + e.getValue());
                } else {
                    temp = map2.get(e.getKey());
                    if (!e.getValue().equals(temp)) {
                        System.out.println("defrent from:"  + e.getKey() + "\t" + e.getValue() + "\t classcode:" + temp);
                    }
                }
            }
//            for (String line : set2) {
//                if (!set1.contains(line)) {
//                    System.out.println(file2 + "\t ++:" + line);
//                }
//            }
        }
    }

    public static void main(String[] args) {
        FileCompactTool compactTool = new FileCompactTool();
        compactTool.compact("d:/file1_cj.txt", "d:/file2_me.txt");
    }
}

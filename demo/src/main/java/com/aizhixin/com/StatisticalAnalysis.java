package com.aizhixin.com;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;


/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-04
 */
public class StatisticalAnalysis {

    public static void main(String[] args)
    {
        //传入参数为文件目录
        statistical("d:/metrics3.json","D:/b.txt");
    }

    public static void statistical(String readerPath, String printPateh){
        BufferedReader br = null;
        try {
            FileInputStream in = new FileInputStream (readerPath);
            br = new BufferedReader(new InputStreamReader (in,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
        List<Vector> dataList = new ArrayList();
        int index = 0;
        try {
            while((line = br.readLine()) != null) {
                JSONArray jsonArray = new JSONArray(line);
                Vector vector = null;
                String tyeName = null;
                long totalTime = 0;
                long startTime = 0;
                long endTime = 0;
                for (int k = 0; k < jsonArray.length(); k++) {
                    Map<String,Object> map = new HashMap<String, Object>();
                    //获取每一个JsonObject对象
                    JSONObject myjObject = jsonArray.getJSONObject(k);
                    String name = myjObject.getString("name");
                    int time = myjObject.getInt("time");

                    if(null == tyeName || !tyeName.equals(name) || k==jsonArray.length()-1){
                        if(null != tyeName) {
                            boolean isNew = true;
                            for(int x =0;x<mapList.size();x++){
                                if(String.valueOf(mapList.get(x).get("name")).equals(tyeName)){
                                    totalTime = Integer.valueOf(String.valueOf(mapList.get(x).get("totalTime")))+totalTime;
                                    mapList.get(x).remove("totalTime");
                                    mapList.get(x).remove("endTime");
                                    mapList.get(x).put("totalTime", totalTime);
                                    mapList.get(x).put("endTime", endTime);
                                    isNew = false;
                                    for(Object d :vector){
                                        dataList.get(x).add(d);
                                    }
                                }
                            }
                            if(isNew) {
                                map.put("name", tyeName);
                                map.put("startTime", startTime);
                                map.put("endTime", endTime);
                                map.put("totalTime", totalTime);
                                mapList.add(index, map);
                                dataList.add(index, vector);
                                index++;
                            }
                        }
                        vector = new Vector();
                        tyeName = name;
                        startTime = myjObject.getInt("starttime");
                        totalTime = 0L;
                    }
                    endTime = myjObject.getInt("endtime");
                    vector.add(time);
                    totalTime = totalTime + time;
                }
            }
            br.close();

            //快速排序和统计
            List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
            for(int i =0;i<mapList.size();i++){
                Object[] obj = dataList.get(i).toArray();
                int [] a = new int[obj.length];
                for(int m =0;m<obj.length;m++){
                    a[m] = Integer.valueOf(String.valueOf(obj[m]));
                }
                int[] rArry =  new  QuickSort().QuickSort(a);
                int count = rArry.length;
                int total = Integer.valueOf(String.valueOf(mapList.get(i).get("totalTime")));
                long sTime = Integer.valueOf(String.valueOf(mapList.get(i).get("startTime")));
                long eTime = Integer.valueOf(String.valueOf(mapList.get(i).get("endTime")));
                Map<String,Object> resultMap = new HashMap<String, Object>();
                resultMap.put("Name",mapList.get(i).get("name"));
                resultMap.put("Minimum",rArry[0]);
                resultMap.put("Maximum",rArry[rArry.length-1]);
                resultMap.put("Average",new DecimalFormat("######0.00").format((total/count) ));
                resultMap.put("Frequency", new DecimalFormat("######0.00").format(total / (eTime - sTime)));
                resultMap.put("Total", total);
                resultMap.put("Count", count);
                double sid = (rArry.length)*0.05;
                int id = (int)sid;
                int[] subRArry = Arrays.copyOfRange(rArry,id,rArry.length-2*id);
                int stotal = 0;
                int scount = subRArry.length;
                for(int f = 0;f<subRArry.length;f++){
                    stotal = stotal + subRArry[f];
                }
                resultMap.put("sName", mapList.get(i).get("name"));
                resultMap.put("sMinimum",subRArry[0]);
                resultMap.put("sMaximum",subRArry[subRArry.length-1]);
                resultMap.put("sAverage",new DecimalFormat("######0.00").format(stotal/scount));
                resultMap.put("sTotal",stotal);
                resultMap.put("sCount", scount);
                resultList.add(resultMap);
            }

            //输出(写到原文件)
            PrintWriter pw = new PrintWriter(new File(printPateh));
            for(int n = 0;n<resultList.size();n++) {
                pw.println("");
                pw.println("");
                pw.println("");
                pw.println(resultList.get(n).get("Name"));
                pw.println("**************************************************************************");
                pw.println("Minimum\tMaximum\tAverage\tKHz\tTotal\tCount");
                pw.println(resultList.get(n).get("Minimum") +
                        "\t" + resultList.get(n).get("Maximum") + "\t" + resultList.get(n).get("Average") +
                        "\t" + resultList.get(n).get("Frequency") + "\t" + resultList.get(n).get("Total") +
                        "\t" + resultList.get(n).get("Count") + "\t");
                pw.println("");
                pw.println(resultList.get(n).get("Name")+"截取中间90%的数据统计");
                pw.println("**************************************************************************");
                pw.println("Minimum\tMaximum\tAverage\tTotal\tCount");
                pw.println(resultList.get(n).get("sMinimum") +
                        "\t" + resultList.get(n).get("sMaximum") + "\t" + resultList.get(n).get("sAverage") +
                        "\t" + resultList.get(n).get("sTotal") + "\t" + resultList.get(n).get("sCount"));
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

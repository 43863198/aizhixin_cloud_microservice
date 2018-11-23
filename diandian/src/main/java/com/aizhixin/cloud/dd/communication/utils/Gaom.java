package com.aizhixin.cloud.dd.communication.utils;


import com.aizhixin.cloud.dd.communication.dto.Lonlat;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class Gaom {

    /**
     * 返回一个点是否在一个多边形区域内
     *
     * @param lltude
     * @param lltudes
     * @return
     */
    public boolean isInPolygon(String lltude, String lltudes) {
        System.out.println("测试点坐标：" + lltude + "   测试围栏数据：" + lltudes);
        String[] ll = lltude.split("-");
        String latitude = ll[0];
        String longitude = ll[1];
        List<Lonlat> enclosureList = new ArrayList<Lonlat>();
        try {
            JSONArray myJsonArray1 = new JSONArray(lltudes);
            for (int k = 0; k < myJsonArray1.length(); k++) {
                //获取每一个JsonObject对象
                JSONObject myjObject = myJsonArray1.getJSONObject(k);
                String longitude1 = myjObject.getString("longitude");
                String latitude1 = myjObject.getString("latitude");
                Lonlat llt = new Lonlat();
                llt.setLatitude(latitude1);
                llt.setLongitude(longitude1);
                enclosureList.add(llt);
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        double p_x = Double.parseDouble(longitude);
        double p_y = Double.parseDouble(latitude);
        Point2D.Double point = new Point2D.Double(p_x, p_y);

        List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();

        for (Lonlat enclosure : enclosureList) {
            double polygonPoint_x = Double.parseDouble(enclosure.getLongitude());
            double polygonPoint_y = Double.parseDouble(enclosure.getLatitude());
            Point2D.Double polygonPoint = new Point2D.Double(polygonPoint_x, polygonPoint_y);
            pointList.add(polygonPoint);
        }

        return checkWithJdkGeneralPath(point, pointList);
    }


    public boolean checkWithJdkGeneralPath(Point2D.Double point, List<Point2D.Double> polygon) {
        java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();

        Point2D.Double first = polygon.get(0);
        p.moveTo(first.x, first.y);
        polygon.remove(0);
        for (Point2D.Double d : polygon) {
            p.lineTo(d.x, d.y);
        }

        p.lineTo(first.x, first.y);

        p.closePath();

        return p.contains(point);

    }
}

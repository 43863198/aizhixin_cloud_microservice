package com.aizhixin.cloud.dd.rollcall.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 类名称：GDMapUtil 创建人：MEIHUA.LI 创建时间：2016年11月28日 上午11:33:45
 * 修改人：MEIHUA.LI 修改时间：2016年11月28日 上午11:33:45 修改备注：
 */
public class GDMapUtil {

    private List<Distribution> dist = new ArrayList<Distribution>();

    private List<Double> longitude = new ArrayList<Double>();
    private List<Double> dimensionality = new ArrayList<Double>();

    private Distribution midDistribution = new Distribution();

    public String getMidDistribution() {
        return midDistribution.getDimensionality() + "-" + midDistribution.getLongitude();
    }

    public void put(String location) {
        if (StringUtils.isBlank(location)) {
            return;
        }
        String[] lstr = location.split("-");
        if (lstr.length != 2) {
            return;
        }
        double l = Double.parseDouble(lstr[1]);
        double d = Double.parseDouble(lstr[0]);
        dist.add(new Distribution(l, d));
        longitude.add(l);
        dimensionality.add(d);
    }

    public String getMidValue1() {
        int midl = longitude.size() / 2;
        int midd = dimensionality.size() / 2;
        Collections.sort(longitude);
        Collections.sort(dimensionality);
        double midlValue = longitude.get(midl / 2);
        double middValue = dimensionality.get(midd / 2);
        midDistribution.setLongitude(midlValue);
        midDistribution.setDimensionality(middValue);
        return midDistribution.getDimensionality() + "-" + midDistribution.getLongitude();
    }

    public double compareMid(String location) {
        if (StringUtils.isBlank(location)) {
            return 0;
        }
        String[] lstr = location.split("-");
        if (lstr.length != 2) {
            return 0;
        }
        double l = Double.parseDouble(lstr[1]);
        double d = Double.parseDouble(lstr[0]);
        return midDistribution.getDistance(midDistribution, new Distribution(l,
                d));
    }

    public static double compare(String location, String midLocaltion) {
        String[] lstr = location.split("-");
        String[] mstr = midLocaltion.split("-");
        if (lstr.length != 2) {
            return 0;
        }
        Distribution midDistribution = new Distribution(
                Double.parseDouble(mstr[1]), Double.parseDouble(mstr[0]));
        return midDistribution.getDistance(midDistribution, new Distribution(
                Double.parseDouble(lstr[1]), Double.parseDouble(lstr[0])));
    }

    public int getConfiLevel(int normal) {
        getMidValue1();
        int a = 0;
        for (Distribution dis : dist) {
            double temp = midDistribution.getDistance(midDistribution, dis);
            if (temp <= normal) {
                a++;
            }
        }
        return (int) ((a / (float) dist.size()) * 100);
    }
}

class Distribution {

    // 经度
    double longitude;
    // 维度
    double dimensionality;

    public Distribution() {

    }

    public Distribution(double longitude, double dimensionality) {
        super();
        this.longitude = longitude;
        this.dimensionality = dimensionality;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDimensionality() {
        return dimensionality;
    }

    public void setDimensionality(double dimensionality) {
        this.dimensionality = dimensionality;
    }

    /*
     * 计算两点之间距离
     *
     * @param start
     *
     * @param end
     *
     * @return 米
     */
    public double getDistance(Distribution start, Distribution end) {

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        double lat1 = (Math.PI / 180) * start.dimensionality;
        double lat2 = (Math.PI / 180) * end.dimensionality;

        // 地球半径
        double R = 6378.137;

        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;

        return d * 1000;
    }

}

package com.aizhixin.cloud.dd.url;

/**
 * @author LIMH
 * @date 2018/1/18
 */
public class DDURL {
    // 类StudentPhoneResourceV5
    public static String STUDENTPHONERESOURCEV5 = URLUtils.getDdURL() + "/api/phone/v1/new/student";
    /**
     * 查询学生当天课程列表
     */
    public static String STUDENTPHONERESOURCEV5_GETSTUDENTCOURSELIST = STUDENTPHONERESOURCEV5 + "/students/courselist/get";
    /**
     * 签到课程 优化
     */
    public static String STUDENTPHONERESOURCEV5_GETSTUDENTSIGNCOURSEV2 = STUDENTPHONERESOURCEV5 + "/students/signCourse";
    /**
     * 签到
     */
    public static String STUDENTPHONERESOURCEV5_SIGNIN = STUDENTPHONERESOURCEV5 + "/student/signIn";
}

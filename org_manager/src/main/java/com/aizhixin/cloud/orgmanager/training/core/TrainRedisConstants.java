package com.aizhixin.cloud.orgmanager.training.core;

public class TrainRedisConstants {

    public final static String EM2_REIDS          = "em2_api:";

    public final static String EM2_BUSINESS_EXAM  = "exam";

    public static String getExamKey(Integer exerciseType,String id) {
        return EM2_REIDS+EM2_BUSINESS_EXAM+String.valueOf(exerciseType) + "_" + id;
    }

}

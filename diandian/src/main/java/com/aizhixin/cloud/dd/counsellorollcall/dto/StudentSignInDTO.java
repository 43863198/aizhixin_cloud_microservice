package com.aizhixin.cloud.dd.counsellorollcall.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author LIMH
 * @date 2017/12/19
 */
@Data
public class StudentSignInDTO implements Serializable {
    private Long rid;
    private Long studentId;
    private String studentName;
    private String studentNum;
    private String gpsLocation;
    private String gpsDetail;
    private String gpsType;
    private String signTime;
    private String deviceToken;
    private Integer haveReport = 0;
    private boolean haveRead = false;
    private String status;
    private Long classId;
    private String className;
    private Long professionalId;
    private Long collegeId;
    private Long orgId;
    private Long semesterId;
    private List<Double> lltudes;
}

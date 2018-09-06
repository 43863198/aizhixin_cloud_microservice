package com.aizhixin.cloud.dd.rollcall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-16
 */
@Entity
@Table(name = "DD_MODIFY_ATTENDANCE_LOG")
public class ModifyAttendanceLog implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    @Getter
    @Setter
    protected Long id;
    /**
     * 签到id
     */
    @Column(name = "ROLLCALL_ID")
    @Getter
    @Setter
    private Long rollcallId;
    /**
     * 操作人Id
     */
    @Column(name = "OPERATOR_ID")
    @Getter
    @Setter
    private Long operatorId;
    /**
     * 操作人
     */
    @Column(name = "OPERATOR")
    @Getter
    @Setter
    private String operator;
    /**
     * 操作内容
     */
    @Column(name = "OPERATING_CONTENT")
    @Getter
    @Setter
    private String operatingContent;
    /**
     * 操作时间
     */
    @Column(name = "OPERATING_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date operatingDate;

}

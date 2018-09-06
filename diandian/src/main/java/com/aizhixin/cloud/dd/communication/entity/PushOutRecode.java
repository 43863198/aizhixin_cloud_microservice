package com.aizhixin.cloud.dd.communication.entity;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "DD_PUSHOUTRECODE")
public class PushOutRecode extends AbstractEntity  {

    private static final long serialVersionUID = 1L;


    // 教师ID
    @Column(name = "TEACHER_ID")
    @Getter
    @Setter
    private Long              teacherId;

    // 学生ID
    @Column(name = "STUDENT_ID")
    @Getter
    @Setter
    private Long              studentId;

    // 学生姓名
    @Column(name = "NAME")
    @Getter
    @Setter
    private String            name;
    // 学生学号
    @Column(name = "JOBNUMBER")
    @Getter
    @Setter
    private String            jobNumber;

    // 当前地址
    @Column(name = "ADDRESS")
    @Getter
    @Setter
    private String            address;

    // 当前学校
    @Column(name = "ORGAN_ID")
    @Getter
    @Setter
    private Long              organId;

    /**超出范围的时间*/
    @Column(name = "NOTICETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date noticeTime;

    public PushOutRecode() {
        super();

    }

    public PushOutRecode(Long id, Long teacherId, Long studentId, String name, String address, Long organId,
                         Timestamp noticeTime) {
        super();
        this.id = id;
        this.teacherId = teacherId;
        this.studentId = studentId;
        this.name = name;
        this.address = address;
        this.organId = organId;
        this.noticeTime = noticeTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((noticeTime == null) ? 0 : noticeTime.hashCode());
        result = prime * result + ((organId == null) ? 0 : organId.hashCode());
        result = prime * result + ((studentId == null) ? 0 : studentId.hashCode());
        result = prime * result + ((teacherId == null) ? 0 : teacherId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PushOutRecode other = (PushOutRecode) obj;
        if (address == null) {
            if (other.address != null) return false;
        } else if (!address.equals(other.address)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (noticeTime == null) {
            if (other.noticeTime != null) return false;
        } else if (!noticeTime.equals(other.noticeTime)) return false;
        if (organId == null) {
            if (other.organId != null) return false;
        } else if (!organId.equals(other.organId)) return false;
        if (studentId == null) {
            if (other.studentId != null) return false;
        } else if (!studentId.equals(other.studentId)) return false;
        if (teacherId == null) {
            if (other.teacherId != null) return false;
        } else if (!teacherId.equals(other.teacherId)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "PushOutRecode [id=" + id + ", teacherId=" + teacherId + ", studentId=" + studentId + ", name=" + name
                + ", address=" + address + ", organId=" + organId + ", noticeTime=" + noticeTime + "]";
    }

}

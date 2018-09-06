package com.aizhixin.cloud.orgmanager.common.provider.store.redis;

import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.MustCourseScheduleExcelDomain;
import com.aizhixin.cloud.orgmanager.classschedule.domain.excel.OptionCourseScheduleExcelDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.CourseRedisData;
import com.aizhixin.cloud.orgmanager.company.domain.excel.NewStudentRedisData;
import com.aizhixin.cloud.orgmanager.company.domain.excel.StudentRedisData;
import com.aizhixin.cloud.orgmanager.company.domain.excel.TeacherRedisData;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportBaseData;
import com.aizhixin.cloud.orgmanager.importdata.domain.ImportCourseData;
import lombok.Setter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Created by zhen.pan on 2017/6/6.
 */
public class RedisTokenStore {
    public final  static int EXCEL_IMPORT_INFO_CACHE_TIME = 3600;
    public final  static String ORG_EXCEL_SIGN = "org_api:excel";
    private static final String STUDENT = ":student:";
    private static final String NEWSTUDENT = ":newstudent:";
    private static final String TEACHER = ":teacher:";
    private static final String COURSE = ":course:";
    private static final String MUST_COURSE = ":mustcourse:";
    private static final String OPTION_COURSE = ":optioncourse:";
    private static final String TEACHING_CLASS = ":teachingclass";
    private static final String BASE_DATA = ":basedata";
    private static final String COURSE_DATA = ":coursedata";
    private final RedisConnectionFactory connectionFactory;
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    @Setter private String prefix = "";

    public RedisTokenStore(RedisConnectionFactory connectionFactory) {

        this.connectionFactory = connectionFactory;
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private ImportBaseData deserializeImportDataResult(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, ImportBaseData.class);
    }

    private ImportCourseData deserializeImportCourseDataResult(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, ImportCourseData.class);
    }

    private StudentRedisData deserializeStudentRedisData(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, StudentRedisData.class);
    }

    private TeacherRedisData deserializeTeacherRedisData(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, TeacherRedisData.class);
    }

    private CourseRedisData deserializeCourseRedisData(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, CourseRedisData.class);
    }

    private MustCourseScheduleExcelDomain deserializeMustCourseScheduleExcelDomain(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, MustCourseScheduleExcelDomain.class);
    }

    private OptionCourseScheduleExcelDomain deserializeOptionCourseScheduleExcelDomain(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OptionCourseScheduleExcelDomain.class);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }

    public void storeTeachingclassId(String id) {
//        byte[] value = serialize(id);
//        byte[] key = serializeKey(TEACHING_CLASS );
        byte[] value = id.getBytes();
        byte[] key = (prefix  + TEACHING_CLASS).getBytes();

        RedisConnection conn = getConnection();
        try {
            conn.sAdd(key, value);
            conn.expire(key, 86400);
        } finally {
            conn.close();
        }
    }


    public void storeStudentRedisData(String keyStr, StudentRedisData d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(STUDENT + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public StudentRedisData readStudentRedisDatan(String keyStr) {
        byte[] key = serializeKey(STUDENT + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeStudentRedisData(value);
        }
        return null;
    }

    public ImportBaseData readImportBaseMsg(String keyStr) {
        byte[] key = serializeKey(BASE_DATA + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeImportDataResult(value);
        }
        return null;
    }

    public void storeImportBaseData(String keyStr, ImportBaseData d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(BASE_DATA + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public ImportCourseData readImportCourseMsg(String keyStr) {
        byte[] key = serializeKey(COURSE_DATA + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeImportCourseDataResult(value);
        }
        return null;
    }

    public void storeImportCourseData(String keyStr, ImportCourseData d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(COURSE_DATA + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public void storeTeacherRedisData(String keyStr, TeacherRedisData d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(TEACHER + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public TeacherRedisData readTeacherRedisDatan(String keyStr) {
        byte[] key = serializeKey(TEACHER + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeTeacherRedisData(value);
        }
        return null;
    }

    public void storeMustCourseScheduleExcelDomain(String keyStr, MustCourseScheduleExcelDomain d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(MUST_COURSE + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public MustCourseScheduleExcelDomain readMustCourseScheduleExcelDomain(String keyStr) {
        byte[] key = serializeKey(MUST_COURSE + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeMustCourseScheduleExcelDomain(value);
        }
        return null;
    }

    public void storeOptionCourseScheduleExcelDomain(String keyStr, OptionCourseScheduleExcelDomain d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(OPTION_COURSE + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public OptionCourseScheduleExcelDomain readOptionCourseScheduleExcelDomain(String keyStr) {
        byte[] key = serializeKey(OPTION_COURSE + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeOptionCourseScheduleExcelDomain(value);
        }
        return null;
    }

    public CourseRedisData readCourseRedisData(String keyStr) {
        byte[] key = serializeKey(COURSE + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeCourseRedisData(value);
        }
        return null;
    }

    public void storeCourseRedisData(String keyStr, CourseRedisData d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(COURSE + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }

    public void pushAddTeachingclassEvent() {
        RedisConnection conn = getConnection();
        try {
            conn.lPush("add:teachingclass".getBytes(), "add:teachingclass".getBytes());
        } finally {
            conn.close();
        }
    }

    public void pushUpdateTeachingclassEvent() {
        RedisConnection conn = getConnection();
        try {
            conn.lPush("update:teachingclass".getBytes(), "update:teachingclass".getBytes());
        } finally {
            conn.close();
        }
    }

    public void pushDeleteTeachingclassEvent() {
        RedisConnection conn = getConnection();
        try {
            conn.lPush("delete:teachingclass".getBytes(), "delete:teachingclass".getBytes());
        } finally {
            conn.close();
        }
    }

    public void pushDeleteTeachingclassStudentEvent() {
        RedisConnection conn = getConnection();
        try {
            conn.lPush("delete:teachingclassStudent".getBytes(), "delete:teachingclassStudent".getBytes());
        } finally {
            conn.close();
        }
    }

    private NewStudentRedisData deserializeNewStudentRedisData(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, NewStudentRedisData.class);
    }

    public NewStudentRedisData readNewStudentRedisDatan(String keyStr) {
        byte[] key = serializeKey(NEWSTUDENT + keyStr);
        byte[] value = null;
        RedisConnection conn = getConnection();
        try {
            value = conn.get(key);
        } finally {
            conn.close();
        }
        if (null != value && value.length > 0) {
            return deserializeNewStudentRedisData(value);
        }
        return null;
    }


    public void storeNewStudentRedisData(String keyStr, NewStudentRedisData d) {
        byte[] value = serialize(d);
        byte[] key = serializeKey(NEWSTUDENT + keyStr);

        RedisConnection conn = getConnection();
        try {
            conn.set(key, value);
            conn.expire(key, EXCEL_IMPORT_INFO_CACHE_TIME);
        } finally {
            conn.close();
        }
    }
}
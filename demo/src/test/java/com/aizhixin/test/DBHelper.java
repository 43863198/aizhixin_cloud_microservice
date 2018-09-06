package com.aizhixin.test;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@ToString
@Slf4j
public class DBHelper {
    @Getter @Setter @Value("${db.driverName}") private String driverName = "com.mysql.jdbc.Driver";
    @Getter @Setter @Value("${db.url}") private String url = "jdbc:mysql://172.16.23.65:3306/student_dynamics_test?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF8&useSSL=false&rewriteBatchedStatements=true";
    @Getter @Setter @Value("${db.username}") private String username = "devops";
    @Getter @Setter @Value("${db.password}") private String password = "dinglicom";
    public DBHelper() {
        try {
            Class.forName(driverName);
        } catch (Exception e) {
            log.warn("Load ({}) driver manager fail.{}", driverName, e);
        }
    }

    public Connection getConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            log.warn("Get connection({}) fail.{}", url, e);
        }
        return conn;
    }

    public PreparedStatement getPrep(Connection conn, String sql) {
        PreparedStatement prep = null;
        try {
            prep = conn.prepareStatement(sql);
        } catch (Exception e) {
            log.warn("Get PreparedStatementfail({}).{}", sql, e);
        }
        return prep;
    }

    public void closeConn(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (Exception e) {
                log.warn("Close connect fail.{}", e);
            }
        }
    }

    public long excuteCountQuery(Connection conn, String sql) {
        long result = -1;
        if (null != conn) {
            PreparedStatement prep = getPrep(conn, sql);
            if (null != prep) {
                try {
                    ResultSet rs = prep.executeQuery();
                    if(rs.next()) {
                        result = rs.getLong(1);
                    }
                } catch (Exception e) {
                    log.warn("Get Result set fail.{}", e);
                }
            }
        }
        return result;
    }

    public long excuteUpdate(Connection conn, String sql) {
        long result = -1;
        if (null != conn) {
            PreparedStatement prep = getPrep(conn, sql);
            if (null != prep) {
                try {
                    result = prep.executeUpdate();
                } catch (Exception e) {
                    log.warn("Execute sql({}) fail.{}", sql, e);
                }
            }
        }
        return result;
    }
}

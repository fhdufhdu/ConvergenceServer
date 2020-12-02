package com.db.model;

import java.sql.*;

public class DAO {
    static private Connection conn;
    static private PreparedStatement ps;
    static private ResultSet rs;

    public DAO() {

    }

    static public void connectDB() {
        conn = null;
        ps = null;
        rs = null;
        try {
            String user = "MT";
            String passwd = "1234";
            String url = "jdbc:oracle:thin:@192.168.224.250:1521:xe";

            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, user, passwd);
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Failed to load DB driver :" + cnfe.toString());
        } catch (SQLException sqle) {
            System.out.println("Fail to connect DB : " + sqle.toString());
        } catch (Exception e) {
            System.out.println("Unkonwn error");
            e.printStackTrace();
        }
    }

    static public Connection getConn() {
        return conn;
    }

    static void setConn(Connection connv) {
        conn = connv;
    }

    static PreparedStatement getPs() {
        return ps;
    }

    static void setPs(PreparedStatement psv) {
        ps = psv;
    }

    static ResultSet getRs() {
        return rs;
    }

    static void setRs(ResultSet rsv) {
        rs = rsv;
    }

    // DB 종료
    static public void closeDB() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
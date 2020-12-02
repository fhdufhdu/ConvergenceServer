package com.db.model;

import java.sql.*;
import java.util.*;

public class ScreenDAO extends DAO {
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public ScreenDAO() {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }

    // 상영관 추가
    public void addScreen(ScreenDTO screen) throws DAOException, SQLException {
        String insert_sql = "insert into screens(theater_id, name, total_capacity, max_row, max_col) values(?, ?, ?, ?, ?)";

        /*
         * TheaterDTO temp = new TheaterDTO(); TheaterDAO tempDao = new TheaterDAO();
         * 
         * temp.setId(screen.getTheaterId()); if(tempDao.checkTheaterID(temp) == 0) {
         * //ps.close(); throw new DAOException("theater id not found"); }
         */
        if (checkScreen(screen) > 0) {
            // ps.close();
            throw new DAOException("screen info duplicate found");
        }

        ps = conn.prepareStatement(insert_sql);

        ps.setString(1, screen.getTheaterId());
        ps.setString(2, screen.getName());
        ps.setInt(3, screen.getTotalCapacity());
        ps.setInt(4, screen.getMaxRow());
        ps.setInt(5, screen.getMaxCol());

        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);

        rs.close();
        ps.close();
    }

    // 상영관 있는 지 체크
    private int checkScreen(ScreenDTO screen) throws DAOException, SQLException {
        String check_sql = "select * from screens where theater_id = ? and name = ? and not(id = ?)";
        ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        ps.setString(1, screen.getTheaterId());
        ps.setString(2, screen.getName());
        ps.setString(3, screen.getId());

        rs = ps.executeQuery();
        rs.last();
        int result_row = rs.getRow();

        rs.close();
        ps.close();

        return result_row;
    }

    // 상영관 리스트 반환
    public ArrayList<ScreenDTO> getScreenList(ScreenDTO screen) throws DAOException, SQLException {
        ArrayList<ScreenDTO> temp_list = new ArrayList<ScreenDTO>();
        String insert_sql = "select * from screens where theater_id = ? and name = ? and total_capacity = ? and max_row = ? and max_col = ?";

        ps = conn.prepareStatement(insert_sql);

        ps.setString(1, screen.getTheaterId());
        ps.setString(2, screen.getName());
        ps.setInt(3, screen.getTotalCapacity());
        ps.setInt(4, screen.getMaxRow());
        ps.setInt(5, screen.getMaxCol());

        rs = ps.executeQuery();
        while (rs.next()) {
            String id = rs.getString("id");
            String theater_id = rs.getString("theater_id");
            String name = rs.getString("name");
            int total_capacity = rs.getInt("total_capacity");
            int max_row = rs.getInt("max_row");
            int max_col = rs.getInt("max_col");
            temp_list.add(new ScreenDTO(id, theater_id, name, total_capacity, max_row, max_col));
        }

        ps.close();

        return temp_list;
    }

    // 상영관 리스트 반환
    public ArrayList<ScreenDTO> getScreenList(String t_id) throws DAOException, SQLException {
        ArrayList<ScreenDTO> temp_list = new ArrayList<ScreenDTO>();
        String insert_sql = "select * from screens where theater_id = ?";

        ps = conn.prepareStatement(insert_sql);
        ps.setString(1, t_id);

        rs = ps.executeQuery();
        while (rs.next()) {
            String id = rs.getString(1);
            String theater_id = rs.getString(2);
            String name = rs.getString(3);
            int total_capacity = rs.getInt(4);
            int max_row = rs.getInt(5);
            int max_col = rs.getInt(6);
            temp_list.add(new ScreenDTO(id, theater_id, name, total_capacity, max_row, max_col));
        }
        ps.close();

        return temp_list;
    }

    // 상영관 수정
    public void changeScreen(ScreenDTO screen) throws DAOException, SQLException {
        String insert_sql = "update screens set name = ?, total_capacity = ?, max_row = ?, max_col = ? where id = ?";

        if (checkScreen(screen) > 0) {
            ps.close();
            throw new DAOException("screen info duplicate found");
        }

        ps = conn.prepareStatement(insert_sql);

        ps.setString(1, screen.getName());
        ps.setInt(2, screen.getTotalCapacity());
        ps.setInt(3, screen.getMaxRow());
        ps.setInt(4, screen.getMaxCol());
        ps.setString(5, screen.getId());

        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);

        ps.close();
    }

    // 상영관 삭제
    public void removeScreen(String sid) throws DAOException, SQLException {
        String insert_sql = "delete from screens where id = ?";

        ps = conn.prepareStatement(insert_sql);

        ps.setString(1, sid);

        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);

        ps.close();
    }

    public ScreenDTO getScreenElem(String sid) throws DAOException, SQLException {
        String insert_sql = "select * from screens where id = ?";

        ps = conn.prepareStatement(insert_sql);

        ps.setString(1, sid);

        rs = ps.executeQuery();
        rs.next();
        String id = rs.getString(1);
        String theater_id = rs.getString(2);
        String name = rs.getString(3);
        int total_capacity = rs.getInt(4);
        int max_row = rs.getInt(5);
        int max_col = rs.getInt(6);
        ps.close();
        return new ScreenDTO(id, theater_id, name, total_capacity, max_row, max_col);
    }
}

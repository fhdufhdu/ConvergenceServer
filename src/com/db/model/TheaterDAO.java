package com.db.model;

import java.sql.*;
import java.util.*;

public class TheaterDAO extends DAO
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public TheaterDAO()
    {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }
    
    // 새로운 극장 추가
    public void addTheater(TheaterDTO mt) throws DAOException, SQLException
    {
        String insert_sql = "insert into theaters(name, address, total_screen, total_seat) values(?, ?, ?, ?)";
        
        if (checkTheater(mt) != 0)
        {
            ps.close();
            throw new DAOException("name, address duplicate found");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mt.getName());
        ps.setString(2, mt.getAddress());
        ps.setInt(3, mt.getTotalScreen());
        ps.setInt(4, mt.getTotalSeats());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // 영화관 중복 확인
    private int checkTheater(TheaterDTO mt) throws DAOException, SQLException
    {
        String check_sql = "select * from theaters where (name = ? or address = ?) and not(id = ?)";
        ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        ps.setString(1, mt.getName());
        ps.setString(2, mt.getAddress());
        ps.setString(3, mt.getId());
        
        rs = ps.executeQuery();
        rs.last();
        int result_row = rs.getRow();
        
        rs.close();
        ps.close();
        
        return result_row;
    }
    
    // 모든 영화관 반환
    public ArrayList<TheaterDTO> getTheaterList() throws DAOException, SQLException
    {
        ArrayList<TheaterDTO> temp_list = new ArrayList<TheaterDTO>();
        String insert_sql = "select * from theaters";
        ps = conn.prepareStatement(insert_sql);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String name = rs.getString("name");
            String address = rs.getString("address");
            int total_screen = rs.getInt("total_screen");
            int total_seats = rs.getInt("total_seat");
            temp_list.add(new TheaterDTO(id, name, address, total_screen, total_seats));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    // 영화관 요소 얻기
    public TheaterDTO getTheaterElem(String tid) throws DAOException, SQLException
    {
        String insert_sql = "select * from theaters where id = ?";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, tid);
        
        rs = ps.executeQuery();
        rs.next();
        String id = rs.getString("id");
        String name = rs.getString("name");
        String address = rs.getString("address");
        int total_screen = rs.getInt("total_screen");
        int total_seats = rs.getInt("total_seat");
        
        rs.close();
        ps.close();
        
        return new TheaterDTO(id, name, address, total_screen, total_seats);
    }
    
    // 선택한 영화관의 정보 수정
    public void changeTheater(TheaterDTO mt) throws DAOException, SQLException
    {
        String insert_sql = "update theaters set name = ?, address = ? where id = ?";
        
        // 바꾸려는 정보가 중복인지 확인
        if (checkTheater(mt) > 0)
        {
            ps.close();
            throw new DAOException("name, address duplicate found");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mt.getName());
        ps.setString(2, mt.getAddress());
        ps.setString(3, mt.getId());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // 영화관 삭제
    public void removeTheater(String id) throws DAOException, SQLException
    {
        String insert_sql = "delete from theaters where id = ?";
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, id);
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
}

package com.db.model;

import java.sql.*;
import java.util.*;

public class TimeTableDAO extends DAO
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public TimeTableDAO()
    {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }
    
    // 상영시간표 설정
    public void addTimeTable(TimeTableDTO elem) throws DAOException, SQLException
    {
        try
        {
            String insert_sql = "call set_timetable_elem(?, ?, ?, ?, ?, ?)";
            
            if (checkTimeTable(elem) != 0)
            {
                ps.close();
                throw new DAOException("DUPLICATE_TIMETABLE");
            }
            
            ps = conn.prepareStatement(insert_sql);
            
            ps.setString(1, elem.getId());
            ps.setString(2, elem.getMovieId());
            ps.setString(3, elem.getScreenId());
            ps.setTimestamp(4, elem.getStartTime());
            ps.setTimestamp(5, elem.getEndTime());
            ps.setString(6, "1");
            
            int r = ps.executeUpdate();
            System.out.println("변경된 row : " + r);
            
            ps.close();
        }
        catch (SQLException sqle)
        {
            System.out.println("find error on sql");
            sqle.printStackTrace();
        }
    }
    
    // 시간과 날짜 중복되는지 확인해야함
    private int checkTimeTable(TimeTableDTO elem) throws DAOException, SQLException
    {
        try
        {
            String check_sql = "select * from timetables where movie_id = ? and screen_id = ? and ((start_time between ? and ?) or (end_time between ? and ?)) and not(id = ?)";
            ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            ps.setString(1, elem.getMovieId());
            ps.setString(2, elem.getScreenId());
            ps.setTimestamp(3, elem.getStartTime());
            ps.setTimestamp(4, elem.getEndTime());
            ps.setTimestamp(5, elem.getStartTime());
            ps.setTimestamp(6, elem.getEndTime());
            ps.setString(7, elem.getId());
            
            rs = ps.executeQuery();
            rs.last();
            int result_row = rs.getRow();
            
            rs.close();
            ps.close();
            
            return result_row;
            
        }
        catch (SQLException sqle)
        {
            System.out.println("find error on sql");
            sqle.printStackTrace();
        }
        return 0;
    }
    
    // 상영 시간표 출력
    public ArrayList<TimeTableDTO> getTimeTableList(TimeTableDTO elem) throws DAOException, SQLException
    {
        try
        {
            ArrayList<TimeTableDTO> temp_list = new ArrayList<TimeTableDTO>();
            String insert_sql = "select * from timetables where movie_id like ? and screen_id like ? and start_time >= ? and end_time <= ?";
            ps = conn.prepareStatement(insert_sql);
            ps.setString(1, elem.getMovieId());
            ps.setString(2, elem.getScreenId());
            ps.setTimestamp(3, elem.getStartTime());
            ps.setTimestamp(4, elem.getEndTime());
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                String id = rs.getString("id");
                String movie_id = rs.getString("movie_id");
                String screen_id = rs.getString("screen_id");
                String start_time = rs.getTimestamp("start_time").toString();
                String end_time = rs.getTimestamp("end_time").toString();
                String type = rs.getString("type");
                int current_rsv = rs.getInt("current_rsv");
                temp_list.add(new TimeTableDTO(id, movie_id, screen_id, start_time, end_time, type, current_rsv));
            }
            
            rs.close();
            ps.close();
            
            return temp_list;
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<TimeTableDTO> getTimeTableList(TimeTableDTO elem, String theater_id) throws DAOException, SQLException
    {
        try
        {
            ArrayList<TimeTableDTO> temp_list = new ArrayList<TimeTableDTO>();
            String insert_sql = "select * from timetables t, movies m, screens s where t.movie_id = m.id and s.id = t.screen_id and s.theater_id in (select id from theaters where id = ?) and movie_id like ? and start_time >= ? and end_time <= ? and m.is_current > -1";
            ps = conn.prepareStatement(insert_sql);
            
            ps.setString(1, theater_id);
            ps.setString(2, elem.getMovieId());
            ps.setTimestamp(3, elem.getStartTime());
            ps.setTimestamp(4, elem.getEndTime());
            
            rs = ps.executeQuery();
            while (rs.next())
            {
                String id = rs.getString("id");
                String movie_id = rs.getString("movie_id");
                String screen_id = rs.getString("screen_id");
                String start_time = rs.getTimestamp("start_time").toString();
                String end_time = rs.getTimestamp("end_time").toString();
                String type = rs.getString("type");
                int current_rsv = rs.getInt("current_rsv");
                temp_list.add(new TimeTableDTO(id, movie_id, screen_id, start_time, end_time, type, current_rsv));
            }
            
            rs.close();
            ps.close();
            
            return temp_list;
        }
        catch (SQLException sqle)
        {
            System.out.println("find error on sql");
            sqle.printStackTrace();
        }
        throw new DAOException("not found result of theaters");
    }
    
    public TimeTableDTO getTimeTable(String tid) throws DAOException, SQLException
    {
        try
        {
            String insert_sql = "select * from timetables where id = ?";
            ps = conn.prepareStatement(insert_sql);
            
            ps.setString(1, tid);
            
            rs = ps.executeQuery();
            rs.next();
            String id = rs.getString("id");
            String movie_id = rs.getString("movie_id");
            String screen_id = rs.getString("screen_id");
            String start_time = rs.getTimestamp("start_time").toString();
            String end_time = rs.getTimestamp("end_time").toString();
            String type = rs.getString("type");
            int current_rsv = rs.getInt("current_rsv");
            
            rs.close();
            ps.close();
            
            return new TimeTableDTO(id, movie_id, screen_id, start_time, end_time, type, current_rsv);
        }
        catch (SQLException sqle)
        {
            System.out.println("find error on sql");
            sqle.printStackTrace();
        }
        throw new DAOException("not found result of theaters");
    }
    
    public double getRsvRate(String movie_id) throws DAOException, SQLException
    {
        try
        {
            String insert_sql = "call CALC_RSV_RATE(?, ?, ?)";
            
            CallableStatement cs = conn.prepareCall(insert_sql);
            
            cs.setString(1, movie_id);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.executeUpdate();
            int total_capacity = cs.getInt(2);
            int total_rsv = cs.getInt(3);
            
            cs.close();
            return ((double) total_rsv / (double) total_capacity);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 상영 시간표 수정
    public void changeTimeTable(TimeTableDTO elem) throws DAOException, SQLException
    {
        try
        {
            String insert_sql = "call set_timetable_elem(?, ?, ?, ?, ?, ?)";
            
            if (checkTimeTable(elem) != 0)
            {
                ps.close();
                throw new DAOException("DUPLICATE_TIMETABLE");
            }
            
            ps = conn.prepareStatement(insert_sql);
            
            ps.setString(1, elem.getId());
            ps.setString(2, elem.getMovieId());
            ps.setString(3, elem.getScreenId());
            ps.setTimestamp(4, elem.getStartTime());
            ps.setTimestamp(5, elem.getEndTime());
            ps.setString(6, "0");
            
            int r = ps.executeUpdate();
            System.out.println("변경된 row : " + r);
            
            ps.close();
            
        }
        catch (SQLException sqle)
        {
            System.out.println("find error on sql");
            sqle.printStackTrace();
        }
    }
    
    // 상영시간표 삭제
    public void removeTimeTable(String ttid) throws DAOException, SQLException
    {
        try
        {
            String insert_sql = "delete from timetables where id = ?";
            ps = conn.prepareStatement(insert_sql);
            ps.setString(1, ttid);
            int r = ps.executeUpdate();
            System.out.println("변경된 row : " + r);
            
            ps.close();
            
        }
        catch (SQLException sqle)
        {
            System.out.println("find error on sql");
            sqle.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

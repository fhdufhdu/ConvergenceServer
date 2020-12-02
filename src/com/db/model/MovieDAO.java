package com.db.model;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class MovieDAO extends DAO
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public MovieDAO()
    {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }
    
    // 영화 추가
    public void addMovie(MovieDTO new_mov) throws DAOException, SQLException
    {
        String insert_sql = "insert into movies(title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        if (checkMovie(new_mov) != 0)
        {
            ps.close();
            throw new DAOException("movie duplicate found");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, new_mov.getTitle());
        ps.setDate(2, new_mov.getReleaseDate());
        ps.setString(3, new_mov.getIsCurrent());
        ps.setString(4, new_mov.getPlot());
        ps.setString(5, new_mov.getPosterPath());
        ps.setString(6, new_mov.getStillCutPath());
        ps.setString(7, new_mov.getTrailerPath());
        ps.setString(8, new_mov.getDirector());
        ps.setString(9, new_mov.getActor());
        ps.setInt(10, new_mov.getMin());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // 개봉일과 제목이 같은 영화가 있는 지 탐색
    public int checkMovie(MovieDTO mov) throws DAOException, SQLException
    {
        String check_sql = "select * from movies where title = ? and release_date = ? and not(id = ?)";
        ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        ps.setString(1, mov.getTitle());
        ps.setDate(2, mov.getReleaseDate());
        ps.setString(3, mov.getId());
        
        rs = ps.executeQuery();
        rs.last();
        int result_row = rs.getRow();
        
        rs.close();
        ps.close();
        return result_row;
    }
    
    // 영화 출력
    public ArrayList<MovieDTO> getMovieList(HashMap<String, String> info) throws DAOException, SQLException
    {
        ArrayList<MovieDTO> temp_list = new ArrayList<MovieDTO>();
        String insert_sql = "select * from movies where title like ? and (release_date between ? and ?) and is_current like ? and director like ? and actor like ?";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, info.get("title"));
        ps.setDate(2, Date.valueOf(info.get("start_date")));
        ps.setDate(3, Date.valueOf(info.get("end_date")));
        ps.setString(4, info.get("is_current"));
        ps.setString(5, info.get("director"));
        ps.setString(6, info.get("actor"));
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String title = rs.getString("title");
            String release_date = rs.getDate("release_date").toString();
            String is_current = rs.getString("is_current");
            String plot = rs.getString("plot");
            String poster_path = rs.getString("poster_path");
            String stillcut_path = rs.getString("stillcut_path");
            String trailer_path = rs.getString("trailer_path");
            String director = rs.getString("director");
            String actor = rs.getString("actor");
            int min = rs.getInt("min");
            temp_list.add(new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    public ArrayList<MovieDTO> getCurrentMovieList() throws DAOException, SQLException
    {
        ArrayList<MovieDTO> temp_list = new ArrayList<MovieDTO>();
        String insert_sql = "select * from movies where is_current like '1'";
        ps = conn.prepareStatement(insert_sql);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String title = rs.getString("title");
            String release_date = rs.getDate("release_date").toString();
            String is_current = rs.getString("is_current");
            String plot = rs.getString("plot");
            String poster_path = rs.getString("poster_path");
            String stillcut_path = rs.getString("stillcut_path");
            String trailer_path = rs.getString("trailer_path");
            String director = rs.getString("director");
            String actor = rs.getString("actor");
            int min = rs.getInt("min");
            temp_list.add(new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    public ArrayList<MovieDTO> getSoonMovieList() throws DAOException, SQLException
    {
        ArrayList<MovieDTO> temp_list = new ArrayList<MovieDTO>();
        String insert_sql = "select * from movies where is_current like '2'";
        ps = conn.prepareStatement(insert_sql);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String title = rs.getString("title");
            String release_date = rs.getDate("release_date").toString();
            String is_current = rs.getString("is_current");
            String plot = rs.getString("plot");
            String poster_path = rs.getString("poster_path");
            String stillcut_path = rs.getString("stillcut_path");
            String trailer_path = rs.getString("trailer_path");
            String director = rs.getString("director");
            String actor = rs.getString("actor");
            int min = rs.getInt("min");
            temp_list.add(new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    public ArrayList<MovieDTO> getMovieListForType(String type) throws DAOException, SQLException
    {
        ArrayList<MovieDTO> temp_list = new ArrayList<MovieDTO>();
        String insert_sql = "select * from movies where is_current = ?";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, type);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String title = rs.getString("title");
            String release_date = rs.getDate("release_date").toString();
            String is_current = rs.getString("is_current");
            String plot = rs.getString("plot");
            String poster_path = rs.getString("poster_path");
            String stillcut_path = rs.getString("stillcut_path");
            String trailer_path = rs.getString("trailer_path");
            String director = rs.getString("director");
            String actor = rs.getString("actor");
            int min = rs.getInt("min");
            temp_list.add(new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    public ArrayList<MovieDTO> getAllMovieList() throws DAOException, SQLException
    {
        ArrayList<MovieDTO> temp_list = new ArrayList<MovieDTO>();
        String insert_sql = "select * from movies";
        ps = conn.prepareStatement(insert_sql);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String title = rs.getString("title");
            String release_date = rs.getDate("release_date").toString();
            String is_current = rs.getString("is_current");
            String plot = rs.getString("plot");
            String poster_path = rs.getString("poster_path");
            String stillcut_path = rs.getString("stillcut_path");
            String trailer_path = rs.getString("trailer_path");
            String director = rs.getString("director");
            String actor = rs.getString("actor");
            int min = rs.getInt("min");
            temp_list.add(new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    public MovieDTO getMovie(String mid) throws DAOException, SQLException
    {
        String insert_sql = "select * from movies where id = ?";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mid);
        
        rs = ps.executeQuery();
        rs.next();
        String id = rs.getString("id");
        String title = rs.getString("title");
        String release_date = rs.getDate("release_date").toString();
        String is_current = rs.getString("is_current");
        String plot = rs.getString("plot");
        String poster_path = rs.getString("poster_path");
        String stillcut_path = rs.getString("stillcut_path");
        String trailer_path = rs.getString("trailer_path");
        String director = rs.getString("director");
        String actor = rs.getString("actor");
        int min = rs.getInt("min");
        
        rs.close();
        ps.close();
        
        return new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min);
    }
    
    // 영화 수정
    public void changeMovie(MovieDTO mov) throws DAOException, SQLException
    {
        String insert_sql = "update movies set title = ?, release_date = ?, is_current = ?, plot = ?, poster_path = ?, stillcut_path = ?, trailer_path = ? , director = ? , actor = ? , min = ?  where id = ?";
        
        if (checkMovie(mov) > 0)
        {
            ps.close();
            throw new DAOException("movie info duplicate found");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mov.getTitle());
        ps.setDate(2, mov.getReleaseDate());
        ps.setString(3, mov.getIsCurrent());
        ps.setString(4, mov.getPlot());
        ps.setString(5, mov.getPosterPath());
        ps.setString(6, mov.getStillCutPath());
        ps.setString(7, mov.getTrailerPath());
        ps.setString(8, mov.getDirector());
        ps.setString(9, mov.getActor());
        ps.setInt(10, mov.getMin());
        ps.setString(11, mov.getId());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // 영화 삭제
    public void removeMovie(String id) throws DAOException, SQLException
    {
        String insert_sql = "delete from movies where id = ?";
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, id);
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    public ArrayList<MovieDTO> getMemberMovie(String mem_id) throws DAOException, SQLException
    {
        ArrayList<MovieDTO> temp_list = new ArrayList<MovieDTO>();
        String insert_sql = "select * from movies where id in (select movie_id from timetables where id in (select distinct ttable_id from reservations where member_id = ? and cancel = 0))";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mem_id);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String title = rs.getString("title");
            String release_date = rs.getDate("release_date").toString();
            String is_current = rs.getString("is_current");
            String plot = rs.getString("plot");
            String poster_path = rs.getString("poster_path");
            String stillcut_path = rs.getString("stillcut_path");
            String trailer_path = rs.getString("trailer_path");
            String director = rs.getString("director");
            String actor = rs.getString("actor");
            int min = rs.getInt("min");
            temp_list.add(new MovieDTO(id, title, release_date, is_current, plot, poster_path, stillcut_path, trailer_path, director, actor, min));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
}

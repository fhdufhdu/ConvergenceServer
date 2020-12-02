package com.db.model;

import java.sql.*;
import java.util.*;

public class ReviewDAO extends DAO
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public ReviewDAO()
    {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }
    
    // 리뷰 등록
    public void addReview(ReviewDTO rv) throws DAOException, SQLException
    {
        String insert_sql = "insert into reviews(member_id, movie_id, star, text, write_time) values(?, ?, ?, ?, sysdate)";
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, rv.getMemberId());
        ps.setString(2, rv.getMovieId());
        ps.setInt(3, rv.getStar());
        ps.setString(4, rv.getText());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // 영화에 해당하는 리뷰 출력
    public ArrayList<ReviewDTO> getRvListFromMov(String mov_id) throws DAOException, SQLException
    {
        ArrayList<ReviewDTO> temp_list = new ArrayList<ReviewDTO>();
        String insert_sql = "select * from reviews where movie_id = ?";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mov_id);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String member_id = rs.getString("member_id");
            String movie_id = rs.getString("movie_id");
            int star = rs.getInt("star");
            String text = rs.getString("text");
            Timestamp time = rs.getTimestamp("write_time");
            temp_list.add(new ReviewDTO(id, member_id, movie_id, star, text, time.toString()));
        }
        
        rs.close();
        ps.close();
        
        return temp_list;
    }
    
    // 영화의 평점 획득
    public int getAverStarGrade(String mov_id) throws DAOException, SQLException
    {
        String insert_sql = "select avg(star) from reviews where movie_id = ?";
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mov_id);
        
        rs = ps.executeQuery();
        rs.next();
        int result = rs.getInt(1);
        
        rs.close();
        ps.close();
        
        return result;
    }
    
    // 리뷰 제거
    public void removeReview(String rid) throws DAOException, SQLException
    {
        String insert_sql = "delete from reviews where id = ?";
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, rid);
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
}

package com.db.model;

import java.sql.*;
import java.util.ArrayList;

public class MemberDAO extends DAO
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public MemberDAO()
    {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }
    
    // 유저 추가
    public void addMember(MemberDTO mem) throws DAOException, SQLException
    {
        String insert_sql = "insert into members(id, role, password, account, name, phone_number, birth, gender) values(?, ?, ?, ?, ?, ?, ?, ?)";
        
        if (checkMember(mem) != 0)
        {
            ps.close();
            throw new DAOException("id duplicate found");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mem.getId());
        ps.setString(2, mem.getRole());
        ps.setString(3, mem.getPassword());
        ps.setString(4, mem.getAccount());
        ps.setString(5, mem.getName());
        ps.setString(6, mem.getPhoneNumber());
        ps.setDate(7, mem.getBirth());
        ps.setString(8, mem.getGender());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // id 중복 체크
    private int checkMember(MemberDTO mem) throws DAOException, SQLException
    {
        String check_sql = "select * from members where id = ?";
        ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        ps.setString(1, mem.getId());
        
        rs = ps.executeQuery();
        rs.last();
        int result_row = rs.getRow();
        
        rs.close();
        ps.close();
        
        return result_row;
    }
    
    // 사용자 정보 가져오기
    public MemberDTO getMember(String mid, String mpasswd) throws DAOException, SQLException
    {
        String check_sql = "select * from members where id = ? and password = ?";
        ps = conn.prepareStatement(check_sql);
        
        ps.setString(1, mid);
        ps.setString(2, mpasswd);
        
        rs = ps.executeQuery();
        rs.next();
        String id = rs.getString("id");
        String role = rs.getString("role");
        String password = rs.getString("password");
        String account = rs.getString("account");
        if (role.equals("1"))
        {
            return new MemberDTO(id, role, password, account);
        }
        String name = rs.getString("name");
        String phone_number = rs.getString("phone_number");
        Date birth = rs.getDate("birth");
        String gender = rs.getString("gender");
        rs.close();
        ps.close();
        return new MemberDTO(id, role, password, account, name, phone_number, birth.toString(), gender);
    }
    
    // 사용자 정보 가져오기
    public MemberDTO getMemberInfo(String mid) throws DAOException, SQLException
    {
        String check_sql = "select * from members where id = ?";
        ps = conn.prepareStatement(check_sql);
        
        ps.setString(1, mid);
        
        rs = ps.executeQuery();
        rs.next();
        String id = rs.getString("id");
        String role = rs.getString("role");
        String password = rs.getString("password");
        String account = rs.getString("account");
        if (role.equals("1"))
        {
            return new MemberDTO(id, role, password, account);
        }
        String name = rs.getString("name");
        String phone_number = rs.getString("phone_number");
        Date birth = rs.getDate("birth");
        String gender = rs.getString("gender");
        rs.close();
        ps.close();
        return new MemberDTO(id, role, password, account, name, phone_number, birth.toString(), gender);
    }
    
    // 사용자 정보 가져오기
    public ArrayList<MemberDTO> getAllMember() throws DAOException, SQLException
    {
        ArrayList<MemberDTO> result = new ArrayList<MemberDTO>();
        String check_sql = "select * from members";
        ps = conn.prepareStatement(check_sql);
        
        rs = ps.executeQuery();
        while (rs.next())
        {
            String id = rs.getString("id");
            String role = rs.getString("role");
            String password = rs.getString("password");
            String account = rs.getString("account");
            if (role.equals("1"))
            {
                continue;
            }
            String name = rs.getString("name");
            String phone_number = rs.getString("phone_number");
            Date birth = rs.getDate("birth");
            String gender = rs.getString("gender");
            result.add(new MemberDTO(id, role, password, account, name, phone_number, birth.toString(), gender));
        }
        
        rs.close();
        ps.close();
        return result;
    }
    
    // 멤버 정보 수정
    public void changeMemberInfo(MemberDTO mem) throws DAOException, SQLException
    {
        String insert_sql = "update members set password = ?, account = ?, name = ?, phone_number = ?, birth = ?, gender = ? where id = ?";
        
        // 바꾸려는 정보가 중복인지 확인
        if (checkMember(mem) > 1)
        {
            ps.close();
            throw new DAOException("member duplicate found");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, mem.getPassword());
        ps.setString(2, mem.getAccount());
        ps.setString(3, mem.getName());
        ps.setString(4, mem.getPhoneNumber());
        ps.setDate(5, mem.getBirth());
        ps.setString(6, mem.getGender());
        ps.setString(7, mem.getId());
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
}

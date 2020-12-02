package com.db.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO extends DAO
{
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public AccountDAO()
    {
        super();
        conn = getConn();
        ps = getPs();
        rs = getRs();
    }
    
    // 관리자 계좌 받아오기
    public AccountDTO getAdminAccount(String mid) throws SQLException
    {
        String check_sql = "select * from accounts where account in (select account from members where id = ?)";
        ps = conn.prepareStatement(check_sql);
        
        ps.setString(1, mid);
        
        rs = ps.executeQuery();
        rs.next();
        String account = rs.getString("account");
        int money = rs.getInt("money");
        String bank = rs.getString("bank");
        
        rs.close();
        ps.close();
        
        return new AccountDTO(account, bank, money);
    }
    
    // 관리자 계좌 정보 수정
    public void changeAccountInfo(String mid, String bank, String account) throws DAOException, SQLException
    {
        String insert_sql = "update members set account = ? where id = ?";
        
        if (isAvailableAccount(bank, account))
        {
            throw new DAOException("NOT_FOUND_ACCOUNT");
        }
        
        ps = conn.prepareStatement(insert_sql);
        
        ps.setString(1, account);
        ps.setString(2, mid);
        
        int r = ps.executeUpdate();
        System.out.println("변경된 row : " + r);
        
        ps.close();
    }
    
    // 사용 가능 계좌인지 확인
    public boolean isAvailableAccount(String bank, String account) throws DAOException, SQLException
    {
        String check_sql = "select * from accounts where account = ? and bank = ?";
        ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        ps.setString(1, account);
        ps.setString(2, bank);
        
        rs = ps.executeQuery();
        rs.last();
        int result_row = rs.getRow();
        
        rs.close();
        ps.close();
        return result_row == 0 ? true : false;
    }
}

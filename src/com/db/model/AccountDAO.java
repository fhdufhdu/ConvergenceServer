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
    
    // 멤버 정보 수정
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
    
    /*
     * //계좌 추가 public void addAccount(AccountDTO ac) throws DAOException, SQLException { String insert_sql = "insert into accounts(account, money) values(?, ?)";
     * 
     * if(checkAccount(ac) != 0) { ps.close(); throw new DAOException("account duplicate found"); }
     * 
     * ps = conn.prepareStatement(insert_sql);
     * 
     * ps.setString(1, ac.getAccount()); ps.setInt(2, ac.getMoney());
     * 
     * int r = ps.executeUpdate(); System.out.println("변경된 row : " + r);
     * 
     * ps.close(); }
     * 
     * //계좌가 중복되는지 확인 private int checkAccount(AccountDTO ac) throws DAOException, SQLException { String check_sql = "select * from accounts where account = ?"; ps = conn.prepareStatement(check_sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
     * 
     * ps.setString(1, ac.getAccount());
     * 
     * rs = ps.executeQuery(); rs.last(); int result_row = rs.getRow();
     * 
     * rs.close(); ps.close();
     * 
     * return result_row; }
     * 
     * //계좌 잔액 수정 - 문제 발생 시 예외처리 public void plusMoney(String account, int money) throws DAOException, SQLException { String insert_sql = "update accounts set money = ? where account = ?";
     * 
     * int modified_money = getMoney(account) + money;
     * 
     * ps = conn.prepareStatement(insert_sql);
     * 
     * ps.setInt(1, modified_money); ps.setString(2, account);
     * 
     * int r = ps.executeUpdate(); System.out.println("변경된 row : " + r);
     * 
     * ps.close(); }
     * 
     * //계좌 잔액 수정 - 문제 발생 시 예외처리0 public void minusMoney(String account, int money) throws DAOException, SQLException { String insert_sql = "update accounts set money = ? where account = ?";
     * 
     * if(getMoney(account) < money) { throw new DAOException("잔액 부족"); } int modified_money = getMoney(account) - money;
     * 
     * ps = conn.prepareStatement(insert_sql);
     * 
     * ps.setInt(1, modified_money); ps.setString(2, account);
     * 
     * int r = ps.executeUpdate(); System.out.println("변경된 row : " + r);
     * 
     * ps.close(); /*String insert_sql = "call payment(?, ?)";
     * 
     * //if(getMoney(account) < money) //{ // throw new DAOException("잔액 부족"); //} //int modified_money = getMoney(account) - money;
     * 
     * ps = conn.prepareStatement(insert_sql);
     * 
     * ps.setString(1, account); ps.setInt(2, money);
     * 
     * int r = ps.executeUpdate(); System.out.println("변경된 row : " + r);
     * 
     * ps.close(); }
     * 
     * public int getMoney(String account) throws DAOException, SQLException { String check_sql = "select * from accounts where account = ?"; ps = conn.prepareStatement(check_sql);
     * 
     * ps.setString(1, account);
     * 
     * rs = ps.executeQuery(); rs.next(); int money = rs.getInt("money"); rs.close(); ps.close(); return money; }
     */
    
}

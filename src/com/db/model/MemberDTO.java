package com.db.model;

import java.sql.*;

public class MemberDTO extends DTO
{
    private String id;
    private String role;
    private String password;
    private String account;
    private String name;
    private String phoneNumber;
    private Date birth;
    private String gender;
    
    // 일반 사용자용
    public MemberDTO(String id, String role, String password, String account, String name, String phoneNumber, String birth, String gender)
    {
        this.id = id;
        this.role = role;
        this.password = password;
        this.account = account;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birth = Date.valueOf(birth); // date
        this.gender = gender;
    }
    
    // 관리자용
    public MemberDTO(String id, String role, String password, String account)
    {
        this.id = id;
        this.role = role;
        this.password = password;
        this.account = account;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setid(String id)
    {
        this.id = id;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getAccount()
    {
        return account;
    }
    
    public void setAccount(String account)
    {
        this.account = account;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getPhoneNumber()
    {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }
    
    public Date getBirth()
    {
        return birth;
    }
    
    public void setBirth(String birth)
    {
        this.birth = Date.valueOf(birth);
    }
    
    public String getGender()
    {
        return gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }
}

package com.db.model;

import java.sql.Timestamp;

public class ReservationDTO extends DTO
{
    private String id;
    private String memberId;
    private String timeTableId;
    private int screenRow;
    private int screenCol;
    private int price; // 자동 결정 되게 하기
    private String type;
    private Timestamp rsvTime;
    private String account;
    private String bank;
    
    public ReservationDTO(String id, String memberId, String timeTableId, int screenRow, int screenCol, int price, String type, Timestamp rsvTime, String account, String bank)
    {
        this.id = id;
        this.memberId = memberId;
        this.timeTableId = timeTableId;
        this.screenRow = screenRow;
        this.screenCol = screenCol;
        this.price = price;
        this.type = type;
        this.rsvTime = rsvTime;
        this.account = account;
        this.bank = bank;
    }
    
    public ReservationDTO(String id, String memberId, String timeTableId, int screenRow, int screenCol, int price, String type, String rsvTime, String account, String bank)
    {
        this.id = id;
        this.memberId = memberId;
        this.timeTableId = timeTableId;
        this.screenRow = screenRow;
        this.screenCol = screenCol;
        this.price = price;
        this.type = type;
        this.rsvTime = Timestamp.valueOf(rsvTime);
        this.account = account;
        this.bank = bank;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getMemberId()
    {
        return memberId;
    }
    
    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }
    
    public String getTimeTableId()
    {
        return timeTableId;
    }
    
    public void setTimeTableId(String timeTableId)
    {
        this.timeTableId = timeTableId;
    }
    
    public int getScreenRow()
    {
        return screenRow;
    }
    
    public void setScreenRow(int screenRow)
    {
        this.screenRow = screenRow;
    }
    
    public int getScreenCol()
    {
        return screenCol;
    }
    
    public void setScreenCol(int screenCol)
    {
        this.screenCol = screenCol;
    }
    
    public int getPrice()
    {
        return price;
    }
    
    public void setPrice(int price)
    {
        this.price = price;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Timestamp getRsvTime()
    {
        return rsvTime;
    }
    
    public void setRsvTime(Timestamp rsvTime)
    {
        this.rsvTime = rsvTime;
    }
    
    public String getAccount()
    {
        return account;
    }
    
    public void setAccount(String account)
    {
        this.account = account;
    }
    
    public String getBank()
    {
        return bank;
    }
    
    public void setBank(String bank)
    {
        this.bank = bank;
    }
    
}

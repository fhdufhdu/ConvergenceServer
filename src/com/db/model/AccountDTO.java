package com.db.model;

public class AccountDTO
{
    private String account;
    private String bank;
    private int money;
    
    public AccountDTO(String account, String bank, int money)
    {
        this.account = account;
        this.bank = bank;
        this.money = money;
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
    
    public int getMoney()
    {
        return money;
    }
    
    public void setMoney(int money)
    {
        this.money = money;
    }
    
}

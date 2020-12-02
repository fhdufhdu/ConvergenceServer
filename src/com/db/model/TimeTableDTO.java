package com.db.model;

import java.sql.Timestamp;

public class TimeTableDTO extends DTO
{
    private String id;
    private String movieId;
    private String screenId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String type;
    private int current_rsv;
    
    public TimeTableDTO(String id, String movieId, String screenId, String startTime, String endTime, String type, int current_rsv)
    {
        // 날짜 2018-09-21 10:53:00.0 의 형태로
        this.id = id;
        this.movieId = movieId;
        this.screenId = screenId;
        this.startTime = Timestamp.valueOf(startTime);
        this.endTime = Timestamp.valueOf(endTime);
        this.type = type; // plsql 만들기
        this.current_rsv = current_rsv;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getMovieId()
    {
        return movieId;
    }
    
    public void setMovieId(String movieId)
    {
        this.movieId = movieId;
    }
    
    public String getScreenId()
    {
        return screenId;
    }
    
    public void setScreenId(String screenId)
    {
        this.screenId = screenId;
    }
    
    public Timestamp getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(String startTime)
    {
        this.startTime = Timestamp.valueOf(startTime);
    }
    
    public Timestamp getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(String endTime)
    {
        this.endTime = Timestamp.valueOf(endTime);
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public int getCurrentRsv()
    {
        return current_rsv;
    }
    
    public void setCurrentRsv(int current_rsv)
    {
        this.current_rsv = current_rsv;
    }
}

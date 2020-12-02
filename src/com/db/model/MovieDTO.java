package com.db.model;

import java.sql.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MovieDTO extends DTO
{
    private String id;
    private String title;
    private Date releaseDate; // date
    private String isCurrent;
    private String plot;
    private String posterPath;
    private String stillCutPath;
    private String trailerPath;
    private String director;
    private String actor;
    private int min;
    
    public MovieDTO(String id, String title, String releaseDate, String isCurrent, String plot, String posterPath, String stillCutPath, String trailerPath, String director, String actor, int min)
    {
        // releaseDate의 포맷은 "YYYY-MM-DD"
        this.id = id;
        this.title = title;
        this.releaseDate = Date.valueOf(releaseDate);
        this.isCurrent = isCurrent;
        this.plot = plot;
        this.posterPath = posterPath;
        this.stillCutPath = stillCutPath;
        this.trailerPath = trailerPath;
        this.director = director;
        this.actor = actor;
        this.min = min;
    }
    
    public StringProperty getScreeningProperty()
    {
        if (isCurrent.equals("0"))
        {
            return new SimpleStringProperty("상영종료");
        }
        else if (isCurrent.equals("1"))
        {
            return new SimpleStringProperty("상영중");
        }
        else
        {
            return new SimpleStringProperty("상영예정");
        }
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public Date getReleaseDate()
    {
        return releaseDate;
    }
    
    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = Date.valueOf(releaseDate);
    }
    
    public String getIsCurrent()
    {
        return isCurrent;
    }
    
    public void setIsCurrent(String isCurrent)
    {
        this.isCurrent = isCurrent;
    }
    
    public String getPlot()
    {
        return plot;
    }
    
    public void setPlot(String plot)
    {
        this.plot = plot;
    }
    
    public String getPosterPath()
    {
        return posterPath;
    }
    
    public void setPosterPath(String posterPath)
    {
        this.posterPath = posterPath;
    }
    
    public String getStillCutPath()
    {
        return stillCutPath;
    }
    
    public void setStillCutPath(String stillCutPath)
    {
        this.stillCutPath = stillCutPath;
    }
    
    public String getTrailerPath()
    {
        return trailerPath;
    }
    
    public void setTrailerPath(String trailerPath)
    {
        this.trailerPath = trailerPath;
    }
    
    public String getDirector()
    {
        return director;
    }
    
    public void setDirector(String director)
    {
        this.director = director;
    }
    
    public String getActor()
    {
        return actor;
    }
    
    public void setActor(String actor)
    {
        this.actor = actor;
    }
    
    public int getMin()
    {
        return min;
    }
    
    public void setMin(int min)
    {
        this.min = min;
    }
}

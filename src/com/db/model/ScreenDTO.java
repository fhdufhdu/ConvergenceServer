package com.db.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScreenDTO extends DTO {
    private String id;
    private String theater_id;
    private String name;
    private int total_capacity;
    private int max_row;
    private int max_col;

    public ScreenDTO(String id, String theater_id, String name, int total_capacity, int max_row, int max_col) {
        this.id = id;
        this.theater_id = theater_id;
        this.name = name;
        this.total_capacity = total_capacity;
        this.max_row = max_row;
        this.max_col = max_col;
    }

    public ScreenDTO(String theater_id, String name, int total_capacity, int max_row, int max_col) {
        this.id = EMPTY_ID;
        this.theater_id = theater_id;
        this.name = name;
        this.total_capacity = total_capacity;
        this.max_row = max_row;
        this.max_col = max_col;
    }

    public ScreenDTO() {
        id = EMPTY_ID;
        theater_id = EMPTY_ID;
        name = null;
        total_capacity = -1;
        max_row = -1;
        max_col = -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTheaterId() {
        return theater_id;
    }

    public void setTheaterId(String theater_id) {
        this.theater_id = theater_id;
    }

    public String getName() {
        return name;
    }

    public StringProperty getNameProperty() {
        return new SimpleStringProperty(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalCapacity() {
        return total_capacity;
    }

    public StringProperty getTotalCapacityProperty() {
        return new SimpleStringProperty(Integer.toString(total_capacity));
    }

    public void setTotalCapacity(int total_capacity) {
        this.total_capacity = total_capacity;
    }

    public int getMaxRow() {
        return max_row;
    }

    public StringProperty getMaxRowProperty() {
        return new SimpleStringProperty(Integer.toString(max_row));
    }

    public void setMaxRow(int max_row) {
        this.max_row = max_row;
    }

    public int getMaxCol() {
        return max_col;
    }

    public StringProperty getMaxColProperty() {
        return new SimpleStringProperty(Integer.toString(max_col));
    }

    public void setMaxCol(int max_col) {
        this.max_col = max_col;
    }

}

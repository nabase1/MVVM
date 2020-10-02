package com.nabase1.mvvm.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Notes {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int priority;
    private long timeStamp;

    public Notes(String title, String description, long timeStamp, int priority) {
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getPriority() {
        return priority;
    }
}

package com.nabase1.my_diary.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Notes {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private int backgroundColor;
    private int textColor;
    private long timeStamp;
    private String fontFamily;

    public Notes(String title, String description, String fontFamily, long timeStamp, int textColor, int backgroundColor) {
        this.title = title;
        this.description = description;
        this.fontFamily = fontFamily;
        this.timeStamp = timeStamp;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
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

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public String getFontFamily() {
        return fontFamily;
    }
}

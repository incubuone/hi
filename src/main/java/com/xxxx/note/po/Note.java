package com.xxxx.note.po;
import java.util.Date;

public class Note {

    private Integer noteId; // 云记ID
    private String title; // 云记标题
    private String content; // 云记内容
    private Integer typeId; // 云记类型ID
    private Date pubTime; // 发布时间

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", typeId=" + typeId +
                ", pubTime=" + pubTime +
                ", lon=" + lon +
                ", lat=" + lat +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private Float lon; // 经度
    private Float lat; // 纬度


    private String typeName;
}

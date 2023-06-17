package com.xxxx.note.vo;

public class NoteVo {
   private String groupName; // 分组名称
    private long noteCount; // 云记数量

    private Integer typeId; // 类型ID

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(long noteCount) {
        this.noteCount = noteCount;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}

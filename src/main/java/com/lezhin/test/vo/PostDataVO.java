package com.lezhin.test.vo;

import java.io.Serializable;
import java.util.Date;

public class PostDataVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 461632777252863152L;

    private int id = 0;
    private int userId = 0;
    private Date date = null;
    private String content = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

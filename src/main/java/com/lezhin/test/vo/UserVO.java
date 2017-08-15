package com.lezhin.test.vo;

import java.io.Serializable;

public class UserVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3564282308084883008L;

    private int userId = 0;
    private String name = null;
    private String showName = null;
    private String apiAccessKey = null;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getApiAccessKey() {
        return apiAccessKey;
    }

    public void setApiAccessKey(String apiAccessKey) {
        this.apiAccessKey = apiAccessKey;
    }

}

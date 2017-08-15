package com.lezhin.test.vo;

import java.io.Serializable;

public class FollowerVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2032395646008717597L;

    private int userId = 0;
    private int followId = 0;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFollowId() {
        return followId;
    }

    public void setFollowId(int followId) {
        this.followId = followId;
    }

}

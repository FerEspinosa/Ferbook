package com.ferbook.models;

public class Like {
    private String postId;
    private String userId;
    private String likeId;
    private long timestamp;

    public Like(){

    }

    public Like(String postId, String userId, String likeId, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.likeId = likeId;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}


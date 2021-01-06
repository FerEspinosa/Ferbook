package com.ferbook.models;

public class Comment {

    private String comment;
    private String userId;
    private String postId;
    private long   timestamp;

    public Comment () {

    }

    public Comment(String comment, String userId, String postId, long timestamp) {
        this.comment = comment;
        this.userId = userId;
        this.postId = postId;
        this.timestamp = timestamp;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.kptech.peps.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Comment implements Serializable {

    @SerializedName("comment_text")
    private String comment_text;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("comment_ID")
    private String comment_ID;
    @SerializedName("comment_img")
    private String comment_img;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("user_pic")
    private String user_pic;

    private String user_id;

    public Comment() {
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getComment_ID() {
        return comment_ID;
    }

    public void setComment_ID(String comment_ID) {
        this.comment_ID = comment_ID;
    }

    public String getComment_img() {
        return comment_img;
    }

    public void setComment_img(String comment_img) {
        this.comment_img = comment_img;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}


package com.kptech.peps.model;

import com.google.gson.annotations.SerializedName;

public class PostDetails {
    @SerializedName("comments")
    private boolean comments;

    @SerializedName("post_likes")
    private Integer post_likes;

    @SerializedName("description")
    private String description;

    @SerializedName("fromSpecial")
    private boolean fromSpecial;

    @SerializedName("row_key")
    private String row_key;

    @SerializedName("post_image")
    private String post_image;

    @SerializedName("is_mature_content")
    private boolean is_mature_content = false;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("email")
    private String email="";

    @SerializedName("user_ID")
    private String user_ID;


    public String getEmail() {
        return email;
    }

    public Integer getPost_likes(){
        return post_likes;
    }

    public void setPost_likes(Integer like) {
        this.post_likes = like;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getComments() {
        return comments;
    }

    public void setComments(boolean comment) {
        this.comments = comment;
    }

    public Boolean getFromSpecial() {
        return fromSpecial;
    }

    public void setFromSpecial(boolean special) {
        this.fromSpecial = special;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public boolean isIs_mature_content() {
        return is_mature_content;
    }

    public void setIs_mature_content(boolean is_mature_content) {
        this.is_mature_content = is_mature_content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }
}

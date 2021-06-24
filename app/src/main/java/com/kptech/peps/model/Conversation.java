package com.kptech.peps.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Conversation implements Serializable {

    private boolean fromC2G;
    private boolean fromVerified;
    private String id;
    private String name;
    private String other_user_id;

    @SerializedName("latest_message")
    private LatestMessage latest_message;

    public Conversation(){}

    public boolean getFromC2G(){
        return fromC2G;
    }

    public void setFromC2G(boolean fromC2G) {
        this.fromC2G = fromC2G;
    }

    public boolean getFromVerified(){
        return fromVerified;
    }

    public void setFromVerified(boolean fromVerified) {
        this.fromVerified = fromVerified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOther_user_id() {
        return other_user_id;
    }

    public void setOther_user_id(String other_user_id) {
        this.other_user_id = other_user_id;
    }

    public LatestMessage getLatestMessage(){
        return latest_message;
    }

    public void setLatest_message(LatestMessage latest_message) {
        this.latest_message = latest_message;
    }
}

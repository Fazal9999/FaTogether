
package com.kptech.peps.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentRequestData {

    @SerializedName("account_name")
    @Expose
    private String account_name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;
    @SerializedName("is_adult_material")
    @Expose
    private String is_adult_material;
    @SerializedName("last_name")
    @Expose
    private String last_name;
    @SerializedName("row_key")
    @Expose
    private String row_key;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("request_type")
    @Expose
    private String request_type;

    private Long start_at;
    private Long end_at;

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getIs_adult_material() {
        return is_adult_material;
    }

    public void setIs_adult_material(String is_adult_material) {
        this.is_adult_material = is_adult_material;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Long getStart_at() {
        return start_at;
    }

    public void setStart_at(Long start_at) {
        this.start_at = start_at;
    }

    public Long getEnd_at() {
        return end_at;
    }

    public void setEnd_at(Long end_at) {
        this.end_at = end_at;
    }
}

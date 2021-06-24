
package com.kptech.peps.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsRequestData {

    @SerializedName("company_name")
    @Expose
    private String company_name;
    @SerializedName("contact_info")
    @Expose
    private String contact_info;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;
    @SerializedName("is_considered_news_outlet")
    @Expose
    private String is_considered_news_outlet;
    @SerializedName("is_niche_news_outlet")
    @Expose
    private String is_niche_news_outlet;
    @SerializedName("is_official_rep")
    @Expose
    private String is_official_rep;
    @SerializedName("last_name")
    @Expose
    private String last_name;
    @SerializedName("name_of_contact")
    @Expose
    private String name_of_contact;
    @SerializedName("org_name")
    @Expose
    private String org_name;
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

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getContact_info() {
        return contact_info;
    }

    public void setContact_info(String contact_info) {
        this.contact_info = contact_info;
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

    public String getIs_considered_news_outlet() {
        return is_considered_news_outlet;
    }

    public void setIs_considered_news_outlet(String is_considered_news_outlet) {
        this.is_considered_news_outlet = is_considered_news_outlet;
    }

    public String getIs_niche_news_outlet() {
        return is_niche_news_outlet;
    }

    public void setIs_niche_news_outlet(String is_niche_news_outlet) {
        this.is_niche_news_outlet = is_niche_news_outlet;
    }

    public String getIs_official_rep() {
        return is_official_rep;
    }

    public void setIs_official_rep(String is_official_rep) {
        this.is_official_rep = is_official_rep;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getName_of_contact() {
        return name_of_contact;
    }

    public void setName_of_contact(String name_of_contact) {
        this.name_of_contact = name_of_contact;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
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

}

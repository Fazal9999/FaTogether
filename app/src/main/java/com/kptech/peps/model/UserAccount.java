package com.kptech.peps.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by suchandra on 7/19/2017.
 */

public class UserAccount {

    public String user_id = "";
    private String email = "";
    private String first_name;
    private String last_name;
//    private Long created_at;
//    private Long updated_at;
    @SerializedName("gender")
    private String gender="";
    private String profile_pic = "";
    private String user_bio = "";
    private String date_of_birth;
    private ArrayList<String> interests;
    private boolean special_account = false;
    private boolean isC2G = false;
    private boolean isVerified = false;
    public boolean lastName_public = true;
    public boolean email_public = true;
    public boolean dateOfBirth_public = true;
    public boolean gender_public = true;

    public boolean getIsC2G() {
        return isC2G;
    }

    public void setIsC2G(boolean c2G) {
        this.isC2G = c2G;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean verified) {
        this.isVerified = verified;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

//    public Long getUpdated_at() {
//        return updated_at;
//    }
//
//    public void setUpdated_at(Long updated_at) {
//        this.updated_at = updated_at;
//    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String image_url) {
        this.profile_pic = image_url;
    }

    public String getUser_bio() {
        return user_bio;
    }

    public void setUser_bio(String bio) {
        this.user_bio = bio;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public UserAccount() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public boolean getIsSpecial_account() {
        return special_account;
    }

    public void setSpecial_account(boolean special_account) {
        this.special_account = special_account;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean getLastName_public() {
        return lastName_public;
    }

    public void setLastName_public(boolean name_public) {
        this.lastName_public = name_public;
    }

//    public Long getCreated_at() {
//        return created_at;
//    }
//
//    public void setCreated_at(Long created_at) {
//        this.created_at = created_at;
//    }

    public boolean getEmail_public() {
        return email_public;
    }

    public void setEmail_public(boolean email_public) {
        this.email_public = email_public;
    }

    public boolean getDateOfBirth_public() {
        return dateOfBirth_public;
    }

    public void setDateOfBirth_public(boolean dateOfBirth_public) {
        this.dateOfBirth_public = dateOfBirth_public;
    }

    public boolean getGender_public() {
        return gender_public;
    }

    public void setGender_public(boolean gender_public) {
        this.gender_public = gender_public;
    }
}

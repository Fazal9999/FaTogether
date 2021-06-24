package com.kptech.peps.model;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldWideProfile {

    public String full_name;
    public String uid;
    public String email = "";
    public String first_name;
    public String last_name;
    public Long updated_at;

    public String date_of_birth = "";

    public boolean is_authenticate_user = false;
    public boolean is_worldwide_available = false;
    public int is_content_account_available = 0;
    public int is_news_account_available = 0;
    public int is_podcast_account_available = 0;

    public String row_key = "";
    public String back_id = "";
    public String front_id = "";
    public String selfie_id = "";
    public String worldwide_user_id = "";
}

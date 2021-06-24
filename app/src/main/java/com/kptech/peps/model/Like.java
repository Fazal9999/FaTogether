package com.kptech.peps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Like  implements Parcelable {
    @SerializedName("comment_text")
    private String comment_text;
    @SerializedName("created_at")
    private Long created_at;
    @SerializedName("email")
    private String email;
    @SerializedName("row_key")
    private String row_key;
    @SerializedName("user_name")
    private String user_name;
    @SerializedName("updated_at")
    private Long updated_at;
    @SerializedName("user_id")
    private String user_id;
    private String profile_url;

    public Like() {
    }

    protected Like(Parcel in) {
        comment_text = in.readString();
        if (in.readByte() == 0) {
            created_at = null;
        } else {
            created_at = in.readLong();
        }
        email = in.readString();
        row_key = in.readString();
        user_name = in.readString();
        if (in.readByte() == 0) {
            updated_at = null;
        } else {
            updated_at = in.readLong();
        }
        user_id = in.readString();
        profile_url = in.readString();
    }

    public static final Creator<Like> CREATOR = new Creator<Like>() {
        @Override
        public Like createFromParcel(Parcel in) {
            return new Like(in);
        }

        @Override
        public Like[] newArray(int size) {
            return new Like[size];
        }
    };

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(comment_text);
        if (created_at == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(created_at);
        }
        parcel.writeString(email);
        parcel.writeString(row_key);
        parcel.writeString(user_name);
        if (updated_at == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(updated_at);
        }
        parcel.writeString(user_id);
        parcel.writeString(profile_url);
    }
}

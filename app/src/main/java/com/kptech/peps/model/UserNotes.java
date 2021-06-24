package com.kptech.peps.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserNotes {

    private String notesFrom;
    private String age;
    private String iAm;
    private String lookingFor;
    private double distance;
    private String seeking;
    public boolean respondWithPic;
    private String notes;
    private String imageUrl;
    private Location userLocation;
    private String userKey;
    private Long created_at;

    public long allowOther;
    public String whocanseeage="";
    public String whocanseegender="";
    private String row_Key="";

    public HashMap<String, NotesComment> notes_users;

    public String getRow_Key() {
        return row_Key;
    }

    public void setRow_Key(String row_Key) {
        this.row_Key = row_Key;
    }



    public List<NotesComment> getNotesComment() {
        List<NotesComment> notesComments = new ArrayList<>();
        if (notes_users != null) {
            Iterator it = notes_users.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
               notesComments.add((NotesComment) pair.getValue());
            }
        }
        return notesComments;

    }

    public String getNotesFrom() {
        return notesFrom;
    }

    public void setNotesFrom(String notesFrom) {
        this.notesFrom = notesFrom;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getiAm() {
        return iAm;
    }

    public void setiAm(String iAm) {
        this.iAm = iAm;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getSeeking() {
        return seeking;
    }

    public void setSeeking(String seeking) {
        this.seeking = seeking;
    }

    public boolean isRespondWithPic() {
        return respondWithPic;
    }

    public void setRespondWithPic(boolean respondWithPic) {
        this.respondWithPic = respondWithPic;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }
}

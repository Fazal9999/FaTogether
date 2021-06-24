package com.kptech.peps.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NotesComment {
    public HashMap<String, CommentsItems> comments;
    public String email = "";
    public String first_name = "";
    public String last_name = "";
    public String user_id = "";
    public String row_Key = "";

    public HashMap<String, CommentsItems> CommentsItems;
    public String profile_url = "";

    public HashMap<String, com.kptech.peps.model.CommentsItems> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, com.kptech.peps.model.CommentsItems> comments) {
        this.comments = comments;
    }

    public List<CommentsItems> getCommentItem() {
        List<CommentsItems> commentsItems = new ArrayList<>();
        if (comments != null) {
            Iterator it = comments.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                commentsItems.add((CommentsItems) pair.getValue());
            }
        }
        return commentsItems;
    }
}

package com.kptech.peps.server.firebase;

/**
 * Created by suchandra on 8/29/2017.
 */

public interface ResponseItemListener {
     void onItemAdded(Object response);
     void onFailure(String error);
}

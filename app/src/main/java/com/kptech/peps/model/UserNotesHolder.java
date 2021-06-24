package com.kptech.peps.model;

import android.util.Log;

import com.kptech.peps.interfaces.DataChangeListener;
import com.kptech.peps.utils.DataValidator;

import java.util.ArrayList;
import java.util.List;

public class UserNotesHolder {
    private static final String TAG = UserNotesHolder.class.getName();
    private static final UserNotesHolder ourInstance = new UserNotesHolder();

    public static UserNotesHolder getInstance() {
        return ourInstance;
    }

    private UserNotesHolder() {
    }

    private List<UserNotes> mDataItems = new ArrayList<>();
    private List<DataChangeListener> mNotifiers = new ArrayList<>();

    public List<UserNotes> getmDataItems() {
        return mDataItems;
    }

    public void setmDataItems(List<UserNotes> mDataItems) {
        this.mDataItems = mDataItems;
    }

    public void addItem(UserNotes data){
        Log.d(TAG,"add item");
        mDataItems.add(data);
        notifyListeners();

    }

    public List<UserNotes> getSearchList(String searchKey){
        List<UserNotes> list = new ArrayList<>();
        for(UserNotes data:mDataItems){
            String val = data.getNotes();
            if(DataValidator.isValid(val) && val.contains(searchKey)){
                list.add(data);
            }
        }

        return list;
    }

    public List<DataChangeListener> getmNotifiers() {
        return mNotifiers;
    }

    public void setmNotifiers(List<DataChangeListener> mNotifiers) {
        this.mNotifiers = mNotifiers;
    }

    public void addListener(DataChangeListener listener){
        mNotifiers.add(listener);
    }

    public void removeListener(DataChangeListener listener){
        if(mNotifiers.contains(listener)){
            mNotifiers.remove(listener);
        }
    }
    public void notifyListeners(){
        for(DataChangeListener listener:mNotifiers){
            listener.onDataChanged();
        }
    }

    public void clear(){
        mDataItems.clear();
        mNotifiers.clear();
    }
}

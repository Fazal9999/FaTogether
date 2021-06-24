package com.kptech.peps.model;

public class DataHolder {
    private static final DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private DataHolder() { }

    private UserAccount mCurrentUser = null;

    public UserAccount getmCurrentUser() {
        return mCurrentUser;
    }

    public void setmCurrentUser(UserAccount mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }
}

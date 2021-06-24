package com.kptech.peps.model;

import com.kptech.peps.interfaces.DataChangeListener;
import com.kptech.peps.utils.DataValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostsDataHolder {

    private static final PostsDataHolder ourInstance = new PostsDataHolder();

    public static PostsDataHolder getInstance() {
        return ourInstance;
    }

    private PostsDataHolder() {
    }

    private List<DataChangeListener> mNotifiers = new ArrayList<>();
    private List<PostDetails> mHomePosts = new ArrayList<>();

    private HashMap<String , List<PostDetails>> mUserCreatedPosts = new HashMap<>();

    public  void addPosts(PostDetails post){
        mHomePosts.add(post);
        notifyListeners();
    }

    public List<PostDetails> getmHomePosts(){
        return mHomePosts;
    }

    public void addUserPost(PostDetails post){
        String type = post.getUser_ID();
        if(DataValidator.isValid(type)){
            List<PostDetails> list = mUserCreatedPosts.get(type);
            if(list == null){
                list = new ArrayList<>();
                mUserCreatedPosts.put(type,list);
            }
            list.add(post);

        }
    }


    public List<PostDetails> getUserPostsByType(String type){
        return mUserCreatedPosts.get(type);
    }

    public int getUserPostsCount(){
        return mUserCreatedPosts.size();
    }

    public void clearUserPosts(){
        mUserCreatedPosts.clear();
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
        mHomePosts.clear();
        mNotifiers.clear();
        mUserCreatedPosts.clear();

    }
}

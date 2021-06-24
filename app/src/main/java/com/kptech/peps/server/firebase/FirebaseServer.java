package com.kptech.peps.server.firebase;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.ContentRequestData;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.Like;
import com.kptech.peps.model.NewsRequestData;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.PostsDataHolder;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.server.BackendServer;
import com.kptech.peps.server.BackendServerConstants;
import com.kptech.peps.server.ResponseReceiver;
import com.kptech.peps.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by suchandra on 7/19/2017.
 */

public class FirebaseServer {
    private static final String TAG = FirebaseServer.class.getName();
    private static final FirebaseServer ourInstance = new FirebaseServer();
    private Context mContext;

    private FirebaseAuth mAuth;
    private HashMap<String, ChildEventListener> mChildEventListeners = new HashMap<String, ChildEventListener>();
    private HashMap<String, ValueEventListener> mValueEventListeners = new HashMap<String, ValueEventListener>();
    private HashMap<String, DatabaseReference> mDbReferences = new HashMap<String, DatabaseReference>();
    private HashMap<String, ResponseItemListener> mDataListeners = new HashMap<String, ResponseItemListener>();
    private List<String> mphotolist = new ArrayList<String>();


    public static FirebaseServer getInstance() {
        return ourInstance;
    }

    private FirebaseServer() {
    }

    public void initialize(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        if (mAuth == null)
            return null;
        else
            return mAuth.getCurrentUser();
    }


    public void changePassword(String pwd, final ResponseReceiver listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(pwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Update pwd successful");
                        if (listener != null)
                            listener.onSuccess(200, "Success");
                    } else {
                        Log.e(TAG, "Password update failed");
                        if (listener != null)
                            listener.onError(BackendServerConstants.PASSWORD_UPDATE_FAILED);
                    }
                }
            });
        }
    }

    public void signup(final Activity act, final UserAccount account, String pwd, final ResponseReceiver listener) {
        Log.d(TAG, "Do sign up");
        if (mAuth != null) {
            mAuth.createUserWithEmailAndPassword(account.getEmail(), pwd).addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        final FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "verify email");
                        if (user != null) {
                            createProfile(account, listener);
                        } else {
                            if (listener != null)
                                listener.onError(BackendServerConstants.SIGNUP_FAILED);
                        }

                    } else {
                        String errormsg = "Unable to signup";
                        errormsg = task.getException().getMessage();
                        if (listener != null)
                            listener.onError(errormsg);
                    }
                }
            });
        }
    }

    public void forgotPassword(String email, final ResponseReceiver listener) {
        Log.d(TAG, "Forgot password");
        if (mAuth != null) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (listener != null)
                            listener.onSuccess(BackendServer.RESULT_SUCCESS, BackendServerConstants.PASSWORD_RESET_EMAIL);
                    } else {
                        String errormsg = "Unable to send password reset link. Please try again later";
                        if (task.getException() != null && task.getException().getMessage() != null)
                            errormsg = task.getException().getMessage();

                        if (listener != null)
                            listener.onError(errormsg);
                    }
                }
            });
        }
    }

    public void signin(Activity act, String email, String password, final ResponseReceiver listener) {
        Log.d(TAG, "Do sign in");
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && listener != null) {
                            UserAccount userAccount = new UserAccount();
                            userAccount.setEmail(user.getEmail());
                           // userAccount.setStatus("online");

                           /* if (user.getDisplayName() != null) {
                                userAccount.setFull_name(user.getDisplayName());
                            }*/
                            listener.onSuccess(200, userAccount);
                            /*if (true || user.isEmailVerified()) {
                                listener.onSuccess(200, user);
                            } else {
                                listener.onFailure("Email not verified. Please verify your email");
                            }*/
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        String errormsg = "failed to login";
                        if (task.getException() != null) {
                            errormsg = task.getException().getMessage();

                        }
                        if (listener != null)
                            listener.onError(errormsg);
                    }
                }
            });
        }

    }

    public void fetchProfile(String key, final ResponseReceiver listener) {
        Log.d(TAG, "Fetch profile for user");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.USERS);
        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Received profile data");
                UserAccount account = dataSnapshot.getValue(UserAccount.class);
                if (account == null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    account = new UserAccount();
                    account.setEmail(user.getEmail());
                }
                if (listener != null)
                    listener.onSuccess(200, account);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (listener != null)
                    listener.onError("Error fetching profile");
            }
        });

    }

    public void updateProfile(String userid, final UserAccount account, final ResponseReceiver listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.USERS);
        myRef.child(userid).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listener != null)
                        listener.onSuccess(BackendServer.RESULT_SUCCESS, account);
                } else {
                    String errormsg = "Unable to update profile";
                    if (task.getException() != null && task.getException().getMessage() != null) {
                        errormsg = task.getException().getMessage();
                    }
                    if (listener != null)
                        listener.onError(errormsg);
                }

            }
        });


    }

    public void createProfile(final UserAccount account, final ResponseReceiver listener) {
        Log.d(TAG, "Create profile");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.USERS);
        FirebaseUser user = mAuth.getCurrentUser();
        String key = Utils.getKey(account.getEmail());
        if (key == null)
            key = user.getUid();

        if (user != null) {
            myRef.child(key).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Create profile successfull");
                        if (listener != null)
                            listener.onSuccess(BackendServerConstants.SUCCESS, account);
                    } else {
                        String errormsg = "Unable to create profile ";
                        if (task.getException() != null) {
                            errormsg = task.getException().getLocalizedMessage();
                        }

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null)
                            user.delete();

                        if (listener != null)
                            listener.onError(errormsg);

                        Log.e(TAG, "Unable to create profile");
                    }
                }
            });
        }

    }

    public void fetchHomePosts(final ResponseItemListener listener) {
        Log.d(TAG, "fetch group list");
        String key = FirebaseConstants.POSTS_LIST;
        if (mChildEventListeners.get(key) == null) {
            mDataListeners.put(key, listener);
            mChildEventListeners.put(key, new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "Received product list");
                    PostDetails info = dataSnapshot.getValue(PostDetails.class);
                    if (info != null) {
                        PostsDataHolder.getInstance().addPosts(info);
                    }

                    if (listener != null)
                        listener.onItemAdded("Success");

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "data changed");

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "child removed");

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "child moved");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "transaction cancelled");

                }
            });

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbref = database.getReference(FirebaseConstants.POSTS_LIST);
            Query groupPostsQuery = dbref.orderByChild("interest_key").limitToLast(20);
            mDbReferences.put(key, groupPostsQuery.getRef());
            groupPostsQuery.addChildEventListener(mChildEventListeners.get(key));
            groupPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (listener != null)
                        listener.onItemAdded("Success");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    public void createNewsRequest(NewsRequestData data, final ResponseReceiver listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference(FirebaseConstants.SPECIAL_ACCOUNT_REQUESTS);
        String keyLcl = dbref.push().getKey();
        data.setRow_key(keyLcl);
        dbref.child(data.getRow_key()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onSuccess(BackendServer.RESULT_SUCCESS, BackendServerConstants.SUCCESS_MSG);
                }

            }
        });

    }

    public void createContentRequest(ContentRequestData data, final ResponseReceiver listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference(FirebaseConstants.SPECIAL_ACCOUNT_REQUESTS);
        String keyLcl = dbref.push().getKey();
        data.setRow_key(keyLcl);
        dbref.child(keyLcl).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onSuccess(BackendServer.RESULT_SUCCESS, BackendServerConstants.SUCCESS_MSG);
                }

            }
        });

    }

    public void fetchPostsForUser(String userId, ResponseReceiver listener) {
        Log.d(TAG, "fetch user post  list");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbref = database.getReference(FirebaseConstants.POSTS_LIST);
        Query groupPostsQuery = dbref.orderByChild("email").equalTo(userId);

        groupPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Received user created post list");
                PostsDataHolder.getInstance().clearUserPosts();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PostDetails data = snapshot.getValue(PostDetails.class);
                    if (data != null) {
                        PostsDataHolder.getInstance().addUserPost(data);
                    }
                }

                if (listener != null) {
                    listener.onSuccess(BackendServer.RESULT_SUCCESS, "Success");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null) {
                    listener.onError("Error fetching posts");
                }

            }
        });


    }


    public void unregisterListeners() {
        Log.d(TAG, "Remove all the listeners");

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        try {
            for (String listkey : mChildEventListeners.keySet()) {
                ChildEventListener listener = mChildEventListeners.get(listkey);
                if (listener != null) {
                    dbref.removeEventListener(listener);
                }

            }

            for (String listkey : mValueEventListeners.keySet()) {
                ValueEventListener listener = mValueEventListeners.get(listkey);
                if (listener != null) {
                    dbref.removeEventListener(listener);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mChildEventListeners.clear();
            mValueEventListeners.clear();
            mDataListeners.clear();
            mDbReferences.clear();
        }
    }

    public void updateAuthenticationDetails(final HashMap<String, Object> upaatedDetails, final ResponseReceiver listener) {
        UserAccount account = DataHolder.getInstance().getmCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.WORLDWIDE_REQUESTS);
        FirebaseUser user = mAuth.getCurrentUser();
        String key = Utils.getKey(account.getEmail());
        if (key == null)
            key = user.getUid();

        if (user != null) {
            myRef.child(key).updateChildren(upaatedDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (listener != null)
                            listener.onSuccess(BackendServer.RESULT_SUCCESS, BackendServerConstants.AUTH_DETAILS_UPDATED);
                    } else {
                        String errormsg = "Unable to update details now";
                        if (task.getException() != null && task.getException().getMessage() != null)
                            errormsg = task.getException().getMessage();

                        if (listener != null)
                            listener.onError(errormsg);
                    }
                }
            });
        }

    }


    public void createHomePost(PostDetails postDetails, ResponseReceiver listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.POSTS_LIST);
        String keyLcl = myRef.push().getKey();
        postDetails.setRow_key(keyLcl);
        postDetails.setCreated_at(postDetails.getCreated_at());
       // postDetails.setUpdated_at(System.currentTimeMillis());
        myRef.child(keyLcl).setValue(postDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listener != null)
                        listener.onSuccess(BackendServer.RESULT_SUCCESS, BackendServerConstants.POST_CREATED_SUCESSFULLY);
                } else {
                    String errormsg = "Something went wrong";
                    if (task.getException() != null && task.getException().getMessage() != null)
                        errormsg = task.getException().getMessage();

                    if (listener != null)
                        listener.onError(errormsg);
                }
            }
        });
    }

    public void createWorldWidePost(PostDetails postDetails, ResponseReceiver listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.POSTS_LIST);
        String keyLcl = myRef.push().getKey();
        postDetails.setRow_key(keyLcl);
        //postDetails.setCreated_at(System.currentTimeMillis());
        //postDetails.setUpdated_at(ServerValue.TIMESTAMP);
        myRef.child(keyLcl).setValue(postDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (listener != null)
                        listener.onSuccess(BackendServer.RESULT_SUCCESS, BackendServerConstants.POST_CREATED_SUCESSFULLY);
                } else {
                    String errormsg = "Something went wrong";
                    if (task.getException() != null && task.getException().getMessage() != null)
                        errormsg = task.getException().getMessage();

                    if (listener != null)
                        listener.onError(errormsg);
                }
            }
        });
    }

    public void postLikeToHome(String postId, int likes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseConstants.POSTS_LIST).child(postId).child("post_likes");
        myRef.setValue(likes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {

                }
            }
        });
    }
}

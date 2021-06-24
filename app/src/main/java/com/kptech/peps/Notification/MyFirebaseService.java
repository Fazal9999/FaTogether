package com.kptech.peps.Notification;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kptech.peps.model.DataHolder;
import com.kptech.peps.model.UserAccount;

public class MyFirebaseService extends FirebaseInstanceIdService {
    private UserAccount userAccount;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        userAccount = DataHolder.getInstance().getmCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (userAccount != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        String current_user_id = userAccount.getEmail().replaceAll("[-+.^:,@]","");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        reference.child(current_user_id).setValue(token);
    }
}

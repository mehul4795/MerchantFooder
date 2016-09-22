package com.blackdogs.merchantfooderbooktable;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.blackdogs.merchantfooderbooktable.Pojo.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by BlackDogs on 14-09-2016.
 */
public class Login extends AppCompatActivity {

    private DatabaseReference mDatabase, userRef;
    FirebaseAuth auth;

    String uid,uName, email,profileImgUrl;

    private static final int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userRef = mDatabase.child("users");
        auth = FirebaseAuth.getInstance();


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(
                                AuthUI.EMAIL_PROVIDER,
                                AuthUI.GOOGLE_PROVIDER,
                                AuthUI.FACEBOOK_PROVIDER)
                        .build(),
                RC_SIGN_IN);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!

                uName = auth.getCurrentUser().getDisplayName();
                email = auth.getCurrentUser().getEmail();
                uid = auth.getCurrentUser().getUid();
                profileImgUrl = String.valueOf(auth.getCurrentUser().getPhotoUrl());

                User user = new User(uName, email,profileImgUrl);
                userRef.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    }
                });


                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
                Toast.makeText(Login.this, "Login failed! Please Try Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }
}

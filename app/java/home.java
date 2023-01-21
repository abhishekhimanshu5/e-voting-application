package com.example.e_voting_samadhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    TextView name;
    FirebaseAuth mAuth;
    BottomNavigationView bnv;
//    ArrayList<model> datalist;
//    ArrayList<String> votedUserList;
//    ArrayList<String> docID;
//    myAdapter myAdapter;
//    RecyclerView recyclerView;
//    LinearLayoutManager lm;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new homeFragment()).commit();

        name = findViewById(R.id.name);
        bnv = findViewById(R.id.bottom_navigation);
        mAuth = FirebaseAuth.getInstance();

        //-------------------------------------------------------------

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (signInAccount != null) {
            uid = firebaseUser.getUid();
            name.setText(firebaseUser.getDisplayName());
        }

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp = null;
                switch (item.getItemId()) {
                    case R.id.home_menu:
                        temp = new homeFragment();
                        break;
                    case R.id.history_menu:
                        temp = new historyFragment();
                        break;
                    case R.id.vote_menu:
                        temp = new createVoteFragment();
                        break;
                    case R.id.account_menu:
                        temp = new accountFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, temp).commit();
                return true;
            }
        });


    }



}
package com.example.e_suplemenpintar_admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserFormActivity extends Activity {
    private Button logout;
    RecyclerView recview;
    myadapter adapter;
    List<model> model;
    DatabaseReference databaseReference;
    String uid;
    SwipeRefreshLayout refreshLayout;
    TextView a, b, c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        a=(TextView)findViewById(R.id.nametext);
        b=(TextView)findViewById(R.id.emailtext);
        c=(TextView)findViewById(R.id.healthtext);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userform);
        setTitle("Cari di sini...");
        refreshLayout=findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        recview=(RecyclerView)findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));
        model=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("pengguna");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    for (DataSnapshot ds2:ds.getChildren()) {
                        model data = ds2.getValue(model.class);
                        model.add(0, data);
                    }
                }
                adapter=new myadapter(model);
                recview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(logout.getContext());
                builder.setTitle("Log Keluar");
                builder.setMessage("Anda pasti ingin log keluar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        SharedPreferences sharedPreferences = getSharedPreferences("logindata", MODE_PRIVATE);
                        sharedPreferences.edit().clear().commit();
                        Intent intent = new Intent(UserFormActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkuserstatus();
    }

    void checkuserstatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("logindata", MODE_PRIVATE);
        Boolean counter = sharedPreferences.getBoolean("logincounter", Boolean.valueOf(String.valueOf(MODE_PRIVATE)));
        String email = sharedPreferences.getString("useremail", String.valueOf(MODE_PRIVATE));
        if (!counter) {
            startActivity(new Intent(UserFormActivity.this, MainActivity.class));
            finish();
        }
    }

}

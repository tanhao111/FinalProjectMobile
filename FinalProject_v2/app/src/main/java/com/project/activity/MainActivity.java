package com.project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.adapter.ShowProductAdapter;
import com.project.models.Product;
import com.project.util.Const;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * CHECK SHARED PREFERENCES IF HAVE INFORMATION OF USER OR NOT
         * IF NOT FORWARD TO ACTIVITY LOGIN
         */
        Intent intent;
        SharedPreferences mPref =getApplicationContext().getSharedPreferences(Const.SHARED_PREFERENCE, MODE_PRIVATE);
        String userData = mPref.getString(Const.USER_KEY, "");
        if(userData == null || userData.compareTo("") == 0){
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // THIS IS MAIN, SHOW ALL PRODUCTS
        // calling this activity's function to use ActionBAr utility method
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("");
        actionBar.setIcon(R.drawable.ic_action_home);

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        gridView = findViewById(R.id.gridView);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("data/products");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, Product>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Product>>() {};
                Map<String, Product> productList = snapshot.getValue(genericTypeIndicator);

                if(productList != null) {
                    ShowProductAdapter showProductAdapter = new ShowProductAdapter(getApplicationContext(), productList);
                    gridView.setAdapter(showProductAdapter);
                    gridView.setOnItemClickListener(MainActivity.this);
                }else{
                    Toast.makeText(getApplicationContext(), "No have data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something error when fetch data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.account:
                intent = new Intent(getApplicationContext(), ManagerAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.cart:
                intent = new Intent(getApplicationContext(), ShowBasketActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
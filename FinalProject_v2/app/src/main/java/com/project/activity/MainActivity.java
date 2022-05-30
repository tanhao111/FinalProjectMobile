package com.project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.project.adapter.ShowProductAdapter;
import com.project.models.Product;
import com.project.util.Const;
import com.project.util.StringSimilarity;

import java.util.HashMap;
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

        //Start service
        if(!foregroundServiceRunning()){
            Intent service = new Intent(getApplicationContext(), ChatService.class);
            startForegroundService(service);
        }

        // THIS IS MAIN, SHOW ALL PRODUCTS
        // calling this activity's function to use ActionBAr utility method
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("");
        actionBar.setIcon(R.drawable.ic_action_home);

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        gridView = findViewById(R.id.gridView);

        // get data
        intent = getIntent();
        String query =  intent.getStringExtra("query");

        getDataAndShow(query);

    }

    private boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(ChatService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void getDataAndShow(String query){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("data/products");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, Product>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Product>>() {};
                Map<String, Product> productList = snapshot.getValue(genericTypeIndicator);
                if(productList != null) {
                    ShowProductAdapter showProductAdapter;
                    if(query == null){
                        showProductAdapter = new ShowProductAdapter(getApplicationContext(), productList);
                    }else{
                        Map<String, Product> resultQuery =  new HashMap<>();
                        for(Map.Entry<String, Product> ele: productList.entrySet()){
                            Product product = ele.getValue();
                            if(StringSimilarity.isMatch(product.getProductName(), query) || StringSimilarity.isMatch(product.getProductType(), query)){
                                resultQuery.put(ele.getKey(), ele.getValue());
                            }
                        }
                        showProductAdapter = new ShowProductAdapter(getApplicationContext(), resultQuery);
                    }

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = getIntent();
                finish();
                intent.putExtra("query", s);
                startActivity(intent);
                //getDataAndShow(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void forward(Class c){
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.account:
                forward(ManagerAccountActivity.class);
                break;
            case R.id.cart:
                forward(ShowBasketActivity.class);
                break;
            case R.id.message:
                forward(ChatActivity.class);
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
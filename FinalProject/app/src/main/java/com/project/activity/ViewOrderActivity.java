package com.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.project.DAO.OrderDao;
import com.project.adapter.ViewOrderAdapter;
import com.project.model.ItemProduct;
import com.project.model.Order;
import com.project.model.User;
import com.project.util.Const;

import java.util.Map;

public class ViewOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private TextView txtUserName, txtUserAddress, txtUserPhone, txtTotal;
    private Button btnUpdateStatus;

    private int totalCost = 0, status;
    private String basketKey, userKey, orderKey;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);
        initialize();
        getData();
    }

    private void initialize(){

        // get data user from SharedPreferences;
        Intent intent = getIntent();
        orderKey = intent.getStringExtra("orderKey");
        basketKey = intent.getStringExtra("basketKey");
        userKey = intent.getStringExtra("userKey");
        totalCost = Integer.parseInt(intent.getStringExtra("total"));
        status =  Integer.parseInt(intent.getStringExtra("status"));
        order = new Order(basketKey, userKey,totalCost ,status);

        listView = findViewById(R.id.listViewCheckOut);
        txtUserName = findViewById(R.id.txtUsername);
        txtUserAddress = findViewById(R.id.txtUserAddress);
        txtUserPhone = findViewById(R.id.txtUserPhone);
        txtTotal = findViewById(R.id.txtTotal);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        btnUpdateStatus.setOnClickListener(this);
        if(status  == Const.PACKING){
            btnUpdateStatus.setText("Packaged");
        }else if(status == Const.SHIPPING){
            btnUpdateStatus.setText("Shipped");
        }else if(status == Const.DONE){
            btnUpdateStatus.setText("Done");
            btnUpdateStatus.setEnabled(false);
        }

        String s = "Total : " + totalCost;
        txtTotal.setText(s);

    }

    private void getData(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef;

        dbRef = db.getReference("data/users/"+ userKey);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                txtUserName.setText("Username: "+ user.getUsername());
                txtUserPhone.setText("Phone Number: " + user.getPhoneNumber());
                txtUserAddress.setText("Address: " + user.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbRef = db.getReference("data/basket/");
        dbRef.child(basketKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<Map<String, ItemProduct>> genericTypeIndicator = new GenericTypeIndicator<Map<String, ItemProduct>>() {};
                Map<String, ItemProduct> list = snapshot.child("/productList/0/").getValue(genericTypeIndicator);

                for(Map.Entry<String, ItemProduct> ele: list.entrySet()){
                    totalCost = totalCost + ele.getValue().getTotal();
                }

                ViewOrderAdapter adapter = new ViewOrderAdapter(getApplicationContext(), list);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        order.setStatus(status + 1);
        OrderDao orderDao = new OrderDao();
        orderDao.updateOrder(orderKey, order);
        Toast.makeText(this, "Order status updated", Toast.LENGTH_SHORT).show();

        //send email

        Intent intent = new Intent(this, OrderFragment.class);
        startActivity(intent);
    }
}
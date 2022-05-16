package com.project.databaseDao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.models.Orders;

public class OrderDao {
        private FirebaseDatabase db;
        private DatabaseReference dbRef;

        public OrderDao(){
                db = FirebaseDatabase.getInstance();
                dbRef = db.getReference("data/orders/");
        }


        public void createNewOrder(Orders orders){
                dbRef.push().setValue(orders);
        }
}

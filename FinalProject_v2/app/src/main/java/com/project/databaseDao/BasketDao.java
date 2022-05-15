package com.project.databaseDao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.models.Basket;
import com.project.models.ItemProduct;

public class BasketDao {

    private FirebaseDatabase db;
    private DatabaseReference dbRef;

    public BasketDao(){
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("data/basket/");
    }

    /**
     *
     * @param basket not have a any item
     * @return
     */
    public String createNewBasket(Basket basket, ItemProduct itemProduct){
        String key = dbRef.push().getKey();
        dbRef.child(key).setValue(basket);
        dbRef.child(key+"/productList/0/").push().setValue(itemProduct);
        return key;
    }

    public void addNewProductToBasket(String key, ItemProduct product){
        dbRef.child(key+"/productList/0/").push().setValue(product);
    }

    public void deleteItemOnBasket(String keyBasket, String keyItem){
        dbRef.child(keyBasket + "/productList/0/"+keyItem).removeValue();
    }

    public void updateQuantityItemOnBasket(String keyBasket, String keyItem, ItemProduct item){
        dbRef.child(keyBasket+"/productList/0/"+keyItem).setValue(item);
    }


}

package com.project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.databaseDao.UserDao;
import com.project.models.User;
import com.project.util.Const;

public class ManagerAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtUserName, txtEmail, txtPhone, txtAddress;
    private  TextView txtId;
    private Button btnUpdateAccount, btnLogOut;
    private boolean isUpdate;
    private String key;
    private User user;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_account);

        initialize();
        setEnable(false);
    }

    private void initialize(){
        isUpdate = false;

        gson = new Gson();
        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SHARED_PREFERENCE, MODE_PRIVATE);
        String json = sharedPreferences.getString(Const.USER_DATA, "");
        key  = sharedPreferences.getString(Const.USER_KEY, "");
        user = gson.fromJson(json, User.class);

        txtId = findViewById(R.id.txtKey);
        txtId.setText("User Key: " + key);

        txtAddress = findViewById(R.id.txtAddress);
        txtAddress.setText(user.getAddress());

        txtUserName = findViewById(R.id.txtUsername);
        txtUserName.setText(user.getUsername());

        txtEmail = findViewById(R.id.txtEmail);
        txtEmail.setText(user.getEmail());

        txtPhone = findViewById(R.id.txtPhone);
        txtPhone.setText(user.getPhoneNumber());

        btnUpdateAccount = findViewById(R.id.btnUpdateAccount);
        btnUpdateAccount.setOnClickListener(this);

        btnLogOut =findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(this);
    }

    private void setEnable(boolean isEnable){
        txtAddress.setEnabled(isEnable);
        txtPhone.setEnabled(isEnable);
        txtEmail.setEnabled(isEnable);
        txtUserName.setEnabled(isEnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUpdateAccount:
                if(isUpdate){
                    User newUser = new User();
                    newUser.setPassword(user.getPassword());
                    newUser.setAddress(txtAddress.getText().toString());
                    newUser.setEmail(txtEmail.getText().toString());
                    newUser.setPhoneNumber(txtPhone.getText().toString());
                    newUser.setUsername(txtUserName.getText().toString());

                    UserDao userDao = new UserDao();
                    userDao.updateUser(key, newUser);

                    String json = gson.toJson(newUser);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(Const.USER_DATA);
                    editor.putString(Const.USER_DATA, json);
                    editor.commit();

                    Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                    isUpdate = false;
                    setEnable(false);

                }else{
                    isUpdate = true;
                    setEnable(true);
                }
                break;
            case R.id.btnLogout:
                SharedPreferences.Editor  editor = sharedPreferences.edit();
                editor.remove(Const.USER_DATA);
                editor.remove(Const.USER_KEY);
                editor.remove(Const.BASKET_KEY);
                editor.commit();

                Intent a = new Intent(this, MainActivity.class);
                startActivity(a);
                break;
            default: break;
        }


    }
}
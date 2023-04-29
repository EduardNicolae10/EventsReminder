package com.eduard.eventsreminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import entities.UserModel;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername;
    EditText edtPassword;
    Button loginBTN;
    TextView registerTXT;

    dbManager db = new dbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        loginBTN = findViewById(R.id.loginButton);
        registerTXT = findViewById(R.id.registerText);

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = getIntent().getStringExtra("email");

                UserModel userModel = new UserModel(username,password,email);

                if(userModel.getUsername().length()==0 || userModel.getPassword().length()==0){
                    Toast.makeText(getApplicationContext(), "Please fill all details!", Toast.LENGTH_SHORT).show();
                } else {
                    if(db.login(userModel) == 1){                                                                                                   //check if the user exists in db
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("username",username);                                                                                      //save the datas with key and value,just to use them in another activities
                        editor.putString("password",password);
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("username",username));        //if the result returned by login method is 1
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();                     //if the result returned by login method is -1
                    }
                }
            }
        });

        registerTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

}
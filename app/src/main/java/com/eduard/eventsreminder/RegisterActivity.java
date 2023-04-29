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

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername;
    EditText edtPassword;
    EditText edtEmail;
    EditText edtConfirmPassword;
    Button registerBTN;
    TextView tvAlreadyRegistered;
    dbManager db = new dbManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtRegusername);                                            //identify all fields
        edtPassword = findViewById(R.id.edtRegPassword);
        edtEmail = findViewById(R.id.edtRegEmail);
        edtConfirmPassword = findViewById(R.id.edtRegConfirmPassword);
        registerBTN = findViewById(R.id.RegButton);
        tvAlreadyRegistered = findViewById(R.id.tvRegAlready);

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();                          //initialize all variables with identified fields values
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                new Intent(RegisterActivity.this,LoginActivity.class).putExtra("email",email);              //send the email to login to creat an object UserModel

                UserModel userModel = new UserModel(username,password,email);

                if(userModel.getUsername().length() == 0 || userModel.getPassword().length() == 0 || userModel.getEmail().length() ==0 || confirmPassword.length()==0){     //verify that all fields are completed
                    Toast.makeText(getApplicationContext(), "Please fill all details!", Toast.LENGTH_SHORT).show();
                } else {
                    if(userModel.getPassword().compareTo(confirmPassword)==0){                                                                                          //verify if the password equals the confirmpassword
                        if(isValid(userModel.getPassword())){                                                                                                           //verify if the password is valid by calling the isValid method
                            db.register(userModel);                                                                                                                     //if the password is valid , just add the user into db
                            Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Password must contain at least 8 charachters, 1 letter, 1 digit and 1 special symbol!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Passwords do not match! Please Try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvAlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }



    //TODO: implement the password validation method
    public static boolean isValid(String passwordHere){
        int f1=0,f2=0,f3=0;
        if(passwordHere.length() < 8){                                                              //minimum 8 characters condition
            return false;
        }else{
            for(int p=0; p < passwordHere.length(); p++){
                if(Character.isLetter(passwordHere.charAt(p))) {                                    //containing letters condition
                    f1=1;
                }
            }
            for(int r=0; r < passwordHere.length(); r++){
                if(Character.isDigit(passwordHere.charAt(r))){                                      //containing digits condition
                    f2=1;
                }
            }
            for(int s=0; s < passwordHere.length(); s++){                                           //containing special symbol condition
                char c = passwordHere.charAt(s);
                if(c >= 33 && c<=46 || c==64){
                    f3=1;
                }
            }
            if(f1==1 && f2==1 && f3==1)                                                             //if all the condition are fulfilled then the password is valid
                return true;
            return false;
        }
    }
}
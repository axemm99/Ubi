package com.example.ubi_interfaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ubi_interfaces.classes.Globals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    EditText createEmail;
    EditText createUsername;
    EditText createPassword;
    EditText confirmPassword;
    Button buttonSignup;
    TextView goLogIn;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        createEmail = findViewById(R.id.createEmail);
        createPassword = findViewById(R.id.createPassword);
        createUsername = findViewById(R.id.createUsername);
        confirmPassword = findViewById(R.id.confirmPassword);
        buttonSignup = findViewById(R.id.buttonSignup);
        goLogIn = findViewById(R.id.goLogIn);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createEmail.getText().toString().isEmpty() && createPassword.getText().toString().isEmpty() && createUsername.getText().toString().isEmpty() && confirmPassword.getText().toString().isEmpty()){
                    Toast.makeText(Signup.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                else if (createPassword.getText().toString() == confirmPassword.getText().toString()){
                    Toast.makeText(Signup.this, "Passwords must be the same", Toast.LENGTH_SHORT).show();
                }
                else if (createPassword.length() < 6){
                    Toast.makeText(Signup.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }
                else {
                    fAuth.createUserWithEmailAndPassword(createEmail.getText().toString(), createPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Signup.this, "User Created", Toast.LENGTH_SHORT).show();
                                final Task <AuthResult> n = task;

                                // Apanhar o user criado
                                FirebaseUser newUser = fAuth.getCurrentUser();
                                String uid = newUser.getUid();


                                /* Acho que não vai ser preciso o user id, para além dos users autenticados pelo face ou google */
                                Map<String, Object> newUserData = new HashMap<>();
                                newUserData.put("name", createUsername.getText().toString());
                                newUserData.put("email", createEmail.getText().toString());
                                // newUserData.put("password", createPassword.getText().toString()); Não é preciso guardar isto na BD, já é gradado na autenticação do firebase
                                newUserData.put("achievments", new HashMap<String, Integer>());
                                newUserData.put("picture", "users/default_user_image.jpeg");
                                newUserData.put("performanceId", -1);
                                newUserData.put("authType", "firebase");



                                // Criar utilizador na firestore
                                db.collection("users").document(uid)
                                        .set(newUserData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.w("RegisterUSer", String.valueOf(n));
                                                Globals.goToActivity(getApplicationContext(), BottomNav.class);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Fazer uma toast depois
                                            Log.w("RegisterUsser", e);
                                        }
                                });

                            }
                            else {
                                Toast.makeText(Signup.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        /* Botão para ir para o login */
        goLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.goToActivity(getApplicationContext(), Login.class);
            }
        });
    }


}

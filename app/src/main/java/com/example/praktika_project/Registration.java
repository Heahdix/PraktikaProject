package com.example.praktika_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.User;

public class Registration extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText loginEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button regButton;
    private Button authButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("User");

        loginEditText = findViewById(R.id.loginEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        regButton = findViewById(R.id.regButton);
        authButton = findViewById(R.id.authButton);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, Authorization.class));
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(passwordEditText.getText().toString()) || TextUtils.isEmpty(emailEditText.getText().toString()) || TextUtils.isEmpty(loginEditText.getText().toString())) {
                    Toast.makeText(Registration.this, "Пустые поля", Toast.LENGTH_SHORT).show();
                }
                else if (passwordEditText.getText().toString().length() < 6){
                    Toast.makeText(Registration.this, "Слишком короткий пароль", Toast.LENGTH_SHORT).show();
                }
                else if (!emailEditText.getText().toString().matches(emailPattern)){
                    Toast.makeText(Registration.this, "Введите действительный email", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User user = dataSnapshot.getValue(User.class);
                                if (user.getEmail().equals(emailEditText.getText().toString())){
                                    Toast.makeText(Registration.this, "Такой email уже зарегестрирован", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    User user = new User(mAuth.getCurrentUser().getUid(), loginEditText.getText().toString(), passwordEditText.getText().toString(), emailEditText.getText().toString(), null, null);
                                    mDatabase.child(mAuth.getCurrentUser().getUid()).setValue(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Registration.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Registration.this, Authorization.class));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Registration.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });

                        };

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }
}
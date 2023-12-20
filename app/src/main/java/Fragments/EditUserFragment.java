package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.praktika_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Model.User;

public class EditUserFragment extends Fragment {


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button buttonSave;
    private Button backButton;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);

        buttonSave = view.findViewById(R.id.buttonSave);
        backButton = view.findViewById(R.id.backButton);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        mAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getText().toString().length() < 6){
                    Toast.makeText(view.getContext(), "Слишком короткий пароль", Toast.LENGTH_SHORT).show();
                }
                else if (!emailEditText.getText().toString().matches(emailPattern)){
                    Toast.makeText(view.getContext(), "Введите действительный email", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User user = dataSnapshot.getValue(User.class);
                                if (user.getEmail().equals(emailEditText.getText().toString()) && !user.getId().toString().equals(mAuth.getCurrentUser().getUid())){
                                    Toast.makeText(view.getContext(), "Такой email уже зарегестрирован", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updateEmail(emailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        HashMap<String, Object> userMap = new HashMap<>();
                                        userMap.put("email", emailEditText.getText().toString());

                                        mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                                        user.updatePassword(passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                HashMap<String, Object> userMap = new HashMap<>();
                                                userMap.put("password", passwordEditText.getText().toString());

                                                mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }
                                        });
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        getUserInfo();

        return view;
    }

    private void getUserInfo(){
        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                    emailEditText.setText(snapshot.child("email").getValue().toString());
                    passwordEditText.setText(snapshot.child("password").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
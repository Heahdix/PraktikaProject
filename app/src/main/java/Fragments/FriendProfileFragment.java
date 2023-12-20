package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.praktika_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileFragment extends Fragment {
    private User user;
    private Button btnFollow;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginTextView;
    private TextView statusTextView;
    private CircleImageView profilePicture;
    private Button notesButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_profile, container, false);

        btnFollow = view.findViewById(R.id.btnFollow);
        loginTextView = view.findViewById(R.id.loginText);
        statusTextView = view.findViewById(R.id.statusText);
        profilePicture = view.findViewById(R.id.profilePicture);
        notesButton = view.findViewById(R.id.notesButton);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Follow");

        getUserInfo();

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FreindNotesFragment(user);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnFollow.getText().toString().toLowerCase().equals(("отправить запрос")) || btnFollow.getText().toString().toLowerCase().equals(("ответить на запрос"))){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(mAuth.getCurrentUser().getUid()).child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(mAuth.getCurrentUser().getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(mAuth.getCurrentUser().getUid()).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(mAuth.getCurrentUser().getUid()).removeValue();
                }
            }
        });

        return view;
    }

    private void getUserInfo() {
        if (user.getImageurl() != null){
            String image = user.getImageurl();
            Picasso.get().load(image).into(profilePicture);
        }
        if (user.getStatus() != null) {
            statusTextView.setText(user.getStatus());
        } else {
            statusTextView.setText("Нет статуса");
        }
        loginTextView.setText(user.getLogin());

        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("following").child(user.getId()).exists() && snapshot.child("followers").child(user.getId()).exists()) {
                    btnFollow.setText("В друзьях");
                }
                else if (snapshot.child("following").child(user.getId()).exists() && !snapshot.child("followers").child(user.getId()).exists()) {
                    btnFollow.setText("Запрос отправлен");
                }
                else if (!snapshot.child("following").child(user.getId()).exists() && snapshot.child("followers").child(user.getId()).exists()) {
                    btnFollow.setText("Ответить на запрос");
                }
                else {
                    btnFollow.setText("Отправить запрос");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public FriendProfileFragment(User user){
        this.user = user;
    }
}
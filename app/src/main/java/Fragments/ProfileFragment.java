package Fragments;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.praktika_project.Authorization;
import com.example.praktika_project.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private String myUri;
    private Uri imageUri;
    private StorageReference mStorage;
    private StorageTask uploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginTextView;
    private TextView statusTextView;
    private Button saveButton;
    private Button editButton;
    private CircleImageView profilePicture;
    private Button exitAccButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_profile, container, false);

         loginTextView = view.findViewById(R.id.loginText);
         statusTextView = view.findViewById(R.id.statusText);
         saveButton = view.findViewById(R.id.buttonSave);
         editButton = view.findViewById(R.id.editProfileButton);
         profilePicture = view.findViewById(R.id.profilePicture);
         exitAccButton = view.findViewById(R.id.exitButton);

         mStorage = FirebaseStorage.getInstance().getReference().child("Profile pic");
         mAuth = FirebaseAuth.getInstance();
         mDatabase = FirebaseDatabase.getInstance().getReference("User");

        editButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Fragment fragment = new EditUserFragment();
                 FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                 fragmentTransaction.replace(R.id.fragment_container, fragment);
                 fragmentTransaction.addToBackStack(null);
                 fragmentTransaction.commit();
             }
         });

        exitAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileFragment.this.getActivity(), Authorization.class));
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                uploadProfilePicture();
            }
        });

        getUserInfo();

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                alert.setTitle("Логин");

                final EditText input = new EditText(view.getContext());
                input.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                        (ConstraintLayout.LayoutParams.WRAP_CONTENT , ConstraintLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                input.setLayoutParams(layoutParams);

                mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                            input.setText(snapshot.child("login").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();

                        if (!TextUtils.isEmpty(value)){
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("login", value);

                            mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        }

                        else {
                            Toast.makeText(view.getContext(), "Пустой логин", Toast.LENGTH_SHORT);
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        statusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                alert.setTitle("Статус");

                final EditText input = new EditText(view.getContext());
                input.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                        (ConstraintLayout.LayoutParams.WRAP_CONTENT , ConstraintLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                input.setLayoutParams(layoutParams);

                mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                            if (snapshot.child("status").getValue() != null){
                                input.setText(snapshot.child("status").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("status", value);

                        mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        return view;
    }

    private void uploadProfilePicture(){
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        if(imageUri != null){
            StorageReference fileRef = mStorage.child(mAuth.getCurrentUser().getUid()+".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = (Uri)task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("imageurl", myUri);

                        mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        saveButton.setVisibility(GONE);

                        progressDialog.dismiss();
                    }
                }
            });
        }
        else{
            progressDialog.dismiss();
        }
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    private void getUserInfo(){
        mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                    loginTextView.setText(snapshot.child("login").getValue().toString());
                    if (snapshot.hasChild("imageurl") && snapshot.child("imageurl").getValue() != null){
                        String image = snapshot.child("imageurl").getValue().toString();
                        Picasso.get().load(image).into(profilePicture);
                    }
                    if (snapshot.hasChild("status") && snapshot.child("status").getValue() != null){
                        statusTextView.setText(snapshot.child("status").getValue().toString());
                    }
                    else {
                        statusTextView.setText("Нет статуса");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.praktika_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import Model.Note;

public class NoteFragment extends Fragment {

    private Note note;

    private DatabaseReference mDatabase;
    private TextView sender;
    private TextView deadline;
    private TextView endTime;
    private TextView header;
    private TextView content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        header = view.findViewById(R.id.header);
        sender = view.findViewById(R.id.sender);
        content = view.findViewById(R.id.content);
        deadline = view.findViewById(R.id.deadline);
        endTime = view.findViewById(R.id.endTime);

        mDatabase = FirebaseDatabase.getInstance().getReference("User");

        Date deadlineDate = new Date(note.getDeadline());
        Date endDate = new Date(note.getTofinish());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

        endTime.setText(format.format(endDate.getTime()));
        deadline.setText(format.format(deadlineDate.getTime()));

        header.setText(note.getHeader());
        content.setText(note.getContent());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (note.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    sender.setText("Вы");
                }
                else{
                    sender.setText(snapshot.child(note.getSender()).child("login").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public NoteFragment(Note note){
        this.note = note;
    }
}
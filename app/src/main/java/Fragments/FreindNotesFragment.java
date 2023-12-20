package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.praktika_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.NoteAdapter;
import Model.Note;
import Model.User;

public class FreindNotesFragment extends Fragment {

    private RecyclerView recyclerViewForOddNotes;
    private RecyclerView recyclerViewForNotOddNotes;
    private NoteAdapter noteOddAdapter;
    private NoteAdapter noteNotOddAdapter;
    private List<Note> mNotesOdd;
    private List<Note> mNotesNotOdd;
    private User recipient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freind_notes, container, false);

        recyclerViewForOddNotes = view.findViewById(R.id.recycler_view_notes_odd);
        recyclerViewForOddNotes.setHasFixedSize(true);
        recyclerViewForOddNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewForNotOddNotes = view.findViewById(R.id.recycler_view_notes_not_odd);
        recyclerViewForNotOddNotes.setHasFixedSize(true);
        recyclerViewForNotOddNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        NoteAdapter.OnNoteClickListener noteClickListener = new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note, int position) {
                Fragment fragment = new NoteFragment(note);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };

        mNotesOdd = new ArrayList<>();
        mNotesNotOdd = new ArrayList<>();
        noteOddAdapter = new NoteAdapter(getContext(), mNotesOdd, noteClickListener);
        recyclerViewForOddNotes.setAdapter(noteOddAdapter);
        noteNotOddAdapter = new NoteAdapter(getContext(), mNotesNotOdd, noteClickListener);
        recyclerViewForNotOddNotes.setAdapter(noteNotOddAdapter);

        readNotes();

        return view;
    }

    private void readNotes(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Note");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mNotesOdd.clear();
                mNotesNotOdd.clear();

                int i = 1;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Note note = dataSnapshot.getValue(Note.class);
                    if (note.getRecipient().equals(recipient.getId()) && note.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if (i % 2 == 1){
                            mNotesNotOdd.add(note);
                            i++;
                        }
                        else {
                            mNotesOdd.add(note);
                            i++;
                        }
                    }
                }
                noteNotOddAdapter.notifyDataSetChanged();
                noteOddAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public FreindNotesFragment(User user){
        recipient = user;
    }
}
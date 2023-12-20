package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praktika_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Model.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    public interface OnNoteClickListener{
        void onNoteClick(Note note, int position);
    }
    private Context mContext;
    private List<Note> mNotes;

    private final OnNoteClickListener onNoteClickListener;

    public NoteAdapter (Context mContext, List<Note> mNotes, OnNoteClickListener onNoteClickListener){
        this.mContext = mContext;
        this.mNotes = mNotes;
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Note note = mNotes.get(position);

        holder.header.setText(note.getHeader());
        holder.content.setText(note.getContent());

        FirebaseDatabase.getInstance().getReference().child("User").child(note.getSender()).child("login").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(note.getSender())){
                    holder.author.setText("Вы");
                }
                else{
                    holder.author.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNoteClickListener.onNoteClick(note, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView header;
        public TextView content;
        public TextView author;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            header = itemView.findViewById(R.id.header);
            content = itemView.findViewById(R.id.content);
            author = itemView.findViewById(R.id.author);
        }
    }
}

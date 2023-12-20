package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.praktika_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Model.Note;

public class AddNoteFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Spinner spinner;
    private EditText endDate;
    private ArrayList<String> users;
    private Map<String, String> loginKey;
    private EditText title;
    private EditText content;
    private EditText deadline;
    private Button buttonSave;
    private Button backButton;
    private Calendar calendarEndDate;
    private Calendar calendarDeadline;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        context = this.getContext();
        buttonSave = view.findViewById(R.id.buttonSave);
        backButton = view.findViewById(R.id.backButton);
        deadline = view.findViewById(R.id.deadline);
        deadline.setInputType(InputType.TYPE_NULL);
        title = view.findViewById(R.id.title);
        content = view.findViewById((R.id.content));
        calendarEndDate = Calendar.getInstance();
        calendarDeadline = Calendar.getInstance();
        endDate = view.findViewById(R.id.endDate);
        endDate.setInputType(InputType.TYPE_NULL);

        mAuth = FirebaseAuth.getInstance();

        loginKey = new HashMap<String, String>();
        users = new ArrayList<String>();
        spinner = view.findViewById(R.id.userSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(endDate, calendarEndDate);
            }
        });

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(deadline, calendarDeadline);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = "";
                for (Map.Entry<String, String> item : loginKey.entrySet()){
                    if (item.getKey().equals(spinner.getSelectedItem().toString())){
                        recipient = item.getValue();
                    }
                }
                if (TextUtils.isEmpty(title.getText().toString()) || TextUtils.isEmpty(content.getText().toString())){
                    Toast.makeText(view.getContext(), "Пустые поля", Toast.LENGTH_SHORT).show();
                }
                else if (calendarEndDate.getTimeInMillis() > calendarEndDate.getTimeInMillis() || calendarEndDate.before(Calendar.getInstance())){
                    Toast.makeText(view.getContext(), "Неверное время", Toast.LENGTH_SHORT).show();
                }
                else{
                    Note note = new Note(title.getText().toString(), content.getText().toString(), mAuth.getCurrentUser().getUid(), recipient, calendarEndDate.getTimeInMillis(), calendarEndDate.getTimeInMillis());
                    FirebaseDatabase.getInstance().getReference().child("Note").push().setValue(note);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        FirebaseDatabase.getInstance().getReference("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                loginKey.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("followers").child(dataSnapshot.getKey()).exists() && (snapshot.child("following").child(dataSnapshot.getKey()).exists()))
                            {
                                loginKey.put(dataSnapshot.child("login").getValue().toString(), dataSnapshot.getKey());
                                users.add(dataSnapshot.child("login").getValue().toString());
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                loginKey.put("Себе", mAuth.getCurrentUser().getUid());
                users.add("Себе");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner.setAdapter(adapter);

        return view;
    }

    private void showDateTimeDialog(EditText date_time_in, Calendar calendar) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        if (calendar.after(Calendar.getInstance())){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");
                            date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                        }
                        else {
                            Toast.makeText(context, "Выберите верное время", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                new TimePickerDialog(context, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(context, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
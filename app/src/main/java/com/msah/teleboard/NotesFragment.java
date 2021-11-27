package com.msah.teleboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.msah.teleboard.notes.NotesActivity;
import com.msah.teleboard.notes.adapters.NotesAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView notesRecyclerview;
    private NotesAdapter notesAdapter;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        notesAdapter = new NotesAdapter(getContext());
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        notesRecyclerview = view.findViewById(R.id.recyclerview_notes);
        notesRecyclerview.setAdapter(notesAdapter);
        notesRecyclerview.setLayoutManager(lm);
        notesRecyclerview.setHasFixedSize(true);

        FloatingActionButton addNote = view.findViewById(R.id.add_note_fab);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote(view);
            }
        });
        return view;
    }


    public void addNote(View view)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add note title");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Notes title");
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String    noteTitle = input.getText().toString();
                if (TextUtils.isEmpty(noteTitle)){
                    Snackbar.make(view, "Invalid title", Snackbar.LENGTH_SHORT).show();
                }

                Intent notesIntent = new Intent(getContext(), NotesActivity.class);
                notesIntent.putExtra(NotesActivity.NOTE_TITLE, noteTitle);
                startActivity(notesIntent);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    notesAdapter.notifyDataSetChanged();
    }
}
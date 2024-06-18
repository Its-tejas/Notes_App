package com.mydroid.notesapp.Fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mydroid.notesapp.Adapter.NoteAdapter;
import com.mydroid.notesapp.DBHelper;
import com.mydroid.notesapp.Model.NoteModel;
import com.mydroid.notesapp.R;
import com.mydroid.notesapp.databinding.FragmentNotesBinding;

import java.util.ArrayList;

public class NotesFragment extends Fragment {
    private FirebaseAuth auth;
    private FragmentNotesBinding binding;
    private NoteAdapter noteAdapter;
    private DBHelper helper;
    private ArrayList<NoteModel> arrayNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        helper = new DBHelper(getActivity());

        // SharedPreferences is used to set and know app opened 1st time or not
        SharedPreferences SP = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();        // Editor is used to set value in SharedPreferences
        boolean isNewUser = SP.getBoolean("isNewUser", false);

        if (isNewUser == true) {
            // code for write data into database only for 1st time
            helper.addNote("Welcome", "Thank you for using this app");
            helper.addNote("App Features", "1. This Note app do not store any type of note data\n2. This app is very simple to use");
            helper.addNote("Enjoy", "Create note and enjoy App");
            helper.addNote("", "");

            editor.putBoolean("isNewUser", false);
            editor.apply();
        }

        // code for fetch data into arraylist
        arrayNote = helper.getNote();
        noteAdapter = new NoteAdapter(getActivity(), arrayNote);

        int spancount = 2;      // no. of columns in grid
        binding.recyclerView.setAdapter(noteAdapter);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spancount, StaggeredGridLayoutManager.VERTICAL));

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayNote.clear(); // Clear the existing data
                arrayNote.addAll(helper.getNote()); // Fetch updated data from the database
                noteAdapter.notifyDataSetChanged(); // Notify adapter about the change();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_DialogWhenLarge_NoActionBar);
                dialog.setContentView(R.layout.note_detailed_view);

                EditText titleofNote = dialog.findViewById(R.id.titleofNote);
                EditText textofNote = dialog.findViewById(R.id.textofNote);
                FloatingActionButton saveNote = dialog.findViewById(R.id.saveNote);
                ImageView backbtn = dialog.findViewById(R.id.backbtn);

                backbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                saveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String TITLE = titleofNote.getText().toString();
                        String TEXT = textofNote.getText().toString();

                        if (!TITLE.equals("") && !TEXT.equals("")) {
                            int res = helper.addNote(TITLE, TEXT);
                            if (res == 1) {
                                Toast.makeText(getActivity(), "Note Created Successfully", Toast.LENGTH_SHORT).show();
                                updateRecyclerViewData();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(getActivity(), "Note Creation Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please enter some text", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public void updateRecyclerViewData() {
        arrayNote.clear(); // Clear the existing data
        arrayNote.addAll(helper.getNote()); // Fetch updated data from the database
        noteAdapter.notifyDataSetChanged(); // Notify adapter about the change
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
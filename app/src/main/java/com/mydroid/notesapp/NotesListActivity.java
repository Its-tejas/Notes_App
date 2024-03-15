package com.mydroid.notesapp;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.mydroid.notesapp.Adapter.NoteAdapter;
import com.mydroid.notesapp.Model.NoteModel;
import com.mydroid.notesapp.databinding.ActivityNotesListBinding;

import java.util.ArrayList;

public class NotesListActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ActivityNotesListBinding binding;
    NoteAdapter noteAdapter;
    DBHelper helper;
    ArrayList<NoteModel> arrayNote;
    public static int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();

        helper = new DBHelper(this);

        // code for write data into database
        String temp = "SQLite is another data storage available in Android where we can store data in the user’s device and can use it any time when required. In this article, we will take a ";
        String temp2 = "SQLite is another data storage available in Android where we can store data in the user’s device and can use it any time when required. In this article, we will take a look at creating an SQLite database in Android app and adding data to that database in the Android app. This is a series of 4 articles in which we are going to perform the basic CRUD (Create, Read, Update, and Delete) operation with SQLite Database in Android. We are going to cover the following 4 articles in this series:";
//            helper.addNote("First", temp);
//            helper.addNote("Second", "abcd");
//            helper.addNote("Third", "1");
//            helper.addNote("Fourth", temp2);
//            helper.addNote("Fifth", "sytfdwdeqjfcdawefvkawefcvksadgfvkusdagfyuksdgfkyigweasiyfiweyifgtewytgfewfg");


        // code for fetch data
        arrayNote = helper.getNote();

        noteAdapter = new NoteAdapter(this, arrayNote);
//        noteAdapter = new NoteAdapter(this);

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

                Dialog dialog = new Dialog(NotesListActivity.this, android.R.style.Theme_DeviceDefault_Light_DialogWhenLarge_NoActionBar);
                dialog.setContentView(R.layout.note_detailed_view);

                EditText titleofNote = dialog.findViewById(R.id.titleofNote);
                EditText textofNote = dialog.findViewById(R.id.textofNote);
                FloatingActionButton saveNote = dialog.findViewById(R.id.saveNote);

                saveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String TITLE = titleofNote.getText().toString();
                        String TEXT = textofNote.getText().toString();

                        int res = helper.addNote(TITLE, TEXT);
                        if (res == 1) {
                            Toast.makeText(NotesListActivity.this, "Note Created Successfully", Toast.LENGTH_SHORT).show();
                            updateRecyclerViewData();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(NotesListActivity.this, "Note Creation Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.show();

            }
        });


    }
    public void updateRecyclerViewData () {
        arrayNote.clear(); // Clear the existing data
        arrayNote.addAll(new DBHelper(this).getNote()); // Fetch updated data from the database
        noteAdapter.notifyDataSetChanged(); // Notify adapter about the change

    }

}
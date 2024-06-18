package com.mydroid.notesapp.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mydroid.notesapp.DBHelper;
import com.mydroid.notesapp.Model.NoteModel;
import com.mydroid.notesapp.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

    ArrayList<NoteModel> arrayNote;
    Context context;

    public NoteAdapter(Context context, ArrayList<NoteModel> arrayNote) {
        this.arrayNote = arrayNote;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteViewHolder holder, int position) {
        // Code for set recyclerView
        NoteModel model = arrayNote.get(position);
        holder.noteTitle.setText(model.getTitle());
        holder.noteText.setText(model.getText());

        holder.noteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_DialogWhenLarge_NoActionBar);
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

                titleofNote.setText(model.title);
                textofNote.setText(model.text);

                saveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String UPDATED_TITLE = titleofNote.getText().toString();
                        String UPDATED_TEXT = textofNote.getText().toString();
                        int UPDATED_ID = model.id;

                        DBHelper helper = new DBHelper(context);
                        NoteModel model = new NoteModel();

                        model.id = UPDATED_ID;
                        model.title = UPDATED_TITLE;
                        model.text = UPDATED_TEXT;

                        if (!UPDATED_TITLE.equals("") && !UPDATED_TEXT.equals("")) {
                            int isUpadateSuccess = helper.updateNote(model);
                            if (isUpadateSuccess == 1) {
                                Toast.makeText(context, "Note update successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                updateRecyclerViewData();
                            } else {
                                Toast.makeText(context, "Note Deletion Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });



        holder.noteCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Delete Note")
                        .setMessage("Are you sure want to delete this note")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = new DBHelper(context);
                                int res = helper.deleteNote(model.id);
                                if (res == 1) {
                                    Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
                                    updateRecyclerViewData();
                                } else {
                                    Toast.makeText(context, "Note Deletion Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayNote.size();
    }

    public void updateRecyclerViewData () {
        arrayNote.clear(); // Clear the existing data
        arrayNote.addAll(new DBHelper(context).getNote()); // Fetch updated data from the database
        notifyDataSetChanged(); // Notify adapter about the change

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle , noteText;
        CardView noteCard;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteText = itemView.findViewById(R.id.noteText);
            noteCard = itemView.findViewById(R.id.notecard);
        }
    }
}

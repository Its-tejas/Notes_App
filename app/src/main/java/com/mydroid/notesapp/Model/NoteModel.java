package com.mydroid.notesapp.Model;

public class NoteModel {
     public int id;
     public String title, text;

    public NoteModel() {
        // Default constructor required for Firebase
    }

    public NoteModel(String title, String text) {
        this.title = title;
        this.text = text;
    }
    public NoteModel(int id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

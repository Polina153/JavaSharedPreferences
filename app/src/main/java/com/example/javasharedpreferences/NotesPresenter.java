package com.example.javasharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class NotesPresenter {

    private static final String PREF_NAME = "My Preferences";
    private static final String KEY = "key";

    private final NotesView view;
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new GsonBuilder().create();
    private final ArrayList<UserNote> notes = new ArrayList<>();

    public NotesPresenter(NotesView view, Context context) {
        this.view = view;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void loadNotes() {
        String savedNotes = sharedPreferences.getString(KEY, null);
        if (savedNotes == null || savedNotes.isEmpty()) {
            view.showEmptyState();
            return;
        }
        try {
            Type type = new TypeToken<ArrayList<UserNote>>() {}.getType();
            ArrayList<UserNote> loaded = gson.fromJson(savedNotes, type);
            notes.clear();
            if (loaded != null) {
                notes.addAll(loaded);
            }
            if (notes.isEmpty()) {
                view.showEmptyState();
            } else {
                view.showNotes(notes);
            }
        } catch (JsonSyntaxException e) {
            view.showError("Ошибка трансформации");
        }
    }

    public void onAddNoteClicked() {
        UserNote newNote =
                new UserNote("New note", new Date(), "New note");
        notes.add(newNote);
        saveNotes();
        view.showNotes(notes);
        view.showMessage("Заметка добавлена");
    }

    private void saveNotes() {
        String jsonNotes = gson.toJson(notes);
        sharedPreferences.edit().putString(KEY, jsonNotes).apply();
    }
}

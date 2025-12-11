package com.example.javasharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotesViewModel extends ViewModel {

    private static final String PREF_NAME = "My Preferences";
    private static final String KEY = "key";

    private final MutableLiveData<List<UserNote>> notesLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<String> messageLiveData = new SingleLiveEvent<>();

    private SharedPreferences sharedPreferences;
    private Gson gson = new GsonBuilder().create();
    private final ArrayList<UserNote> notes = new ArrayList<>();

    public void init(Context context) {
        if (sharedPreferences != null) return; // уже инициализирован
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadNotesInternal();
    }

    public LiveData<List<UserNote>> getNotes() {
        return notesLiveData;
    }

    public LiveData<String> getMessage() {
        return messageLiveData;
    }

    private void loadNotesInternal() {
        String savedNotes = sharedPreferences.getString(KEY, null);
        if (savedNotes == null || savedNotes.isEmpty()) {
            notes.clear();
            notesLiveData.setValue(new ArrayList<>());
            messageLiveData.setValue("Список пуст");
            return;
        }
        try {
            Type type = new TypeToken<ArrayList<UserNote>>() {}.getType();
            ArrayList<UserNote> loaded = gson.fromJson(savedNotes, type);
            notes.clear();
            if (loaded != null) {
                notes.addAll(loaded);
            }
            notesLiveData.setValue(new ArrayList<>(notes));
        } catch (JsonSyntaxException e) {
            messageLiveData.setValue("Ошибка трансформации");
        }
    }

    public void addNote() {
        UserNote newNote = new UserNote("New note", new Date(), "New note");
        notes.add(newNote);
        saveNotesInternal();
        notesLiveData.setValue(new ArrayList<>(notes));
        messageLiveData.setValue("Заметка добавлена");
    }

    private void saveNotesInternal() {
        String jsonNotes = gson.toJson(notes);
        sharedPreferences.edit().putString(KEY, jsonNotes).apply();
    }
}

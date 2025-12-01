package com.example.javasharedpreferences;

import android.annotation.SuppressLint;

import java.lang.reflect.Type;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPref = null;
    public static final String KEY = "key";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPref = getSharedPreferences("My Preferences", MODE_PRIVATE);

        final ArrayList<UserNote> userNotes = new ArrayList<>();

        final NotesAdapter notesAdapter = new NotesAdapter(userNotes);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(notesAdapter);

        String savedNotes = sharedPref.getString(KEY, null);
        if (savedNotes == null || savedNotes.isEmpty()) {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Type type = new TypeToken<ArrayList<UserNote>>() {
                }.getType();
                notesAdapter.setNewData(new GsonBuilder().create().fromJson(savedNotes, type));
            } catch (JsonSyntaxException e) {
                Toast.makeText(this, "Ошибка трансформации", Toast.LENGTH_SHORT).show();
            }
        }

        findViewById(R.id.fab).setOnClickListener(view -> {
            userNotes.add(new UserNote("New note", new Date(), "New note"));
            notesAdapter.setNewData(userNotes);
            String jsonNotes = new GsonBuilder().create().toJson(userNotes);
            sharedPref.edit().putString(KEY, jsonNotes).apply();
        });
    }
}
package com.example.javasharedpreferences;

import java.util.List;

interface NotesView {
    void showNotes(List<UserNote> notes);
    void showEmptyState();
    void showError(String message);
    void showMessage(String message);
}

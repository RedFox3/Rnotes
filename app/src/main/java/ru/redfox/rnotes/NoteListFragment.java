package ru.redfox.rnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.net.URI;

import ru.redfox.rnotes.db.NoteViewModel;

public class NoteListFragment extends Fragment {

    private RecyclerView noteRecyclerView;
    private FloatingActionButton addNoteFAB;
    private NoteAdapter adapter;
    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteRecyclerView = view.findViewById(R.id.notesRecyclerView);
        addNoteFAB = view.findViewById(R.id.addNoteFAB);

        noteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noteRecyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        noteRecyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            adapter.setNotes(notes);
            noteRecyclerView.scheduleLayoutAnimation();
        });

        adapter.setOnItemClickListener(note -> {
            NoteListFragmentDirections.ActionToEditNoteFragment action =
                    NoteListFragmentDirections.actionToEditNoteFragment(note);
            Navigation.findNavController(view).navigate(action);
        });

        adapter.setOnItemLongClickListener(note -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить заметку")
                    .setMessage("Вы уверены, что хотите удалить эту заметку?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        if (note.getImageUri() != null) {
                            File file = new File(URI.create(note.getImageUri().toString()));
                            file.delete();
                        }
                        noteViewModel.delete(note);
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        addNoteFAB.setOnClickListener(v -> {
            NoteListFragmentDirections.ActionToEditNoteFragment action =
                    NoteListFragmentDirections.actionToEditNoteFragment(null);
            Navigation.findNavController(view).navigate(action);
        });
    }
}

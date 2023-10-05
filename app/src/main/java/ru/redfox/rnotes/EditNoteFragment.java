package ru.redfox.rnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import ru.redfox.rnotes.db.Note;
import ru.redfox.rnotes.db.NoteViewModel;

public class EditNoteFragment extends Fragment {

    private EditText editTextTitle, editTextContent;
    private Button buttonSave;

    private NoteViewModel noteViewModel;
    private Note rootNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        buttonSave = view.findViewById(R.id.buttonSave);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        rootNote = EditNoteFragmentArgs.fromBundle(getArguments()).getNote();
        if (rootNote != null) {
            editTextTitle.setText(rootNote.getTitle());
            editTextContent.setText(rootNote.getContent());
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getActivity(), "Please insert a title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(title, content);
        if (rootNote != null) {
            note.setId(rootNote.getId());
            noteViewModel.update(note);
        } else {
            noteViewModel.insert(note);
        }

        Toast.makeText(getActivity(), "Note saved", Toast.LENGTH_SHORT).show();

        Navigation.findNavController(requireView()).navigateUp();
    }
}

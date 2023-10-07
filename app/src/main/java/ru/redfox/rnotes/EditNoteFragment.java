package ru.redfox.rnotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import ru.redfox.rnotes.db.Note;
import ru.redfox.rnotes.db.NoteViewModel;

public class EditNoteFragment extends Fragment {

    private EditText editTextTitle, editTextContent;
    private ImageView imageNote;
    private Button buttonImage;
    private Button buttonSave;

    private NoteViewModel noteViewModel;
    private Note rootNote;
    private Uri imageUri;
    private boolean isEditing = false;

    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                    Uri selectedImageUri = data.getData();
                    imageUri = selectedImageUri;
                    imageNote.setImageURI(selectedImageUri);
                    imageNote.setVisibility(View.VISIBLE);
                }
            });

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
        imageNote = view.findViewById(R.id.imageNote);
        buttonImage = view.findViewById(R.id.buttonImage);
        buttonSave = view.findViewById(R.id.buttonSave);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        rootNote = EditNoteFragmentArgs.fromBundle(getArguments()).getNote();
        if (rootNote != null) {
            isEditing = true;
            editTextTitle.setText(rootNote.getTitle());
            editTextContent.setText(rootNote.getContent());
            if (rootNote.getImageUri() != null) {
                imageUri = rootNote.getImageUri();
                imageNote.setImageURI(imageUri);
                imageNote.setVisibility(View.VISIBLE);
            }
        }

        imageNote.setOnLongClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить изображение")
                    .setMessage("Вы уверены, что хотите удалить изображение?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        imageUri = null;
                        imageNote.setVisibility(View.GONE);
                    })
                    .setNegativeButton("Нет", null)
                    .show();
            return true;
        });

        buttonImage.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pickImageIntent);
        });

        buttonSave.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getActivity(), "Please insert a title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note(title, content, null);
        if (isEditing) {
            note.setId(rootNote.getId());
            if (rootNote.getImageUri() != null && !rootNote.getImageUri().equals(imageUri)) {
                File file = new File(URI.create(rootNote.getImageUri().toString()));
                file.delete();
            }
        }

        if (imageUri != null) {
            File file = new File(requireContext().getFilesDir(), imageUri.hashCode() + ".jpg");
            if (!file.exists()) {
                InputStream inputStream;
                OutputStream outputStream;
                try {
                    inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                    outputStream = new FileOutputStream(file);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                byte[] buffer = new byte[4096];
                int bytesRead;
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            imageUri = Uri.fromFile(file);
            note.setImageUri(imageUri);
        }

        if (isEditing) {
            noteViewModel.update(note);
        } else {
            noteViewModel.insert(note);
        }

        imageUri = null;

        Toast.makeText(getActivity(), "Note saved", Toast.LENGTH_SHORT).show();

        Navigation.findNavController(requireView()).navigateUp();
    }
}

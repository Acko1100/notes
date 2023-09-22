package rs.ac.singidunum.notes;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    //Deklaracija komponenti korisnickog interfejsa
    EditText titleEditText, contentEditText;
    ImageButton saveNoteButton;
    TextView pageTitleTextView;
    String title, content, documentId;
    Boolean isEdited = false;
    TextView deleteNoteTextViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        //Inicijalizacija komponenti prema njihovim ID-jevima
        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteButton = findViewById(R.id.save_note_button);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewButton = findViewById(R.id.delete_note_text_view_button);

        //Primanje podataka
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        documentId = getIntent().getStringExtra("documentId");

        //Ako postoji documentId znamo da se radi o uredjivanju postojece beleske i zato naslov prelazi u "Edit your note"
        if (documentId != null && !documentId.isEmpty()) {
            isEdited = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if (isEdited) {
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewButton.setVisibility(View.VISIBLE);
        }


        saveNoteButton.setOnClickListener((v) -> saveNote());

        deleteNoteTextViewButton.setOnClickListener((v) -> deleteNoteFromFirebase());
    }

    //Funkcija cuvanja beleske
    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        //Validacija da li je naslov prazan
        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText.setError("Title text is required.");
            return;
        }

        //Pravljenje Note modela klase
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteFirebase(note);

    }

    //Cuvanje u bazi
    void saveNoteFirebase(Note note) {
        DocumentReference documentReference;
        if (isEdited) {
            //Ako je "isEdited" onda izmenjuje belesku
            documentReference = Utility.getCollectionReferenceForNotes().document(documentId);
        } else {
            //Ako nije "isEdited" onda pravi novu belesku
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Uspesno dodata beleska
                Utility.showToast(NoteDetailsActivity.this, "Note added successfully! ");
                finish();
            } else {
                //Beleska nije dodata
                Utility.showToast(NoteDetailsActivity.this, "Error while adding note. ");
            }
        });
    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;

        documentReference = Utility.getCollectionReferenceForNotes().document(documentId);
        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Uspesno obrisana beleska
                Utility.showToast(NoteDetailsActivity.this, "Note deleted successfully! ");
                finish();
            } else {
                //Beleska nije obrisana
                Utility.showToast(NoteDetailsActivity.this, "Error while deleting note. ");
            }
        });


    }
}
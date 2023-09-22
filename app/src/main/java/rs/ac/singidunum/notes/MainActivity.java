package rs.ac.singidunum.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    //Deklaracija komponenti korisnickog interfejsa
    FloatingActionButton addNoteButton;
    RecyclerView recyclerView;
    ImageButton menuButton;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicijalizacija komponenti prema njihovim ID-jevima
        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recycler_view);
        menuButton = findViewById(R.id.menu_button);

        addNoteButton.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NoteDetailsActivity.class)));
        menuButton.setOnClickListener((v) -> showMenu());
        setupRecyclerView();
    }

    void showMenu(){
        //Izlazi popup meni
        PopupMenu popupMenu = new PopupMenu(MainActivity.this,menuButton);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            //Odjavljivanje i preusmeravanje na LoginActivity
            if (menuItem.getTitle()== "Logout"){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    //Recycler view za pregled beleski
    void setupRecyclerView(){
        //Upit za hvatanje podataka iz baze i njhovo sortiranje
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        //Prikaz beleski u vertikalnom rasporedu
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options,this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    //Pocinje sa slusanjem promena
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    //Zaustavljanje aktivnosti
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    //Obavestava adapter da azurira prikaz nakon promena
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}
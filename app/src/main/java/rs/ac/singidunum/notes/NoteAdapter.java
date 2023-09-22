package rs.ac.singidunum.notes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


//Koristimo firebase kao recyclerView
public class NoteAdapter extends FirestoreRecyclerAdapter <Note, NoteAdapter.NoteViewHolder> {
    Context context;

    //Konstruktor klase NoteAdapter sa opcijama za konfigurisanje i kontekst
    public NoteAdapter(FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    //Popunjavanje podataka u svaki element
    protected void onBindViewHolder(NoteViewHolder holder, int position, Note note) {
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);
        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));

        holder.itemView.setOnClickListener((v) -> {
            //Kreira se Intent za pokretanje NoteDetailsActivity
            Intent intent = new Intent(context, NoteDetailsActivity.class);
            //Dodaju se podaci u Intent kako bi se preneli na NoteDetailsActivity
            intent.putExtra("title",note.title);
            intent.putExtra("content",note.content);
            String documentId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("documentId",documentId);
            context.startActivity(intent);
        });
    }

    @Override
    //Ona metoda se koristi za pravljanje novih instanci ViewHoldera sa parametrima parent i viewType
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Pravi se view za pojedinacnu stakvu iz xml fajla (recycler_note_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item,parent,false);
        //Pravi se nova instanca ViewHolder-a i prosledjuje se napravljeni view kao parametar
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        //Definise se TextView koji ce se koristiti za prikaz sadrzaja
        TextView titleTextView,contentTextView,timestampTextView;

        public NoteViewHolder(View itemView) {
            //Pogled (view) koji sadrzi itemView
            super(itemView);
            //Inicijalizacija referenci prema odgovarajucim text_view komponentama
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
        }
    }
}

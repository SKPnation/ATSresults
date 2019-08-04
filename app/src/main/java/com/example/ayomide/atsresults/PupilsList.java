package com.example.ayomide.atsresults;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ayomide.atsresults.Model.Pupil;
import com.example.ayomide.atsresults.ViewHolder.PupilViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PupilsList extends AppCompatActivity {

    //Firebase
    FirebaseDatabase db;
    DatabaseReference pupilsList;
    FirebaseRecyclerAdapter<Pupil, PupilViewHolder> adapter;

    String classId = "";

    //View
    RecyclerView recycler_pupils;
    RecyclerView.LayoutManager layoutManager;

    EditText etEntryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_pupils_list );

        db = FirebaseDatabase.getInstance();
        pupilsList = db.getReference( "Pupil" );

        //load Pupils list
        recycler_pupils = findViewById( R.id.recycler_pupils );
        recycler_pupils.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recycler_pupils.setLayoutManager( layoutManager );

        if (getIntent() != null)
            classId = getIntent().getStringExtra( "ClassId" );
        if (!classId.isEmpty()) {
            loadPupilsList( classId );
        }
    }

    private void loadPupilsList(String classId)
    {
        adapter = new FirebaseRecyclerAdapter<Pupil, PupilViewHolder>(
                Pupil.class,
                R.layout.pupil_layout,
                PupilViewHolder.class,
                pupilsList.orderByChild( "categoryId" ).equalTo( classId )) {
            @Override
            protected void populateViewHolder(PupilViewHolder viewHolder, Pupil model, final int position) {
                viewHolder.pupil_name.setText( model.getName() );
                Picasso.with( getBaseContext() ).load( model.getImage() ).into( viewHolder.pupil_image );
                viewHolder.btnReport.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showReportEntryDialog(adapter.getRef( position ).getKey(), adapter.getItem( position ));
                    }
                } );

                viewHolder.btnBill.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFeesEntryDialog(adapter.getRef( position ).getKey(), adapter.getItem( position ));
                    }
                } );
            }
        };
        adapter.notifyDataSetChanged();
        recycler_pupils.setAdapter( adapter );
    }

    private void showReportEntryDialog(final String key, final Pupil item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( PupilsList.this );
        alertDialog.setTitle( "Entry Code" );
        alertDialog.setMessage( "Enter your child's reg no:" );
        alertDialog.setIcon( R.drawable.ic_person_black_24dp );

        LayoutInflater inflater = this.getLayoutInflater();
        View entry_pupil_details_layout = inflater.inflate( R.layout.pupil_details_entry_layout, null );

        etEntryCode = entry_pupil_details_layout.findViewById( R.id.etEntryCode );

        alertDialog.setView( entry_pupil_details_layout );

        alertDialog.setPositiveButton( "ENTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pupilsList.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (item.getEntryCode().equals( etEntryCode.getText().toString() ))
                        {
                            Intent pupilReport = new Intent(PupilsList.this, ReportCard.class);
                            pupilReport.putExtra("pupilId", key); //Send pupil Id to new activity
                            startActivity(pupilReport);
                        }
                        else
                            Toast.makeText( PupilsList.this, "Wrong Registration number", Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }
        } );

        alertDialog.show();
    }

    private void showFeesEntryDialog(final String key, final Pupil item)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( PupilsList.this );
        alertDialog.setTitle( "Entry Code" );
        alertDialog.setMessage( "Enter your child's reg no:" );
        alertDialog.setIcon( R.drawable.ic_person_black_24dp );

        LayoutInflater inflater = this.getLayoutInflater();
        View entry_pupil_details_layout = inflater.inflate( R.layout.pupil_details_entry_layout, null );

        etEntryCode = entry_pupil_details_layout.findViewById( R.id.etEntryCode );

        alertDialog.setView( entry_pupil_details_layout );

        alertDialog.setPositiveButton( "ENTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pupilsList.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (item.getEntryCode().equals( etEntryCode.getText().toString() ))
                        {
                            Intent pupilFees = new Intent(PupilsList.this, BillActivity.class);
                            pupilFees.putExtra("pupilId", key); //Send pupil Id to new activity
                            startActivity(pupilFees);
                        }
                        else
                            Toast.makeText( PupilsList.this, "Wrong Registration number", Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }
        } );
        alertDialog.show();
    }
}

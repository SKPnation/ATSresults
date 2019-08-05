package com.example.ayomide.atsresults;

import android.content.Intent;
import android.net.Uri;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ayomide.atsresults.Common.Common;
import com.example.ayomide.atsresults.Model.Parent;
import com.example.ayomide.atsresults.Model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class Help extends AppCompatActivity {

    EditText etRecipient, etSubject, etMessage, adminPhone;
    Button btnSend;
    FloatingActionButton fab;
    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_help );

        etRecipient = findViewById( R.id.etRecipient );
        etSubject = findViewById( R.id.etSubject );
        etMessage = findViewById( R.id.etMessage );
        btnSend = findViewById( R.id.btnSend );

        btnSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipient = etRecipient.getText().toString().trim(); //trim will remove space before and after the text
                String subject = etSubject.getText().toString().trim();
                String message = etMessage.getText().toString().trim();

                //method call for email
                sendEmail(recipient, subject, message);
            }
        } );

        fab = findViewById( R.id.fab_call );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        } );
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( Help.this );
        alertDialog.setTitle( "Call admin" );

        LayoutInflater inflater = this.getLayoutInflater();
        View admin_option_layout = inflater.inflate( R.layout.admin_option_layout, null );


        adminPhone =  admin_option_layout.findViewById(R.id.admin_phone);

        alertDialog.setView( admin_option_layout );

        //Load all admin phone to spinner
        final List<String> adminList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("User")
                .addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot adminSnapshot:dataSnapshot.getChildren())
                            adminList.add( adminSnapshot.getKey() );
                        adminPhone.setText( adminList.get( 0 ) );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );


        alertDialog.show();
    }

    private void sendEmail(String recipient, String subject, String message)
    {
        Intent intent = new Intent();
        intent.setData( Uri.parse( "mailto:" ) );
        intent.setType( "text/plain" ); //message/rfc2822 is a mime type for email messages
        intent.putExtra( Intent.EXTRA_EMAIL, new String[]{recipient} );

        intent.putExtra( Intent.EXTRA_SUBJECT, subject );
        //put message of email in intent
        intent.putExtra( Intent.EXTRA_TEXT, message );

        if (Common.isConnectedToTheInternet( getBaseContext() ))
        {
            startActivity(Intent.createChooser(intent, "Choose a Client"));
        }
        else
            Toast.makeText(Help.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
    }
}

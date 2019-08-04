package com.example.ayomide.atsresults;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ayomide.atsresults.Common.Common;

public class Help extends AppCompatActivity {

    EditText etRecipient, etSubject, etMessage;
    Button btnSend;

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

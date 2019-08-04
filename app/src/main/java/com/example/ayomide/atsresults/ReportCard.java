package com.example.ayomide.atsresults;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsresults.Common.Common;
import com.example.ayomide.atsresults.Model.Pupil;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportCard extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference pupils;

    String pupilId = "";

    Pupil currentPupil;

    PDFView pdfView;

    TextView report_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_card );

        db = FirebaseDatabase.getInstance();
        pupils = db.getReference("Pupil");

        report_url = findViewById( R.id.repor_url );

        pdfView = findViewById( R.id.pdfView );
        pdfView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        } );

        //Get pupil Id from Intent
        if(getIntent() != null)
            pupilId = getIntent().getStringExtra("pupilId");
        if(!pupilId.isEmpty())
        {
            if (Common.isConnectedToTheInternet(getBaseContext()))
            {
                getReport( pupilId );
                Toast.makeText( ReportCard.this, "Tap the screen to share the result ", Toast.LENGTH_LONG ).show();
            }
        }
    }

    private void sendEmail()
    {
        Intent intent = new Intent();
        intent.setData( Uri.parse( "mailto:" ) );
        intent.setType( "text/plain" );

        intent.putExtra( Intent.EXTRA_SUBJECT, currentPupil.getName()+"'s report card" );
        //put message of email in intent
        intent.putExtra( Intent.EXTRA_TEXT, currentPupil.getReportPdf() );

        if (Common.isConnectedToTheInternet( getBaseContext() ))
        {
            startActivity(Intent.createChooser(intent, "Share using"));
        }
        else
            Toast.makeText(ReportCard.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
    }

    private void getReport(String pupilId)
    {
        pupils.child( pupilId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPupil = dataSnapshot.getValue(Pupil.class);

                report_url.setText( currentPupil.getReportPdf() );
                //This function reads pdf from URL
                new RetrievePDFStream().execute( report_url.getText().toString() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }


    class RetrievePDFStream extends AsyncTask<String,Void,InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try{
                URL url = new URL( strings[0] );
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode()==200)
                {
                    inputStream = new BufferedInputStream( urlConnection.getInputStream() );
                }
            }
            catch (IOException e)
            {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream( inputStream ).load();
        }
    }
}

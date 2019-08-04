package com.example.ayomide.atsresults;

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

public class BillActivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference pupils;

    String pupilId = "";

    Pupil currentPupil;

    PDFView billView;

    TextView bill_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bill );

        db = FirebaseDatabase.getInstance();
        pupils = db.getReference("Pupil");

        bill_url = findViewById( R.id.bill_url );

        billView = findViewById( R.id.bill_pdf );
        billView.setOnClickListener( new View.OnClickListener() {
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
            if (Common.isConnectedToTheInternet( getBaseContext() ))
            {
                getBill(pupilId);
                Toast.makeText( BillActivity.this, "Tap the link above if you want to share the child's school fees", Toast.LENGTH_LONG ).show();
            }
        }
    }

    private void getBill(String pupilId)
    {
        pupils.child( pupilId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentPupil = dataSnapshot.getValue(Pupil.class);

                bill_url.setText( currentPupil.getBillPdf() );
                //This function reads pdf from URL
                new RetrieveFeesPDFStream().execute( bill_url.getText().toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void sendEmail()
    {
    }



    class RetrieveFeesPDFStream extends AsyncTask<String,Void,InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try
            {
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
            billView.fromStream( inputStream ).load();
        }
    }
}

package com.example.ayomide.atsresults;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayomide.atsresults.Common.Common;
import com.example.ayomide.atsresults.Model.Parent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    TextInputEditText etPhone, etPassword;
    TextView forgot_pwd;
    Button btnSignIn;
    CheckBox cbRemember;

    private ProgressDialog mDialog;

    FirebaseDatabase db;
    DatabaseReference table_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        db = FirebaseDatabase.getInstance();
        table_parent = db.getReference(Common.PARENTS_TABLE );

        etPhone = findViewById( R.id.etPhone );
        etPassword = findViewById( R.id.etPassword );
        forgot_pwd = findViewById( R.id.tvForgotPass );
        cbRemember = findViewById( R.id.cbRemember );

        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginParent();
            }
        });

        forgot_pwd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //..
            }
        } );
    }

    private void loginParent()
    {
        if (Common.isConnectedToTheInternet( getBaseContext() )) {

            if(cbRemember.isChecked())
            {
                Paper.book().write( Common.USER_KEY, etPhone.getText().toString() );
                Paper.book().write( Common.PWD_KEY, etPassword.getText().toString() );
            }

            mDialog = new ProgressDialog( SignIn.this );
            mDialog.setMessage( "Loading..." );
            mDialog.show();

            table_parent.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child( etPhone.getText().toString() ).exists()) {
                        mDialog.dismiss();
                        Parent parent = dataSnapshot.child( etPhone.getText().toString() ).getValue( Parent.class );
                        parent.setPhone( etPhone.getText().toString() );

                        if (parent.getPassword().equals( etPassword.getText().toString() )) {
                            mDialog.dismiss();

                            Intent homeIntent = new Intent( SignIn.this, MainActivity.class );
                            Common.currentParent = parent;
                            startActivity( homeIntent );
                            finish();
                            Toast.makeText( SignIn.this, "Login successful", Toast.LENGTH_SHORT ).show();

                        } else {
                            Toast.makeText( SignIn.this, "Wrong password", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText( SignIn.this, "Parent does not exist", Toast.LENGTH_SHORT ).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
        else
            Toast.makeText(SignIn.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
    }

}

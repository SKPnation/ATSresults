package com.example.ayomide.atsresults;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ayomide.atsresults.Common.Common;
import com.example.ayomide.atsresults.ItemClickListener.ItemClickListener;
import com.example.ayomide.atsresults.Model.Category;
import com.example.ayomide.atsresults.ViewHolder.ClassViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Firebase
    FirebaseDatabase db;
    DatabaseReference category;
    FirebaseRecyclerAdapter<Category, ClassViewHolder> adapter;

    //View
    RecyclerView recycler_grade;
    RecyclerView.LayoutManager layoutManager;

    TextView tvFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle("Classes");
        setSupportActionBar( toolbar );

        db = FirebaseDatabase.getInstance();
        category = db.getReference("Class");

        //Init Paper
        Paper.init( this );

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        tvFullName = (TextView) headerView.findViewById(R.id.tvFullName);
        tvFullName.setText( Common.currentParent.getName());

        //Load classes
        recycler_grade = (RecyclerView) findViewById(R.id.recycler_grades);
        recycler_grade.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_grade.setLayoutManager(layoutManager);

        loadClasses();
    }

    private void loadClasses()
    {
        adapter = new FirebaseRecyclerAdapter<Category, ClassViewHolder>(
                Category.class, R.layout.grade_layout, ClassViewHolder.class, category ) {
            @Override
            protected void populateViewHolder(ClassViewHolder viewHolder, Category model, int position) {
                viewHolder.tvClassName.setText(model.getName());

                viewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send classId to new Activity and start new Activity
                        Intent pupilList = new Intent( MainActivity.this, PupilsList.class );
                        //Because classId is key so we just get key of this items
                        pupilList.putExtra( "ClassId", adapter.getRef( position ).getKey() );
                        startActivity( pupilList );
                    }
                } );
            }
        };
        adapter.notifyDataSetChanged();
        recycler_grade.setAdapter( adapter );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_classes) {
            // Handle the camera action
        } else if (id == R.id.nav_help) {

            startActivity( new Intent( MainActivity.this, Help.class ) );

        } else if (id == R.id.nav_sign_out) {

            //Delete remembered user and password
            Paper.book().destroy();

            Intent signIn = new Intent(MainActivity.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }
}

package nl.hsleiden.basenstefan.ikpmd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.hsleiden.basenstefan.ikpmd.api.Movie;
import nl.hsleiden.basenstefan.ikpmd.api.MovieDetailed;
import nl.hsleiden.basenstefan.ikpmd.api.MovieRepository;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchActivity;
import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchResultAdapter;

public class ListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView resultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> switchToSearch());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        resultsView = findViewById(R.id.ResultsView);
        resultsView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser currentUser = getCurrentUser();

        TextView testData = findViewById(R.id.testDataPlaceholder);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    List<Movie> movies = new ArrayList<Movie>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            movies.clear();
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                            List<String> movieIds;
                            if (data != null && data.size() > 0) {
                                movieIds = new ArrayList<>(data.keySet());
                                for (String movieId: Arrays.copyOf(movieIds.toArray(), movieIds .size(), String[].class)) {
                                    testData.setText(movieId);
                                    MovieRepository.fetchMovie(movieId, resultsView.getContext(), this::onResult, this::onError);
                                }
                            } else {
                                testData.setText("Geen data");
                            }
                        }
                    }

                    private void onError(VolleyError volleyError) {
                        testData.setText(volleyError.toString());
                    }

                    private void onResult(MovieDetailed movieDetailed) {
                        movies.add(movieDetailed);
                        resultsView.setAdapter(new SearchResultAdapter(Arrays.copyOf(movies.toArray(), movies.size(), Movie[].class)));
                        testData.setText(movieDetailed.getTitle());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        testData.setText(databaseError.getMessage());
                    }
                });
    }

    private FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        } else {
            //TODO redirect to login activity
        }
        return currentUser;
    }

    private void switchToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser currentUser) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView fullname = header.findViewById(R.id.fullname);
        TextView email = header.findViewById(R.id.email);
        ImageView image = header.findViewById(R.id.profile_picture);
        fullname.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());
        image.setImageURI(currentUser.getPhotoUrl());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.log_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            FirebaseAuth.getInstance().signOut();
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

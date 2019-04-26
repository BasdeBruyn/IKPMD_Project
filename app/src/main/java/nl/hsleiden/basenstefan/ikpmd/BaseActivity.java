package nl.hsleiden.basenstefan.ikpmd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.hsleiden.basenstefan.ikpmd.movieSearch.SearchActivity;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    DrawerLayout drawer;
    FirebaseUser currentUser;

    public void onCreate(Bundle savedInstanceState, int layout_id) {
        setContentView(layout_id);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.common_layout);
        drawer.requestDisallowInterceptTouchEvent(true);
        currentUser = getCurrentUser();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                drawer.setZ(0);
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawer.setZ(2);
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        if (id == R.id.movie_watch_list && ActivityState.getState() != ActivityState.LIST) {
            finish();
        }
        if (id == R.id.search_movie && ActivityState.getState() != ActivityState.SEARCH) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        drawer.bringToFront();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.common_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            FirebaseAuth.getInstance().signOut();
            startActivity(intent);
        }
        return currentUser;
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
}

package tk.samgrogan.pulp.UI;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Models.ComicDataObject;
import tk.samgrogan.pulp.Models.DrawerItem;
import tk.samgrogan.pulp.R;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawer;
    ActionBarDrawerToggle mToggle;
    Menu mBoxList;
    SubMenu subMenu;
    Toolbar toolbar;
    RecyclerView navRecycler;
    CoverFragment coverFragment;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    ChildEventListener childEventListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<DrawerItem> testList = new ArrayList<>();
    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navRecycler = (RecyclerView)findViewById(R.id.nvView);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle( this, mDrawer, toolbar,  R.string.open, R.string.close){

            @Override
            public void onDrawerClosed(View drawerView) {
                ActivityCompat.invalidateOptionsMenu(MainActivity.this);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ActivityCompat.invalidateOptionsMenu(MainActivity.this);
            }
        };
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("collections");
        }

        DrawerItem shortMaker = new DrawerItem();
        shortMaker.setmTitle("Make a Short Box");
        shortMaker.setmIcon(R.drawable.ic_menu_slideshow);
        testList.add(shortMaker);

        DrawerAdapter adapter = new DrawerAdapter(testList);
        navRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        navRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new DrawerAdapter.OnItemSelectedListener() {
                                           @Override
                                           public void onItemSelected(View view, int position) {
                                               if (testList.get(position).getmTitle().equals("Make a Short Box")){
                                                   Bridgette coverFragment = new Bridgette();
                                                   android.app.FragmentManager fragmentManager = getFragmentManager();
                                                   fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit();
                                                   mDrawer.closeDrawers();
                                               }
                                           }
                                       });

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        mBoxList = navigationView.getMenu();
        subMenu = mBoxList.addSubMenu("Short Boxes");*/


        coverFragment = new CoverFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                }else {
                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER)
                                    .setTheme(R.style.SignInTheme)
                            .build(),RC_SIGN_IN);
                }
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ComicDataObject dataObject = dataSnapshot.getValue(ComicDataObject.class);
                //subMenu.add(dataObject.collectionTitle);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (firebaseAuth.getCurrentUser() != null){
            databaseReference.addChildEventListener(childEventListener);
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    /*@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.collections) {
            Bridgette coverFragment = new Bridgette();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit();
        }else if (id == R.id.all){
            coverFragment = new CoverFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
        //databaseReference.addChildEventListener(childEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}

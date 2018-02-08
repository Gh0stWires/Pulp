package tk.samgrogan.pulp.UI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;
    private RecyclerView navRecycler;
    private CoverFragment coverFragment;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ChildEventListener childEventListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DrawerAdapter adapter;
    private List<DrawerItem> testList = new ArrayList<>();
    private List<ComicDataObject> collectionList = new ArrayList<>();
    private static final int RC_SIGN_IN = 123;

    //God object?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        setContentView(R.layout.cover_fragment);
        toolbar = (Toolbar) findViewById(R.id.include);
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


        adapter = new DrawerAdapter(testList);
        navRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        adapter.setOnItemClickListener(new DrawerAdapter.OnItemSelectedListener() {
                                           @Override
                                           public void onItemSelected(View view, int position) {
                                               Bundle bundle = new Bundle();
                                               //ArrayList<String> passList = new ArrayList<>();
                                               //passList.addAll(collectionList.get(position).getCollectionList());
                                               String box = collectionList.get(position).collectionTitle;
                                               //bundle.putStringArrayList("collection-paths", passList);
                                               bundle.putString("box-name", box);
                                               ShortBoxFragment fragment = new ShortBoxFragment();
                                               fragment.setArguments(bundle);
                                               android.app.FragmentManager fragmentManager = getFragmentManager();
                                               fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                                           }
                                       });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nvStatic);
        navigationView.setNavigationItemSelectedListener(this);



        coverFragment = new CoverFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                }else {

                }
            }
        };

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ComicDataObject dataObject = dataSnapshot.getValue(ComicDataObject.class);
                collectionList.add(dataObject);
                DrawerItem shortMaker = new DrawerItem();
                shortMaker.setmTitle(dataObject.collectionTitle);
                shortMaker.setmIcon();
                testList.add(shortMaker);
                adapter.notifyDataSetChanged();
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

        navRecycler.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coverFragment = new CoverFragment();
                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit();
                    navRecycler.setAdapter(adapter);


                } else {

                    Toast.makeText(this, R.string.permission_toast, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
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
    }

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

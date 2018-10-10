package tk.samgrogan.pulp.UI

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import tk.samgrogan.pulp.Models.ComicDataObject
import tk.samgrogan.pulp.Models.DrawerItem
import tk.samgrogan.pulp.R
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mDrawer: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var toolbar: Toolbar? = null
    private var navRecycler: RecyclerView? = null
    private var coverFragment: CoverFragment? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private var childEventListener: ChildEventListener? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var adapter: DrawerAdapter? = null
    private val drawerItems = ArrayList<DrawerItem>()
    private val collectionList = ArrayList<ComicDataObject>()

    //God object?


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        setContentView(R.layout.cover_fragment)
        toolbar = findViewById<View>(R.id.include) as Toolbar
        navRecycler = findViewById<View>(R.id.nvView) as RecyclerView
        mDrawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mToggle = object : ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.open, R.string.close) {

            override fun onDrawerClosed(drawerView: View) {
                ActivityCompat.invalidateOptionsMenu(this@MainActivity)
            }

            override fun onDrawerOpened(drawerView: View) {
                ActivityCompat.invalidateOptionsMenu(this@MainActivity)
            }
        }
        mDrawer!!.addDrawerListener(mToggle!!)
        mToggle!!.syncState()

        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        if (firebaseAuth!!.currentUser != null) {
            databaseReference = firebaseDatabase!!.reference.child("users").child(firebaseAuth!!.currentUser!!.uid).child("collections")
        }


        adapter = DrawerAdapter(drawerItems)
        navRecycler!!.layoutManager = LinearLayoutManager(applicationContext)




        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)


        val navigationView = findViewById<View>(R.id.nvStatic) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)



        coverFragment = CoverFragment()
        val fragmentManager = fragmentManager
        fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
            } else {

            }
        }

        adapter!!.setOnItemClickListener { view, position ->
            val bundle = Bundle()
            //ArrayList<String> passList = new ArrayList<>();
            //passList.addAll(collectionList.get(position).getCollectionList());
            val box = collectionList[position].collectionTitle
            //bundle.putStringArrayList("collection-paths", passList);
            bundle.putString("box-name", box)
            val fragment = ShortBoxFragment()
            fragment.arguments = bundle
            val fragmentManager = getFragmentManager()
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit()
            mDrawer!!.closeDrawers()
        }

        childEventListener = object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val dataObject = dataSnapshot.getValue<ComicDataObject>(ComicDataObject::class.java)
                dataObject?.let { collectionList.add(it) }
                val shortMaker = DrawerItem()
                shortMaker.setmTitle(dataObject!!.collectionTitle)
                shortMaker.setmIcon()
                drawerItems.add(shortMaker)
                adapter!!.notifyDataSetChanged()
                //subMenu.add(dataObject.collectionTitle);

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        if (firebaseAuth!!.currentUser != null) {
            databaseReference!!.addChildEventListener(childEventListener as ChildEventListener)
        }

        navRecycler!!.adapter = adapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coverFragment = CoverFragment()
                    val fragmentManager = fragmentManager
                    fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit()
                    navRecycler!!.adapter = adapter


                } else {

                    Toast.makeText(this, R.string.permission_toast, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.collections) {
            val coverFragment = ShortMaker()
            val fragmentManager = fragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit()
        } else if (id == R.id.all) {
            coverFragment = CoverFragment()
            val fragmentManager = fragmentManager
            fragmentManager.beginTransaction().replace(R.id.flContent, coverFragment).commit()
        }


        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
        //databaseReference.addChildEventListener(childEventListener);
    }

    override fun onPause() {
        super.onPause()
        firebaseAuth!!.removeAuthStateListener(authStateListener!!)
    }

    companion object {
        private val RC_SIGN_IN = 123
    }
}

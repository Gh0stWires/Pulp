package tk.samgrogan.pulp.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import tk.samgrogan.pulp.R
import tk.samgrogan.pulp.ui.MainActivity
import java.util.*

class SignInActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser

        if (mUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        } else {
            signInIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    signInIntent()

                } else if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    val snackbar = Snackbar.make(findViewById(R.id.sign_in), "Offline", Snackbar.LENGTH_INDEFINITE)
                    snackbar.setAction("Refresh", NetworkActionListener())
                    snackbar.show()
                }

            }
        }
    }

    private fun signInIntent() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList<AuthUI.IdpConfig>(
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.EmailBuilder().build()))
                        .build(),
                RC_SIGN_IN)
    }

    inner class NetworkActionListener : View.OnClickListener {

        override fun onClick(v: View) {
            val intent = Intent(v.context, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private val RC_SIGN_IN = 123
    }
}

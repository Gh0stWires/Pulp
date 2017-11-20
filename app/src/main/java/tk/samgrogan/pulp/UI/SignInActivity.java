package tk.samgrogan.pulp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import tk.samgrogan.pulp.R;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .setTheme(R.style.SignInTheme)
                            .build(),RC_SIGN_IN);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setTheme(R.style.SignInTheme)
                                    .build(),RC_SIGN_IN);

                }else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.sign_in), "Offline", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Refresh", new NetworkActionListener());
                    snackbar.show();
                }

            }
        }
    }

    public class NetworkActionListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

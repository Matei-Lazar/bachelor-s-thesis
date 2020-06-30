package ths.kariru.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.BuildConfig
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import ths.kariru.MainActivity
import ths.kariru.R
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        createLoginUI()
    }

    private fun createLoginUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_login_pic)
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .setTheme(R.style.SigninTheme)
                .build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                // TODO initialize current user in Firestore
                val user = FirebaseAuth.getInstance().currentUser
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Timber.i("USERDATA: ${user?.displayName}")
            } else if (response == null) {
                finish()
            } else if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, response.error?.errorCode.toString(), Toast.LENGTH_SHORT).show()
                return
            } else if (response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(this, response.error?.errorCode.toString(), Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1
        private const val ERROR_DIALOG_REQUEST = 2
    }
}

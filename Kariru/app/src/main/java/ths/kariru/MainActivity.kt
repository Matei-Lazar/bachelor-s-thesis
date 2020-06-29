package ths.kariru

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import ths.kariru.databinding.ActivityMainBinding
import ths.kariru.fragments.AddFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth == null) {
            createLoginUI()
        } else {
            Toast.makeText(this, "$auth has entered the chat", Toast.LENGTH_SHORT).show()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setupBottomNavMenu(navController)
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
                .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                .setTheme(R.style.SigninTheme)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
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

//    fun setupUI(view: View) {
//        // Set up touch listener for non-text box views to hide keyboard
//
//        if (view !is EditText) {
//            view.setOnTouchListener(object: View.OnTouchListener {
//                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                    hideSoftKeyboard()
//                    return false
//                }
//            })
//        }
//
//        // If a layout container, iterate over children and seed recursion
//        if (view is ViewGroup) {
//            for (i in 0..view.childCount) {
//                var innerView = view.getChildAt(i)
//                setupUI(innerView)
//            }
//        }
//    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNavigationView?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1
        private const val ERROR_DIALOG_REQUEST = 2
    }
}

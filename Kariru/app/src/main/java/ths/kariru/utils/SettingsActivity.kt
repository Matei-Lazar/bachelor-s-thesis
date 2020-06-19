package ths.kariru.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import ths.kariru.MainActivity
import ths.kariru.R
import ths.kariru.databinding.ActivitySettingsBinding
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        val authUI = AuthUI.getInstance()

        binding.signOutButton.setOnClickListener {
            authUI
                .signOut(this)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Timber.i("Sign-out failed %s", task.exception)
                        Toast.makeText(this, "Sign-out failed", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete Account")
                .setMessage("This is permanent, are you sure?")
                .setPositiveButton("Yes") { _,_ ->
                    authUI.delete(this)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                Timber.i("Delete account failed %s", task.exception)
                                Toast.makeText(this, "Delete account failed", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        user = FirebaseAuth.getInstance().currentUser
        binding.textInputEditText.setText(user?.displayName)
    }

    override fun onPause() {
        super.onPause()
        val profile = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.textInputEditText.text.toString())
            .build()

        if (user != null) {
            user!!.updateProfile(profile)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Timber.i("Failed to update display name %s", task.exception)
                        Toast.makeText(this, "Name update failed", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

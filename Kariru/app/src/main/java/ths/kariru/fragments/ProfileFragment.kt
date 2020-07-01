package ths.kariru.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_profile.*
import ths.kariru.R
import ths.kariru.databinding.FragmentProfileBinding
import ths.kariru.utils.*
import ths.kariru.viewmodels.ProfileFragmentViewModel
import timber.log.Timber

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileFragmentViewModel
    private lateinit var binding: FragmentProfileBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var selectedImage: ByteArray? = null
    private var pictureJustChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)
        binding.viewModel = viewModel

        binding.profileDeleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Account")
                .setMessage("This is permanent, are you sure?")
                .setPositiveButton("Yes") { _,_ ->

                    Firebase.firestore.document("users/$userId").delete()
                    val ref = Firebase.firestore.collection("properties")
                        ref.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            if (firebaseFirestoreException != null) {
                                Timber.i("Profile: Delete account + properties error: ${firebaseFirestoreException.message}")
                                return@addSnapshotListener
                            }
                            querySnapshot!!.documents.forEach {
                                val id = it.data!!["userId"] as String
                                if (id == userId)
                                    it.reference.delete()
                            }
                        }

                    AuthUI.getInstance().delete(requireContext())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(requireContext(), SplashActivity::class.java))
                            } else {
                                Timber.i("Profile: Delete account failed ${task.exception}")
                                Toast.makeText(requireContext(), "Delete account failed", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.profileSaveButton.setOnClickListener {
            if (selectedImage != null)
                StorageUtil.uploadProfilePicture(selectedImage!!) { imagePath ->
                    FirestoreUtil.updateCurrentUser(profile_name_text.text.toString(), imagePath)
                }
            else
                FirestoreUtil.updateCurrentUser(profile_name_text.text.toString(), null)
            Toast.makeText(context, "Saving...", Toast.LENGTH_SHORT).show()
        }

        binding.profileSignOutButton.setOnClickListener {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
        }

        binding.profileProfilePicture.setOnClickListener {
            uploadProfilePicture()
        }

        return binding.root
    }

    private fun uploadProfilePicture() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null) {
            val selectedImageUri = data.data
            val inputStream = selectedImageUri?.let { activity?.contentResolver?.openInputStream(it) }
            val byteArray = inputStream?.readBytes()

            GlideApp.with(this)
                .load(byteArray)
                .into(binding.profileProfilePicture)

            pictureJustChanged = true

        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this.isVisible) {
                binding.profileNameText.setText(user.name)
                if (!pictureJustChanged && user.profilePicture != null) {
//
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(user.profilePicture))
                        .placeholder(R.drawable.ic_account)
                        .into(binding.profileProfilePicture)
                }
            }
        }
    }

    companion object {
        private const val RC_SELECT_IMAGE = 1
    }

}

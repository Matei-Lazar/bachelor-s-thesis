package ths.kariru.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add2.*
import ths.kariru.MainActivity

import ths.kariru.R
import ths.kariru.adapters.AddRecyclerViewAdapter
import ths.kariru.adapters.AddViewPagerAdapter
import ths.kariru.databinding.FragmentAdd2Binding
import ths.kariru.models.Address
import ths.kariru.models.Image
import ths.kariru.models.Property
import ths.kariru.viewmodels.AddFragmentViewModel
import java.util.*

class AddFragment2 : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel
    private lateinit var binding: FragmentAdd2Binding
    private lateinit var recyclerViewAdapter: AddRecyclerViewAdapter

    private var images: MutableList<Uri> = arrayListOf()

    // Firebase
    private val user = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add2, container, false)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        // Uploads photos from gallery
        images = arrayListOf()
        uploadPhotos(binding.add2UploadButton)

        // RecyclerView
        binding.apply {
            add2Recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            add2Recyclerview.setHasFixedSize(true)
            recyclerViewAdapter = AddRecyclerViewAdapter(images)
            add2Recyclerview.adapter = recyclerViewAdapter
        }

        binding.add2DeleteButton.setOnClickListener {
            recyclerViewAdapter = AddRecyclerViewAdapter(images = arrayListOf())
            binding.add2Recyclerview.adapter = recyclerViewAdapter
        }

        // Saves property to firestore
        binding.add2SaveButton.setOnClickListener {
            saveProperty()
            //uploadImagesToStorage("images")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val safeArgs = AddFragment2Args.fromBundle(it)
            binding.add2StreetText.editText?.setText(safeArgs.streetName)
            binding.add2StreetNrText.editText?.setText(safeArgs.streetNumber)
        }
    }

    private fun saveProperty() {

        user ?: return // if we don't have a populated user, we return
        var latitude = ""
        var longitude = ""

        // Address
        val street = add2_street_text.editText?.text.toString()
        val streetNumber = binding.add2StreetNrText.editText?.text.toString()
        val blockName = binding.add2BlockNameText.editText?.text.toString()
        val apartmentNumber = binding.add2ApartmentNrText.editText?.text.toString().toInt()
        val address = Address(street, streetNumber, blockName, apartmentNumber)

        arguments?.let {
            val safeArgs = AddFragment2Args.fromBundle(it)
            latitude = safeArgs.latitude
            longitude = safeArgs.longitude
        }

        val property = Property(address, latitude = latitude, longitude = longitude)
        viewModel.savePropertyToFirestore(property, user, images)
    }

    private fun uploadPhotos(button: Button) {
        button.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                it.type = "image/*"
                startActivityForResult(it, PICK_MULTIPLE_IMAGES)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_MULTIPLE_IMAGES) {
                val clipData = data?.clipData
                if (clipData != null) { // handle multiple images
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        images.add(uri)
                    }
                } else {
                    data?.data.let {
                        if (it != null) {
                            images.add(it)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerViewAdapter = AddRecyclerViewAdapter(images)
        binding.add2Recyclerview.adapter = recyclerViewAdapter
    }

    companion object {
        private const val PICK_MULTIPLE_IMAGES = 1

    }
}
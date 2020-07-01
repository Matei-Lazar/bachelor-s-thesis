package ths.kariru.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_edit.*

import ths.kariru.R
import ths.kariru.adapters.AddRecyclerViewAdapter
import ths.kariru.adapters.AddViewPagerAdapter
import ths.kariru.adapters.EditRecyclerViewAdapter
import ths.kariru.databinding.FragmentEditBinding
import ths.kariru.models.Address
import ths.kariru.models.Property
import ths.kariru.viewmodels.AddFragmentViewModel
import timber.log.Timber


class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    private lateinit var viewModel: AddFragmentViewModel
    private lateinit var navController: NavController
    private lateinit var spinnerFloorAdapter: ArrayAdapter<String>
    private lateinit var spinnerRoomAdapter: ArrayAdapter<String>
    private lateinit var spinnerBathAdapter: ArrayAdapter<String>
    private lateinit var spinnerBalconyAdapter: ArrayAdapter<String>
    private lateinit var editTextAdapter: ArrayAdapter<String>
    private lateinit var propertyItem: Property
    private lateinit var floor: String
    private lateinit var rooms: String
    private lateinit var baths: String
    private lateinit var balconies: String
    private var images: MutableList<Uri> = arrayListOf()
    private val user = FirebaseAuth.getInstance().currentUser
    private var imagesHaveBeenUpdated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        initializeFloorSpinner()
        initializeRoomSpinner()
        initializeBathSpinner()
        initializeBalconySpinner()
        initializeTextFields()

        // Uploads photos from gallery
        images = arrayListOf()
        uploadPhotos(binding.editUploadButton)

        // Deletes all images from recycler wiew
        binding.editDeleteButton.setOnClickListener {
            deletePhotos()
        }
        // Saves property to firestore
        binding.editSaveButton.setOnClickListener {
            validateTextFields()
        }

        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val safeArgs = EditFragmentArgs.fromBundle(it)
            propertyItem = safeArgs.propertyItem!!
            Timber.i("Search2: ${propertyItem.propertyId}")
            bindData(propertyItem)
        }
    }

    private fun bindData(property: Property) {
        binding.apply {
            editStreet.setText(property.address.street)
            editStreetNr.setText(property.address.streetNumber)
            editBlockName.setText(property.address.blockName)
            editApartmentNr.setText(property.address.apartmentNumber.toString())
            editNeighborhoodText.setText(property.address.neighborhood)

            val imageListUri = mutableListOf<Uri>()
            val imageList = property.imageList
            imageList.forEach {
                imageListUri.add(Uri.parse(it))
            }
            editRecyclerview.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            editRecyclerview.setHasFixedSize(true)
            editRecyclerview.adapter = EditRecyclerViewAdapter(requireContext(), property.imageList)
            editFloorSpinner.setSelection(spinnerFloorAdapter.getPosition(property.floor))
            editRoomsSpinner.setSelection(spinnerRoomAdapter.getPosition(property.room))
            editBathsSpinner.setSelection(spinnerBathAdapter.getPosition(property.bath))

            editDescription.setText(property.description)
            editSurface.setText(property.surface.toString())
            editPrice.setText(property.price.toString())
        }
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

    private fun deletePhotos() {
        binding.editRecyclerview.adapter = EditRecyclerViewAdapter(requireContext(), images = arrayListOf())
        (binding.editRecyclerview.adapter as EditRecyclerViewAdapter).notifyDataSetChanged()
        images = arrayListOf()
    }

    private fun validateTextFields() {

        val isOkList = mutableListOf<Boolean>()
        var isOk = true

        if (binding.editStreet.text.isNullOrEmpty()) {
            binding.editStreetText.isErrorEnabled = true
            binding.editStreetText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editStreetText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editStreetNr.text.isNullOrEmpty()) {
            binding.editStreetNrText.isErrorEnabled = true
            binding.editStreetNrText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editStreetNrText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editBlockName.text.isNullOrEmpty()) {
            binding.editBlockNameText.isErrorEnabled = true
            binding.editBlockNameText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editBlockNameText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editApartmentNr.text.isNullOrEmpty()) {
            binding.editApartmentNrText.isErrorEnabled = true
            binding.editApartmentNrText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editApartmentNrText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editNeighborhoodText.text.isNullOrEmpty()) {
            binding.editNeighborhoodText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editNeighborhoodText.error = null
            isOkList.add(true)
        }

        if (binding.editDescription.text.isNullOrEmpty()) {
            binding.editDescriptionText.isErrorEnabled = true
            binding.editDescriptionText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editDescriptionText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editSurface.text.isNullOrEmpty()) {
            binding.editSurfaceText.isErrorEnabled = true
            binding.editSurfaceText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editSurfaceText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editPrice.text.isNullOrEmpty()) {
            binding.editPriceText.isErrorEnabled = true
            binding.editPriceText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.editPriceText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.editRecyclerview.adapter?.itemCount == 0) {
            isOkList.add(false)
            Toast.makeText(activity, "You must upload images", Toast.LENGTH_SHORT).show()
        }

        isOkList.forEach {
            if (!it) {
                isOk = false
            }
        }
        if (isOk) {
            saveProperty()
            val action = EditFragmentDirections.editToAdd()
            navController.navigate(action)
        }
    }

    private fun saveProperty() {

        user ?: return // if we don't have a populated user, we return
        var latitude = ""
        var longitude = ""
        var propertyId = ""
        var floorProperty: String
        var roomsProperty: String
        var bathsProperty: String
        var balconiesProperty: String

        arguments?.let {
            val safeArgs = EditFragmentArgs.fromBundle(it)
            latitude = safeArgs.propertyItem!!.latitude
            longitude = safeArgs.propertyItem.longitude
            propertyId = safeArgs.propertyItem.propertyId
        }

        // Address
        val street = edit_street_text.editText?.text.toString()
        val streetNumber = binding.editStreetNrText.editText?.text.toString()
        val blockName = binding.editBlockNameText.editText?.text.toString()
        val apartmentNumber = binding.editApartmentNrText.editText?.text.toString().toInt()
        val neighborhood = binding.editNeighborhoodText.text.toString()
        val address = Address(street, streetNumber, blockName, apartmentNumber, neighborhood)

        // Details
        floor.let { floorProperty = it }
        rooms.let { roomsProperty = it }
        baths.let { bathsProperty = it }
        balconies.let { balconiesProperty = it }
        val surface = binding.editSurfaceText.editText?.text.toString()
        val description = binding.editDescriptionText.editText?.text.toString()
        val price = binding.editPriceText.editText?.text.toString()

        val property = Property(
            address,
            latitude = latitude,
            longitude = longitude,
            floor = floorProperty,
            room = roomsProperty,
            bath = bathsProperty,
            balcony = balconiesProperty,
            description = description,
            surface = surface.toInt(),
            price = price.toInt(),
            userId = user.uid,
            propertyId = propertyId
        )

//        val map = mutableMapOf<String, Any>()
//        map

        viewModel.updatePropertyToFirestore(property, images, imagesHaveBeenUpdated)
    }

    private fun initializeFloorSpinner() {
        spinnerFloorAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.floor_spinner)
        )
        binding.editFloorSpinner.adapter = spinnerFloorAdapter
        binding.editFloorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemSelected = parent?.selectedItem as String
                floor = itemSelected
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSelected = parent?.getItemAtPosition(position) as String
                floor = itemSelected
            }
        }
    }

    private fun initializeRoomSpinner() {
        spinnerRoomAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.rooms_spinner)
            )
            binding.editRoomsSpinner.adapter = spinnerRoomAdapter
            binding.editRoomsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    val itemSelected = parent?.selectedItem as String
                    rooms = itemSelected
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val itemSelected = parent?.getItemAtPosition(position) as String
                    rooms = itemSelected
                }
            }
    }

    private fun initializeBathSpinner() {
        spinnerBathAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.baths_spinner)
        )
        binding.editBathsSpinner.adapter = spinnerBathAdapter
        binding.editBathsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemSelected = parent?.selectedItem as String
                baths = itemSelected
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSelected = parent?.getItemAtPosition(position) as String
                baths = itemSelected
            }
        }
    }

    private fun initializeBalconySpinner() {
        spinnerBalconyAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.balconies_spinner)
        )
        binding.editBalconiesSpinner.adapter = spinnerBalconyAdapter
        binding.editBalconiesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                val itemSelected = parent?.selectedItem as String
                balconies = itemSelected
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSelected = parent?.getItemAtPosition(position) as String
                balconies = itemSelected
            }
        }
    }

    private fun initializeTextFields() {
        editTextAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.neighborhood)
        )
        binding.editNeighborhoodText.setAdapter(editTextAdapter)
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
            imagesHaveBeenUpdated = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (imagesHaveBeenUpdated)
            binding.editRecyclerview.adapter = AddRecyclerViewAdapter(images)
    }

    companion object {
        private const val PICK_MULTIPLE_IMAGES = 1
    }

}

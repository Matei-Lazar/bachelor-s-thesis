package ths.kariru.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_add2.*

import ths.kariru.R
import ths.kariru.adapters.AddRecyclerViewAdapter
import ths.kariru.databinding.FragmentAdd2Binding
import ths.kariru.models.Address
import ths.kariru.models.Property
import ths.kariru.viewmodels.AddFragmentViewModel

class AddFragment2 : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel
    private lateinit var binding: FragmentAdd2Binding
    private lateinit var navController: NavController
    private lateinit var recyclerViewAdapter: AddRecyclerViewAdapter
    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>
    private lateinit var editTextAdapter: ArrayAdapter<String>

    private var images: MutableList<Uri> = arrayListOf()
    private var imageLimit = 5
    private lateinit var floor: String
    private lateinit var rooms: String
    private lateinit var baths: String
    private lateinit var balconies: String

    // Firebase
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add2, container, false)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        // Uploads photos from gallery
        images = arrayListOf()
        uploadPhotos(binding.add2UploadButton)

        initializeSpinners()
        initializeTextFields()
        setFocusListenerOnTextViews()

        // RecyclerView
        binding.apply {
            add2Recyclerview.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            add2Recyclerview.setHasFixedSize(true)
            recyclerViewAdapter = AddRecyclerViewAdapter(images)
            add2Recyclerview.adapter = recyclerViewAdapter
        }
        // Deletes all images from recycler wiew
        binding.add2DeleteButton.setOnClickListener {
            deletePhotos()
        }
        // Saves property to firestore
        binding.add2SaveButton.setOnClickListener {
            validateTextFields()
        }

        navController = findNavController()
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
        var floorProperty: String
        var roomsProperty: String
        var bathsProperty: String
        var balconiesProperty: String

        arguments?.let {
            val safeArgs = AddFragment2Args.fromBundle(it)
            latitude = safeArgs.latitude
            longitude = safeArgs.longitude
        }

        // Address
        val street = add2_street_text.editText?.text.toString()
        val streetNumber = binding.add2StreetNrText.editText?.text.toString()
        val blockName = binding.add2BlockNameText.editText?.text.toString()
        val apartmentNumber = binding.add2ApartmentNrText.editText?.text.toString().toInt()
        val neighborhood = binding.add2NeighborhoodText.text.toString()
        val address = Address(street, streetNumber, blockName, apartmentNumber, neighborhood)

        // Details
        floor.let { floorProperty = it }
        rooms.let { roomsProperty = it }
        baths.let { bathsProperty = it }
        balconies.let { balconiesProperty = it }
        val surface = binding.add2SurfaceText.editText?.text.toString()
        val description = binding.add2DescriptionText.editText?.text.toString()
        val price = binding.add2PriceText.editText?.text.toString()

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
            price = price.toInt()
        )
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

    private fun deletePhotos() {
        recyclerViewAdapter = AddRecyclerViewAdapter(images = arrayListOf())
        binding.add2Recyclerview.adapter = recyclerViewAdapter
        images = arrayListOf()
    }

    private fun setFocusListenerOnTextViews() {
        // The lambda function is the overriden function onFocusChange
        binding.apply {
            add2Street.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2StreetNr.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2Address.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2BlockName.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2ApartmentNr.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2NeighborhoodText.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2Surface.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2Description.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }

            add2Price.onFocusChangeListener =
                View.OnFocusChangeListener { v, hasFocus -> if (!hasFocus) v?.let { hideSoftKeyboard(it) } }
        }
    }

    private fun hideSoftKeyboard(view: View) {

        val inputMethodManager =
            context?.let { ContextCompat.getSystemService(it, InputMethodManager::class.java) }!!
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

    }

    private fun initializeSpinners() {
        binding.apply {
            spinnerArrayAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.floor_spinner)
            )
            add2FloorSpinner.adapter = spinnerArrayAdapter
            add2FloorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

            spinnerArrayAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.rooms_spinner)
            )
            add2RoomsSpinner.adapter = spinnerArrayAdapter
            add2RoomsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

            spinnerArrayAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.baths_spinner)
            )
            add2BathsSpinner.adapter = spinnerArrayAdapter
            add2BathsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

            spinnerArrayAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.balconies_spinner)
            )
            add2BalconiesSpinner.adapter = spinnerArrayAdapter
            add2BalconiesSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
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
    }

    private fun initializeTextFields() {
        editTextAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.neighborhood)
        )
        binding.add2NeighborhoodText.setAdapter(editTextAdapter)
    }

    private fun validateTextFields() {

        val isOkList = mutableListOf<Boolean>()
        var isOk = true

        if (binding.add2Street.text.isNullOrEmpty()) {
            binding.add2StreetText.isErrorEnabled = true
            binding.add2StreetText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2StreetText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2StreetNr.text.isNullOrEmpty()) {
            binding.add2StreetNrText.isErrorEnabled = true
            binding.add2StreetNrText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2StreetNrText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2BlockName.text.isNullOrEmpty()) {
            binding.add2BlockNameText.isErrorEnabled = true
            binding.add2BlockNameText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2BlockNameText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2ApartmentNr.text.isNullOrEmpty()) {
            binding.add2ApartmentNrText.isErrorEnabled = true
            binding.add2ApartmentNrText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2ApartmentNrText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2NeighborhoodText.text.isNullOrEmpty()) {
            binding.add2NeighborhoodText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2NeighborhoodText.error = null
            isOkList.add(true)
        }

        if (binding.add2Description.text.isNullOrEmpty()) {
            binding.add2DescriptionText.isErrorEnabled = true
            binding.add2DescriptionText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2DescriptionText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2Surface.text.isNullOrEmpty()) {
            binding.add2SurfaceText.isErrorEnabled = true
            binding.add2SurfaceText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2SurfaceText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2Price.text.isNullOrEmpty()) {
            binding.add2PriceText.isErrorEnabled = true
            binding.add2PriceText.error = "Field can't be empty"
            isOkList.add(false)
        } else {
            binding.add2PriceText.isErrorEnabled = false
            isOkList.add(true)
        }

        if (binding.add2Recyclerview.adapter?.itemCount == 0) {
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
            clearDataAfterSave()
            val action = AddFragment2Directions.add2ToAdd()
            navController.navigate(action)
        }
    }

    private fun clearDataAfterSave() {
        binding.apply {
            add2Street.text?.clear()
            add2StreetNr.text?.clear()
            add2BlockName.text?.clear()
            add2ApartmentNr.text?.clear()
            add2NeighborhoodText.text?.clear()
            deletePhotos()
            add2FloorSpinner.getItemAtPosition(0)
            add2RoomsSpinner.getItemAtPosition(0)
            add2BathsSpinner.getItemAtPosition(0)
            add2BalconiesSpinner.getItemAtPosition(0)
            add2Description.text?.clear()
            add2Surface.text?.clear()
            add2Price.text?.clear()
        }
        Toast.makeText(activity, "Property has been saved", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_MULTIPLE_IMAGES) {
                val clipData = data?.clipData
                if (clipData != null) { // handle multiple images
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        if (images.size < imageLimit)
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
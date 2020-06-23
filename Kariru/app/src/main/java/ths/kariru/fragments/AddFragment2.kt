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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add2.*

import ths.kariru.R
import ths.kariru.adapters.AddViewPagerAdapter
import ths.kariru.databinding.FragmentAdd2Binding
import ths.kariru.databinding.FragmentAddBinding
import ths.kariru.models.Address
import ths.kariru.models.Property
import ths.kariru.viewmodels.AddFragmentViewModel

class AddFragment2 : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel
    private var images: MutableList<Uri> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentAdd2Binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add2, container, false)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        // Uploads photos from gallery
        images = arrayListOf()
        uploadPhotos(binding.add2UploadButton)

        // Saves property to firestore
        binding.add2SaveButton.setOnClickListener {
            saveProperty()
        }

        return binding.root
    }

    private fun saveProperty() {
        val street = add_street_text.text.toString()
        val streetNumber = add_streetNr_text.text.toString().toInt()
        val blockName = add_blockName_text.text.toString()
        val apartmentNumber = add_apartmentNr_text.text.toString().toInt()
        val address = Address(street, streetNumber, blockName, apartmentNumber)
        val property = Property(address)
        viewModel.saveProperty(property)
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
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = AddViewPagerAdapter(images)
        add_view_pager_rv.adapter = adapter
    }

    companion object {
        private const val PICK_MULTIPLE_IMAGES = 1

    }

}

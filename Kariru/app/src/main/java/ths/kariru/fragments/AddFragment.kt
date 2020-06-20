package ths.kariru.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add.*
import ths.kariru.R
import ths.kariru.databinding.FragmentAddBinding
import ths.kariru.viewmodels.AddFragmentViewModel

class AddFragment : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentAddBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        uploadPhotos(binding.addUploadImage)

        return binding.root
    }

    private fun uploadPhotos(button: Button) {
        button.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 0) {
            val uri = data?.data
            add_image.setImageURI(uri)
        }
    }
}

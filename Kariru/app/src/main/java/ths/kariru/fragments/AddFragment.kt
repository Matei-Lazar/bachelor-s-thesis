package ths.kariru.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.fragment_add.*
import ths.kariru.MainActivity
import ths.kariru.MapsActivity
import ths.kariru.R
import ths.kariru.adapters.AddViewPagerAdapter
import ths.kariru.databinding.FragmentAddBinding
import ths.kariru.models.Address
import ths.kariru.models.Property
import ths.kariru.viewmodels.AddFragmentViewModel
import timber.log.Timber

class AddFragment : Fragment() {

    private lateinit var viewModel: AddFragmentViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: FragmentAddBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)

        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(AddFragmentViewModel::class.java)
        binding.viewModel = viewModel

        binding.searchMapButton.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            startActivityForResult(intent, REQUEST_PROPERTY_COORDINATES)
        }

        navController = findNavController()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PROPERTY_COORDINATES && resultCode == RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            var streetName = data.getStringExtra("streetName")
            var streetNumber = data.getStringExtra("streetNumber")

            if (streetName == null) streetName = ""
            if (streetNumber == null) streetNumber = ""

            val action = AddFragmentDirections.addToAdd2(
                latitude = latitude.toString(),
                longitude = longitude.toString(),
                streetName = streetName,
                streetNumber = streetNumber
            )
            navController.navigate(action)
        }
    }

    companion object {
        private const val REQUEST_PROPERTY_COORDINATES = 4
    }
}

package ths.kariru

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore
import ths.kariru.databinding.ActivityMapsBinding
import timber.log.Timber
import java.lang.reflect.Array
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var streetName: String
    private lateinit var streetNumber: String
    private lateinit var coordinates: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCLN3mCWd2uoO7zBW8LfiPCegwiwzPLPho")
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val placesClient: PlacesClient = Places.createClient(this)

        val autocompleteFragment: AutocompleteSupportFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS)
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(LatLng(46.516, 23.068),LatLng(46.982739, 24.161427)))
        autocompleteFragment.setCountries("RO")
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS))
        autocompleteFragment.setOnPlaceSelectedListener(placeSelectionListener)

        binding.mapNextButton.setOnClickListener {
            setResult(Activity.RESULT_OK,
            Intent().putExtra("latitude", coordinates.latitude)
                .putExtra("longitude", coordinates.longitude)
                .putExtra("streetName", streetName)
                .putExtra("streetNumber", streetNumber))
            finish()
            Timber.i("intent: streetNumber: $streetNumber")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val clujLatLong = LatLng(46.7712, 23.6236)
        val zoomLevel = 17f
        var firestore = FirebaseFirestore.getInstance()

        //map.addMarker(MarkerOptions().position(clujLatLong))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(clujLatLong, zoomLevel))
    }

    private val placeSelectionListener: PlaceSelectionListener
        get() = object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                if (place.addressComponents != null) {
                    val latitude: Double? = place.latLng?.latitude
                    val longitude: Double? = place.latLng?.longitude
                    streetName = place.addressComponents!!.asList()[1].name
                    streetNumber = place.addressComponents!!.asList()[0].name
                    Timber.i("onPlaceSelected: ${place.addressComponents}")
                }
                if (place.latLng != null) {
                    coordinates = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f))
                    map.addMarker(MarkerOptions().position(coordinates))
                }
                binding.mapNextButton.visibility = View.VISIBLE
            }

            override fun onError(status: Status) {
                Timber.i("onPlaceSelected error: ${status.statusMessage}")
            }
        }
}

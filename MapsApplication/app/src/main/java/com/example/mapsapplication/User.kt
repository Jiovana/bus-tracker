package com.example.mapsapplication

import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.DocumentReference
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener


class User : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)
        setUpMap()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        // isMyLocationEnabled = true enables the my-location layer which draws a light blue dot on the user’s location. It also adds a button to the map that, when tapped, centers the map on the user’s location.
        map.isMyLocationEnabled = true

        // fusedLocationClient.getLastLocation() gives you the most recent location currently available.
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // If you were able to retrieve the the most recent location, then move the camera to the user’s current location.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }

            val db = FirebaseFirestore.getInstance()

            val user: HashMap<String, Double> = HashMap();
            user.put("id", 0.0)
            user.put("latitude", location.latitude)
            user.put("longitude", location.longitude)

            // Add a new document with a generated ID
            db.collection("user")
                .add(user as Map<String, Any>)
                .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                    Log.d("tag","DocumentSnapshot added with ID: " + documentReference.id)
                })
                .addOnFailureListener(OnFailureListener { e ->
                    Log.w("tag","Error adding document", e)
                })


            db.collection("bus")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            var lat:Double = document.data.get("latitude") as Double
                            var lng:Double = document.data.get("longitude") as Double
                            map.addMarker(MarkerOptions().position(LatLng(lat, lng)).title("Onibus"));
                            Log.d("tag", document.id + " => " + document.data)
                        }
                    } else {
                        Log.w("tag", "Error getting documents.", task.exception)
                    }
                }



        }


    }
}
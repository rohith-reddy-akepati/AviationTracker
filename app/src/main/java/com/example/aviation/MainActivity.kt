package com.example.aviation

import AirportResponse
import AviationApiService
import FlightData
import FlightResponse
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var  googleMap: GoogleMap? = null
    private lateinit var refreshImageView: ImageView // Assuming you have this ImageView in your layout
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    private val flightDataMap = HashMap<LatLng, FlightData>()

    private val text1 = "Airplanes"
    private val text2 = "Airports"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textViewSwitcher)
        imageView = findViewById(R.id.ib_1)
        refreshImageView = findViewById(R.id.ib_2) // Initialize the refresh ImageView

        textView.text = text1

        imageView.setOnClickListener {
            // Clear existing markers
            googleMap?.clear()

            // Switch text and update markers accordingly
            textView.text = if (textView.text == text1) text2 else text1

            // Fetch data based on the current selection
            if (textView.text == text1) {
                fetchFlightData()
            } else {
                fetchAirportData()
            }
        }

        refreshImageView.setOnClickListener {
            // Trigger an immediate update without switching between modes
            googleMap?.clear()
            if (textView.text == text1) {
                fetchFlightData()
            } else {
                fetchAirportData()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFrag) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.setOnMarkerClickListener { marker ->
            val flightData = flightDataMap[marker.position] // Assuming flightDataMap is a HashMap<LatLng, FlightData>

            // Inflate the ticket layout
            val view = layoutInflater.inflate(R.layout.layout_ticket, null)

            // Set the flight details in the ticket
            view.findViewById<TextView>(R.id.tv_title).text = "Flight Number: ${flightData?.flight?.iata}"
            view.findViewById<TextView>(R.id.tv_from).text = "From: ${flightData?.departure?.airport}"
            view.findViewById<TextView>(R.id.tv_to).text = "To: ${flightData?.arrival?.airport}"
            view.findViewById<TextView>(R.id.tv_status).text = "Status: ${flightData?.flight_status}"

            // Add the ticket to the bottom of the screen
            // Assuming you have a LinearLayout or similar in your activity's layout where you can add the ticket
            val ticketContainer = findViewById<LinearLayout>(R.id.ticketLayout)
            ticketContainer.removeAllViews()
            ticketContainer.addView(view)
            ticketContainer.visibility = View.VISIBLE


            false // Return true to indicate that we have handled the event and no further processing is required
        }
        fetchFlightData()
    }

    fun resizeIcon(context: Context, resourceId: Int, width: Int, height: Int): BitmapDescriptor {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

    private fun fetchFlightData() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aviationstack.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AviationApiService::class.java)
        service.getActiveFlights().enqueue(object : Callback<FlightResponse> {
            override fun onResponse(call: Call<FlightResponse>, response: Response<FlightResponse>) {
                response.body()?.data?.forEach { flightData ->

                    flightData.live?.let {
                        if (it.latitude != null && it.longitude != null) {
                            // Resize the icon to the desired dimensions, e.g., 100x100 pixels
                            val flightIcon = resizeIcon(this@MainActivity, R.mipmap.airplane_icon_custom_foreground, 100, 100)
                            val latLng = LatLng(it.latitude, it.longitude)
                            val markerOptions = MarkerOptions()
                                .position(latLng)
                                .icon(flightIcon).title("IATA: ${flightData.flight?.iata}") // Use the resized icon here
                            googleMap?.addMarker(markerOptions)
                            flightDataMap[latLng] = flightData

                        }
                    }
                }
            }

            override fun onFailure(call: Call<FlightResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchAirportData() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aviationstack.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AviationApiService::class.java)
        val call = service.getAirports() // Assuming getAirports is implemented in AviationApiService

        call.enqueue(object : Callback<AirportResponse> { // Assuming AirportResponse is the correct model for airport data
            override fun onResponse(call: Call<AirportResponse>, response: Response<AirportResponse>) {
                response.body()?.data?.forEach { airport ->
                        val lat = airport.latitude.toDouble()
                        val lng = airport.longitude.toDouble()
                        val flightIcon = resizeIcon(this@MainActivity, R.mipmap.airport_custom_foreground, 100, 100)
                        val markerOptions = MarkerOptions()
                            .position(LatLng(lat, lng))
                            .icon(flightIcon)
                    googleMap?.addMarker(markerOptions)

                }
            }

            override fun onFailure(call: Call<AirportResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
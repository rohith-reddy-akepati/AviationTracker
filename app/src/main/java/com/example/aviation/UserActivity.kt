package com.example.aviation

import AviationApiService
import FlightResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var flightsRecyclerView: RecyclerView
    private lateinit var noResultsImageView: ImageView
    private lateinit var flightsAdapter: FlightsAdapter
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        searchEditText = findViewById(R.id.searchEditText)
        flightsRecyclerView = findViewById(R.id.flightsRecyclerView)
        noResultsImageView = findViewById(R.id.noResultsImageView)
        searchButton = findViewById(R.id.searchButton)

        flightsAdapter = FlightsAdapter(listOf())
        flightsRecyclerView.layoutManager = LinearLayoutManager(this)
        flightsRecyclerView.adapter = flightsAdapter

        searchButton.setOnClickListener {
            searchFlights(searchEditText.text.toString())
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

//    private fun searchFlights(flightNumber: String) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.aviationstack.com/") // Replace with your actual base URL
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val service = retrofit.create(AviationApiService::class.java)
//
//        service.getFlightsByIata(flightNumber).enqueue(object : Callback<FlightResponse> {
//            override fun onResponse(call: Call<FlightResponse>, response: Response<FlightResponse>) {
//                if (response.isSuccessful) {
//                    Log.d("API Response", response.body().toString()) // Check the actual API response
//                    val flights = response.body()?.data
//                    if (flights.isNullOrEmpty()) {
//                        noResultsImageView.visibility = View.VISIBLE
//                        flightsRecyclerView.visibility = View.GONE
//                    } else {
//                        Log.d("FlightData", flights.toString()) // Add this line
//
//                        flightsAdapter.updateFlights(flights)
//                        noResultsImageView.visibility = View.GONE
//                        flightsRecyclerView.visibility = View.VISIBLE
//
//                        val departureAirport = flights[0].departure?.airport
//                        val arrivalAirport = flights[0].arrival?.airport
//                        searchEditText.setText("$departureAirport - $arrivalAirport")
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<FlightResponse>, t: Throwable) {
//                // Handle the error
//            }
//        })
//    }
    private fun searchFlights(flightNumber: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aviationstack.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AviationApiService::class.java)

        service.getFlightsByIata(flightNumber).enqueue(object : Callback<FlightResponse> {
            override fun onResponse(call: Call<FlightResponse>, response: Response<FlightResponse>) {
                if (response.isSuccessful) {
                    Log.d("API Response", response.body().toString())
                    val flights = response.body()?.data
                    if (flights.isNullOrEmpty()) {
                        noResultsImageView.visibility = View.VISIBLE
                        flightsRecyclerView.visibility = View.GONE
                    } else {
                        Log.d("FlightData", flights.toString())

                        // Pass the list of flights to the adapter
                        flightsAdapter.updateFlights(flights)
                        noResultsImageView.visibility = View.GONE
                        flightsRecyclerView.visibility = View.VISIBLE

//                        val departureAirport = flights[0].departure?.airport ?: "Unknown"
//                        val arrivalAirport = flights[0].arrival?.airport ?: "Unknown"
//                        searchEditText.setText("$departureAirport - $arrivalAirport")
                    }
                } else {
                    Log.e("API Error", response.message()) // Log error in case of bad response
                }
            }

            override fun onFailure(call: Call<FlightResponse>, t: Throwable) {
                Log.e("API Failure", t.message.toString()) // Log the error
            }
        }
    )}
}



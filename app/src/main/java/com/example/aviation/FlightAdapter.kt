package com.example.aviation

import FlightData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//class FlightsAdapter(private var flights: List<FlightData>) : RecyclerView.Adapter<FlightsAdapter.FlightViewHolder>() {
//
//    class FlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val flightNumberTextView: TextView = view.findViewById(R.id.flightNumberTextView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight, parent, false)
//        return FlightViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
//        holder.flightNumberTextView.text = flights[position].flight?.iata
//    }
//
//    override fun getItemCount() = flights.size
//
//    fun updateFlights(newFlights: List<FlightData>) {
//        flights = newFlights
//        notifyDataSetChanged()
//    }
//}

class FlightsAdapter(private var flights: List<FlightData>) : RecyclerView.Adapter<FlightsAdapter.FlightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flights[position]
        val flightNumber = flight.flight?.number ?: "Unknown"
        val airlineName = flight.airline?.name ?: "Unknown Airline"
        val departureAirport = flight.departure?.airport ?: "Unknown Departure"
        val arrivalAirport = flight.arrival?.airport ?: "Unknown Arrival"

        holder.flightNumberTextView.text = "$flightNumber - $airlineName"
        holder.departureTextView.text = "Departure: $departureAirport"
        holder.arrivalTextView.text = "Arrival: $arrivalAirport"
    }

    override fun getItemCount(): Int {
        return flights.size
    }

    // Method to update flights list
    fun updateFlights(newFlights: List<FlightData>) {
        flights = newFlights
        notifyDataSetChanged() // Notify the adapter of data change
    }

    class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightNumberTextView: TextView = itemView.findViewById(R.id.flightNumberTextView)
        val departureTextView: TextView = itemView.findViewById(R.id.departureTextView)
        val arrivalTextView: TextView = itemView.findViewById(R.id.arrivalTextView)
    }
}

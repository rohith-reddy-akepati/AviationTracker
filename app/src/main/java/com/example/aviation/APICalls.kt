import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AviationApiService {
    @GET("v1/flights?access_key=3accadadce7d658423b32d8182ccd86b&flight_status=active")
    fun getActiveFlights(): Call<FlightResponse>

    @GET("v1/flights?access_key=3accadadce7d658423b32d8182ccd86b")
    fun getFlightsByIata(@Query("flight_iata") flightIata: String): Call<FlightResponse>

    @GET("v1/airports?access_key=3accadadce7d658423b32d8182ccd86b")
    fun getAirports(): Call<AirportResponse>
}

data class FlightResponse(val data: List<FlightData>)

data class FlightData(
    val live: FlightLive?,
    val airline: FlightAirline?,
    val flight: FlightDetails?,
    val departure: FlightDeparture?, // Add this line
    val arrival: FlightArrival?, // Add this line
    val flight_status: String?
)
data class FlightLive(val latitude: Double?, val longitude: Double?)
data class FlightAirline(val name: String?, val iata: String?, val icao: String?) // Add this
data class FlightDetails(val number: String?, val iata: String?, val icao: String?) // Add number field
data class FlightDeparture(val airport: String?, val timezone: String?, val iata: String?, val terminal: String?, val gate: String?, val delay: String?, val scheduled: String?, val estimated: String?)
data class FlightArrival(val airport: String?, val timezone: String?, val iata: String?, val terminal: String?, val gate: String?, val delay: String?, val scheduled: String?, val estimated: String?)

data class AirportResponse(
    val data: List<AirportData>
)

data class AirportData(
    val latitude: String,
    val longitude: String,
    val airport_name: String
)
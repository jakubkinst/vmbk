package cz.kinst.jakub.vmbk.model

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


data class WeatherResponse(
        val weather: List<WeatherType>,
        val main: WeatherMain,
        val name: String
)

data class WeatherForecastItem(
        val dt: Long,
        @SerializedName("dt_txt") val dtTxt: String,
        val weather: List<WeatherType>,
        val main: WeatherMain,
        val name: String
)

data class WeatherType(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
)

data class WeatherMain(
        val temp: Double,
        val pressure: Double,
        val humidity: Double,
        @SerializedName("temp_min") val tempMin: Double,
        @SerializedName("temp_max") val tempMax: Double
)

data class WeatherForecastResponse(val list: List<WeatherForecastItem>)

interface WeatherApi {
    @GET("weather")
    fun getWeatherByPlace(@Query("q") query: String): Call<WeatherResponse>

    @GET("weather")
    fun getWeatherByLocation(@Query("lat") latitude: Double, @Query("lon") longitude: Double): Call<WeatherResponse>

    @GET("forecast")
    fun getWeatherForecastByPlace(@Query("q") query: String): Call<WeatherForecastResponse>

    @GET("forecast")
    fun getWeatherForecastByLocation(@Query("lat") latitude: Double, @Query("lon") longitude: Double): Call<WeatherForecastResponse>
}
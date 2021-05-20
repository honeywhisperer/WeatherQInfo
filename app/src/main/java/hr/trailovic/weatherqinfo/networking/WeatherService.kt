package hr.trailovic.weatherqinfo.networking

import hr.trailovic.weatherqinfo.BuildConfig
import hr.trailovic.weatherqinfo.model.WeatherTodayResponse
import hr.trailovic.weatherqinfo.model.WeatherWeekResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    // Rx

    @GET("weather/")
    fun getCurrentWeatherForTodayRx(

        @Query("q")
        city: String,

        @Query("appid")
        appid: String = BuildConfig.API_KEY,

        @Query("units")
        units: String = "metric"

    ): Observable<WeatherTodayResponse>


    @GET("onecall")
    fun getWeatherForWeekRx(

        @Query("lon")
        lon: Double,

        @Query("lat")
        lat: Double,

        @Query("exclude")
        exclude: String = "current,minutely,hourly,alerts",

        @Query("appid")
        appid: String = BuildConfig.API_KEY,

        @Query("units")
        units: String = "metric"

    ): Observable<WeatherWeekResponse>
}
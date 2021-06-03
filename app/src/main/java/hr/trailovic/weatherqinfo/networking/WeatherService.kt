package hr.trailovic.weatherqinfo.networking

import hr.trailovic.weatherqinfo.BuildConfig
import hr.trailovic.weatherqinfo.model.CityResponse
import hr.trailovic.weatherqinfo.model.WeatherTodayResponse
import hr.trailovic.weatherqinfo.model.WeatherWeekResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    // Rx

    @GET("data/2.5/weather/")
    fun getCurrentWeatherForTodayRx(

        @Query("q")
        city: String,

        @Query("appid")
        appid: String/* = BuildConfig.API_KEY*/,

        @Query("units")
        units: String = "metric"

    ): Observable<WeatherTodayResponse>


    @GET("data/2.5/onecall")
    fun getWeatherForWeekRx(

        @Query("lon")
        lon: Double,

        @Query("lat")
        lat: Double,

        @Query("exclude")
        exclude: String = "current,minutely,hourly,alerts",

        @Query("appid")
        appid: String/* = BuildConfig.API_KEY*/,

        @Query("units")
        units: String = "metric"

    ): Observable<WeatherWeekResponse>


    @GET("geo/1.0/direct")
    fun getListOfCitiesRx(

        @Query("q")
        city:String,

        @Query("limit")
        limit:Int = 50,

        @Query("appid")
        appid: String/* = BuildConfig.API_KEY*/,
    ): Observable<List<CityResponse>?>
}
package hr.trailovic.weatherqinfo.networking

import hr.trailovic.weatherqinfo.model.CityResponse
import hr.trailovic.weatherqinfo.model.WeatherTodayResponse
import hr.trailovic.weatherqinfo.model.WeatherWeekResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    // Rx

    @GET("data/2.5/weather/")
    fun fetchWeatherTodayForCityRx(

//        @Query("q")
//        city: String,

        @Query("lon")
        lon: Double,

        @Query("lat")
        lat: Double,

        @Query("appid")
        appid: String/* = BuildConfig.API_KEY*/,

        @Query("units")
        units: String = "metric"

    ): Observable<WeatherTodayResponse>


    @GET("data/2.5/onecall")
    fun fetchWeatherWeekForCityRx(

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
    fun getCitiesListRx(

        @Query("q")
        city:String,

        @Query("limit")
        limit:Int = 50,

        @Query("appid")
        appid: String/* = BuildConfig.API_KEY*/,
    ): Observable<List<CityResponse>?>

    @GET("data/2.5/weather/")
    fun fetchWeatherTodayForLocationRxSingle(

        @Query("lon")
        lon: Double,

        @Query("lat")
        lat: Double,

        @Query("appid")
        appid: String/* = BuildConfig.API_KEY*/,

        @Query("units")
        units: String = "metric"

    ): Single<WeatherTodayResponse>

//    /**
//     * Reverse Geocoding
//     * */
//    @GET("geo/1.0/reverse")
//    fun fetchWeatherTodayForLocationRx(
//
//        @Query("lon")
//        lon: Double,
//
//        @Query("lat")
//        lat: Double,
//
//        @Query("limit")
//        limit: Int = 1,
//
//        @Query("appid")
//        appid: String/* = BuildConfig.API_KEY*/,
//
//    ): Flowable<ResponseBody>
}
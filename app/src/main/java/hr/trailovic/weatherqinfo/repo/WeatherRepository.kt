package hr.trailovic.weatherqinfo.repo

import android.util.Log
import androidx.lifecycle.LiveData
import hr.trailovic.weatherqinfo.database.AppDatabase
import hr.trailovic.weatherqinfo.model.*
import hr.trailovic.weatherqinfo.networking.WeatherService
import io.reactivex.Observable
import javax.inject.Inject

private const val TAG = "wR:::"

class WeatherRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: WeatherService
) {

    private val weatherTodayDao = appDatabase.weatherTodayDao()
    private val weatherWeekDao = appDatabase.weatherWeekDao()
    private val cityDao = appDatabase.cityDao()

    /*city - Room*/

    fun getAllCitiesRx(): Observable<List<City>> {
        return cityDao.getAllCitiesRx()
    }

    fun getAllCitiesLD(): LiveData<List<City>> {
        return cityDao.getAllCitiesLD()
    }

    suspend fun removeCity(city: City) {
        cityDao.removeCity(city)
    }

    suspend fun removeAllCities() {
        cityDao.removeAll()
    }

    fun addCity(city: City) {
        cityDao.addCity(city)
    }

    /*weather today - Room*/

    fun getAllWeatherTodayRx(): Observable<List<WeatherToday>> {
        return weatherTodayDao.getAllWeatherTodayRx()
    }

    suspend fun removeWeatherToday(weatherToday: WeatherToday) {
        weatherTodayDao.removeWeatherToday(weatherToday)
    }

    suspend fun removeWeatherTodayByCityName(cityName: String){
        weatherTodayDao.removeWeatherTodayByCityName(cityName)
    }

    suspend fun removeAllWeatherToday() {
        weatherTodayDao.removeAllWeatherToday()
    }

    fun addWeatherToday(weatherToday: WeatherToday) {
        weatherTodayDao.addWeatherToday(weatherToday)
    }

    /*weather today - Api*/

    fun fetchWeatherTodayForCity(city: City): Observable<WeatherTodayResponse> {
        return apiService.getCurrentWeatherForTodayRx(city.fullName)
    }

//    fun fetchCoordinatesForCity(cityName: String): Observable<WeatherTodayResponse> {
//        return apiService.getCurrentWeatherForTodayRx(cityName)
//    }

    fun fetchCoordinatesForCity(cityName: String): WeatherTodayResponse? {
        var response: WeatherTodayResponse?
        try {
            Log.d(TAG, "fetchCoordinatesForCity: try: we are here")
            response = apiService.getCurrentWeatherForTodayRx(cityName).blockingFirst()
        } catch (e: Throwable) {
            Log.e(TAG, "fetchCoordinatesForCity: catch: we are here", e)
            response = null
        } finally {
            Log.d(TAG, "fetchCoordinatesForCity: finally: we are here")
        }
        return response
    }

    /*weather week - Room*/

    fun getAllWeatherWeekRx(): Observable<List<WeatherWeek>> {
        return weatherWeekDao.getAllWeatherWeekRx()
    }

    suspend fun removeAllWeatherWeek() {
        weatherWeekDao.removeAllWeatherWeek()
    }

    suspend fun removeWeatherWeekByCityName(cityName: String){
        weatherWeekDao.removeWeatherWeekByCityName(cityName)
    }

    fun addWeatherWeek(weatherWeek: WeatherWeek) {
        weatherWeekDao.addWeatherWeek(weatherWeek)
    }

    /*weather week - Api*/

    fun fetchWeatherWeekForCity(city: City): Observable<WeatherWeekResponse> {
        return apiService.getWeatherForWeekRx(city.lon, city.lat)
    }
}
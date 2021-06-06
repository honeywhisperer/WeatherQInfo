package hr.trailovic.weatherqinfo.repo

import androidx.lifecycle.LiveData
import hr.trailovic.weatherqinfo.database.AppDatabase
import hr.trailovic.weatherqinfo.model.*
import hr.trailovic.weatherqinfo.networking.WeatherService
import io.reactivex.Observable
import javax.inject.Inject

private const val TAG = "wR:::"

class WeatherRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val apiService: WeatherService,
    private val prefsHelper: SharedPreferencesHelper,
) {

    private val weatherTodayDao = appDatabase.weatherTodayDao()
    private val weatherWeekDao = appDatabase.weatherWeekDao()
    private val cityDao = appDatabase.cityDao()

    /* <<< City API */
    fun getCitiesListRx(cityName: String): Observable<List<CityResponse>?> {
        return apiService.getCitiesListRx(cityName, appid = prefsHelper.readApiKey())
    }
    /* City API >>> */


    /* <<< City Room */
    fun getAllCitiesRx(): Observable<List<City>> {
        return cityDao.getAllCitiesRx()
    }

    fun getAllCitiesLD(): LiveData<List<City>> {
        return cityDao.getAllCitiesLD()
    }

    suspend fun removeCitySuspend(city: City) {
        cityDao.removeCitySuspend(city)
    }

    suspend fun removeAllCitiesSuspend() {
        cityDao.removeAllCitiesSuspend()
    }

    fun addCity(city: City) {
        cityDao.addCity(city)
    }

    suspend fun addCitySuspend(city: City) {
        cityDao.addCitySuspend(city)
    }

    suspend fun getCityByCoordinatesSuspend(lat: Double, lon: Double): City? {
        return cityDao.getCityByCoordinatesSuspend(lat, lon)
    }

    suspend fun getAllCitiesList(): List<City> {
        return cityDao.getAllCitiesList()
    }
    /* City Room >>> */


    /* <<< Weather Today Room */
    fun getAllWeatherTodayRx(): Observable<List<WeatherToday>> {
        return weatherTodayDao.getAllWeatherTodayRx()
    }

    suspend fun removeWeatherTodaySuspend(weatherToday: WeatherToday) {
        weatherTodayDao.removeWeatherTodaySuspend(weatherToday)
    }

    suspend fun removeWeatherTodayByCityNameSuspend(cityName: String) {
        weatherTodayDao.removeWeatherTodayByCityNameSuspend(cityName)
    }

    suspend fun removeAllWeatherTodaySuspend() {
        weatherTodayDao.removeAllWeatherTodaySuspend()
    }

    suspend fun addWeatherTodaySuspend(weatherToday: WeatherToday) {
        weatherTodayDao.addWeatherTodaySuspend(weatherToday)
    }

    fun getWeatherTodayForCity(city: City): WeatherToday? {
        return weatherTodayDao.getWeatherTodayForCity(city.fullName)
    }
    /* Weather Today Room >>> */

    /* <<< Weather Today Api */
    fun fetchWeatherTodayForCityRx(city: City): Observable<WeatherTodayResponse> {
        return apiService.fetchWeatherTodayForCityRx(city.lon, city.lat, prefsHelper.readApiKey())
    }
    /* Weather Today Api >>> */


    /* <<< Weather Week Room */
    fun getAllWeatherWeekRx(): Observable<List<WeatherWeek>> {
        return weatherWeekDao.getAllWeatherWeekRx()
    }

    suspend fun removeAllWeatherWeekSuspend() {
        weatherWeekDao.removeAllWeatherWeekSuspend()
    }

    suspend fun removeWeatherWeekByCityNameSuspend(cityName: String) {
        weatherWeekDao.removeWeatherWeekByCityNameSuspend(cityName)
    }

    suspend fun addWeatherWeekSuspend(weatherWeek: WeatherWeek) {
        weatherWeekDao.addWeatherWeekSuspend(weatherWeek)
    }

    fun getWeatherWeekForCity(city: City): List<WeatherWeek>? {
        return weatherWeekDao.getWeatherWeekForCity(city.fullName)
    }
    /* Weather Week Room >>> */

    /* <<< Weather Week API */
    fun fetchWeatherWeekForCityRx(city: City): Observable<WeatherWeekResponse> {
        return apiService.fetchWeatherWeekForCityRx(
            city.lon,
            city.lat,
            appid = prefsHelper.readApiKey()
        )
    }
    /* Weather Week API >>> */
}
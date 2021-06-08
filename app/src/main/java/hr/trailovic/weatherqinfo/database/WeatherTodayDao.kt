package hr.trailovic.weatherqinfo.database

import androidx.room.*
import hr.trailovic.weatherqinfo.model.WeatherToday
import io.reactivex.Observable


@Dao
interface WeatherTodayDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeatherTodaySuspend(weatherToday: WeatherToday)

    @Delete
    suspend fun removeWeatherTodaySuspend(weatherToday: WeatherToday)

    @Query("DELETE FROM weathertoday")
    suspend fun removeAllWeatherTodaySuspend()

    @Query("DELETE FROM weathertoday WHERE upper(locationFullName) LIKE upper(:cityName)")
    suspend fun removeWeatherTodayByCityNameSuspend(cityName: String)

    @Transaction
    suspend fun addOrUpdateWeatherTodaySuspend(weatherToday: WeatherToday){
        removeWeatherTodayByCityNameSuspend(weatherToday.locationFullName)
        addWeatherTodaySuspend(weatherToday)
    }

    // GET

    @Query("SELECT * FROM weathertoday")
    fun getAllWeatherTodayRx(): Observable<List<WeatherToday>>

    @Query("SELECT * FROM weathertoday WHERE upper(locationFullName) LIKE upper(:cityFullName)")
    fun getWeatherTodayForCity(cityFullName: String): WeatherToday?


    @Query("SELECT * FROM weathertoday")
    suspend fun getAllWeatherTodayList(): List<WeatherToday>
}
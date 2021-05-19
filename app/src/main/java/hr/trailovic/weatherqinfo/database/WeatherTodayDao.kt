package hr.trailovic.weatherqinfo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import hr.trailovic.weatherqinfo.model.City
import hr.trailovic.weatherqinfo.model.WeatherToday
import io.reactivex.Observable


@Dao
interface WeatherTodayDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /*suspend*/ fun addWeatherToday(weatherToday: WeatherToday)

    @Delete
    suspend fun removeWeatherToday(weatherToday: WeatherToday)

    @Query("DELETE FROM weathertoday")
    suspend fun removeAllWeatherToday()

    @Query("DELETE FROM weathertoday WHERE upper(city) LIKE upper(:cityName)")
    suspend fun removeWeatherTodayByCityName(cityName: String)

    // GET

    @Query("SELECT * FROM weathertoday")
    fun getAllWeatherTodayRx(): Observable<List<WeatherToday>>


    @Query("SELECT * FROM weathertoday")
    suspend fun getAllWeatherTodayList(): List<WeatherToday>
}
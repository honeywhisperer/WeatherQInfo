package hr.trailovic.weatherqinfo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.trailovic.weatherqinfo.model.WeatherWeek
import io.reactivex.Observable

@Dao
interface WeatherWeekDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeatherWeekSuspend(weatherWeek: WeatherWeek)

    @Query("DELETE FROM weatherweek")
    suspend fun removeAllWeatherWeekSuspend()

    @Query("DELETE FROM weatherweek WHERE upper(locationFullName) LIKE upper(:cityName)")
    suspend fun removeWeatherWeekByCityNameSuspend(cityName: String)

    // GET

    @Query("SELECT * FROM weatherweek")
    fun getAllWeatherWeekRx(): Observable<List<WeatherWeek>>

    @Query("SELECT * FROM weatherweek WHERE upper(locationFullName) LIKE upper(:cityName)")
    fun getWeatherWeekForCity(cityName: String): List<WeatherWeek>?

}
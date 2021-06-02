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
    fun addWeatherWeek(weatherWeek: WeatherWeek)

    @Query("DELETE FROM weatherweek")
    suspend fun removeAllWeatherWeek()

    @Query("DELETE FROM weatherweek WHERE upper(location) LIKE upper(:cityName)")
    suspend fun removeWeatherWeekByCityName(cityName: String)

    // GET

    @Query("SELECT * FROM weatherweek")
    fun getAllWeatherWeekRx(): Observable<List<WeatherWeek>>

}
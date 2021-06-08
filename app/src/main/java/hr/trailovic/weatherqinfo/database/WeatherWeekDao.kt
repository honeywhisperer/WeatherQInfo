package hr.trailovic.weatherqinfo.database

import androidx.room.*
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

    @Transaction
    suspend fun addOrUpdateWeatherWeekSuspend(weatherWeekList : List<WeatherWeek>){
        removeWeatherWeekByCityNameSuspend(weatherWeekList[0].locationFullName)
        weatherWeekList.forEach {
            addWeatherWeekSuspend(it)
        }
    }

    // GET

    @Query("SELECT * FROM weatherweek")
    fun getAllWeatherWeekRx(): Observable<List<WeatherWeek>>

    @Query("SELECT * FROM weatherweek WHERE upper(locationFullName) LIKE upper(:cityName)")
    fun getWeatherWeekForCity(cityName: String): List<WeatherWeek>?

}
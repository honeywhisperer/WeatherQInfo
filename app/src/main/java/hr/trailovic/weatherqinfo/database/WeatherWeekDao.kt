package hr.trailovic.weatherqinfo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.trailovic.weatherqinfo.model.WeatherWeek
import io.reactivex.Observable

@Dao
interface WeatherWeekDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    /*suspend*/ fun addWeatherWeek(weatherWeek: WeatherWeek)

    @Query("DELETE FROM weatherweek")
    suspend fun removeAllWeatherWeek()

    // GET

    @Query("SELECT * FROM weatherweek")
    fun getAllWeatherWeekRx(): Observable<List<WeatherWeek>>

}
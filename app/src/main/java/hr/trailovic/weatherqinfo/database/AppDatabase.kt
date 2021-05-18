package hr.trailovic.weatherqinfo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import hr.trailovic.weatherqinfo.model.City
import hr.trailovic.weatherqinfo.model.WeatherToday
import hr.trailovic.weatherqinfo.model.WeatherWeek

@Database(entities = [City::class, WeatherToday::class, WeatherWeek::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun weatherTodayDao(): WeatherTodayDao
    abstract fun weatherWeekDao(): WeatherWeekDao
}
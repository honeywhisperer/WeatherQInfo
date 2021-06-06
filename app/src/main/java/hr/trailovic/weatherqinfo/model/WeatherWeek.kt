package hr.trailovic.weatherqinfo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity
data class WeatherWeek(
    @ColumnInfo val locationFullName: String,
    @ColumnInfo val sunrise: Long,
    @ColumnInfo val sunset: Long,
    @ColumnInfo val tempDay: Double,
    @ColumnInfo val tempMin: Double,
    @ColumnInfo val tempMax: Double,
    @ColumnInfo val weatherDescription: String,
    @ColumnInfo val weatherIcon: String,
    @ColumnInfo val pressure: Int,
    @ColumnInfo val humidity: Int,
    @ColumnInfo val windSpeed: Double,
    @ColumnInfo val rain: Double,
    @ColumnInfo val uvi: Double,
    @ColumnInfo val timeTag: Long = System.currentTimeMillis(),
    @PrimaryKey val id: String = locationFullName + sunrise.toString()
) : Parcelable

// --- API response

data class WeatherWeekResponse(
    @field:Json(name = "lat")
    val lat: Double,
    @field:Json(name = "lon")
    val lon: Double,
    @field:Json(name = "timezone")
    val timezone: String,
    @field:Json(name = "timezone_offset")
    val timezoneOffset: Int,

    @field:Json(name = "daily")
    val daily: List<DailyData>,
)

data class DailyData(
    @field:Json(name = "sunrise")
    val sunrise: Long,
    @field:Json(name = "sunset")
    val sunset: Long,
    @field:Json(name = "temp")
    val temp: Temperature,

    @field:Json(name = "weather")
    val weather: List<Weather7DayDescription>,
    @field:Json(name = "pressure")
    val pressure: Int,
    @field:Json(name = "humidity")
    val humidity: Int,
    @field:Json(name = "wind_speed")
    val windSpeed: Double,
    @field:Json(name = "rain")
    val rain: Double,
    @field:Json(name = "uvi")
    val uvi: Double,
)

data class Temperature(
    @field:Json(name = "day")
    val day: Double,
    @field:Json(name = "min")
    val min: Double,
    @field:Json(name = "max")
    val max: Double,
)

data class Weather7DayDescription(
    @field:Json(name = "description")
    val description: String,
    @field:Json(name = "icon")
    val icon: String,
)
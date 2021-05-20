package hr.trailovic.weatherqinfo.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class WeatherToday(
    @ColumnInfo val cod: Int,

    @PrimaryKey val city: String,
    @ColumnInfo val country: String,

    @ColumnInfo val sunrise: Long,
    @ColumnInfo val sunset: Long,

    @ColumnInfo val temp: Double,
    @ColumnInfo val feels_like: Double,
    @ColumnInfo val temp_min: Double,
    @ColumnInfo val temp_max: Double,

    @ColumnInfo val pressure: Int,
    @ColumnInfo val humidity: Int,

    @ColumnInfo val main: String,
    @ColumnInfo val description: String,
    @ColumnInfo val icon: String,
) : Parcelable

// --- API response

data class WeatherTodayResponse(

    @field:Json(name = "cod")
    val cod: Int,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "id")
    val id: Long,
    @field:Json(name = "timezone")
    val timezone: Int, // shift in seconds from UTC

    @field:Json(name = "sys")
    val sys: Sys,

    @field:Json(name = "dt")
    val dt: Long, // time of data calculation

    @field:Json(name = "snow")
    val snow: Snow,

    @field:Json(name = "rain")
    val rain: Rain,

    @field:Json(name = "clouds")
    val clouds: Clouds,

    @field:Json(name = "wind")
    val wind: Wind,

    @field:Json(name = "main")
    val main: Main,

    @field:Json(name = "visibility")
    val visibility: Int,

    @field:Json(name = "weather")
    val weather: List<Weather>,
    @field:Json(name = "coord")
    val coord: Coord,
)

data class Sys(
    @field:Json(name = "country")
    val country: String,
    @field:Json(name = "sunrise")
    val sunrise: Long,
    @field:Json(name = "sunset")
    val sunset: Long,
)

data class Wind(
    @field:Json(name = "speed")
    val speed: Double,
    @field:Json(name = "deg")
    val deg: Int,
)

data class Main(
    @field:Json(name = "temp")
    val temp: Double,
    @field:Json(name = "feels_like")
    val feels_like: Double,
    @field:Json(name = "temp_min")
    val temp_min: Double,
    @field:Json(name = "temp_max")
    val temp_max: Double,
    @field:Json(name = "pressure")
    val pressure: Int,
    @field:Json(name = "humidity")
    val humidity: Int,
)

data class Weather(
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "main")
    val main: String,
    @field:Json(name = "description")
    val description: String,
    @field:Json(name = "icon")
    val icon: String,
)

data class Coord(
    @field:Json(name = "lon")
    val lon: Double,
    @field:Json(name = "lat")
    val lat: Double,
)

data class Rain(
    @field:Json(name = "1h")
    val h: String,
)

data class Clouds(
    @field:Json(name = "all")
    val all: Int,
)

data class Snow(
    @field:Json(name = "snow.1h")
    val snow1h: Int,
    @field:Json(name = "snow.3h")
    val snow3h: Int,
)

// --- Wrapper

data class WeatherTodayResponseWrapper(val weatherTodayResponse: WeatherTodayResponse?)
package hr.trailovic.weatherqinfo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

@Entity
data class City(
    @ColumnInfo val name: String,
    @ColumnInfo val country: String,
    @ColumnInfo val state: String?=null, //for US, and maybe some other country...
    @ColumnInfo val lon: Double,
    @ColumnInfo val lat: Double,
    @PrimaryKey val fullName: String = state?.let { "$name, $it, $country" } ?: "$name, $country",
    @ColumnInfo val id: String = UUID.randomUUID().toString()
)

data class CityResponse(

    @field:Json(name = "name")
    val name: String?,

    @field:Json(name = "lat")
    val lat: Double?,

    @field:Json(name = "lon")
    val lon: Double?,

    @field:Json(name = "country")
    val country: String?,

    @field:Json(name = "state")
    val state: String?
)
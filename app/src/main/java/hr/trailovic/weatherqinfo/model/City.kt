package hr.trailovic.weatherqinfo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class City(
    @ColumnInfo val name: String,
    @ColumnInfo val country: String,
    @ColumnInfo val lon: Double,
    @ColumnInfo val lat: Double,
    @PrimaryKey val fullName: String = "$name, $country",
    @ColumnInfo val id: String = UUID.randomUUID().toString()
)

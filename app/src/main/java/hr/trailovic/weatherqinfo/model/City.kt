package hr.trailovic.weatherqinfo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class City(
    @PrimaryKey val name: String,
    @ColumnInfo val lon: Double,
    @ColumnInfo val lat: Double,
    @ColumnInfo val id: String = UUID.randomUUID().toString()
)

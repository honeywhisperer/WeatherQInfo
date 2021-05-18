package hr.trailovic.weatherqinfo.database

import androidx.room.*
import hr.trailovic.weatherqinfo.model.City
import io.reactivex.Flowable
import io.reactivex.Observable


@Dao
interface CityDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    /*suspend*/ fun addCity(city: City)

    @Delete
    /*suspend*/ fun removeCity(city: City)

    @Query("DELETE FROM city WHERE upper(name) LIKE upper(:cityName)")
    suspend fun removeCityByName(cityName: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    /*suspend*/ fun updateCity(city: City)

    @Query("DELETE FROM city")
    suspend fun removeAll()

    // - GET

    // Rx
    @Query("SELECT * FROM city")
    fun getAllCitiesRx(): Observable<List<City>>



    @Query("SELECT * FROM city")
    suspend fun getAllCitiesList(): List<City>


    @Query("SELECT * FROM city WHERE upper(name) LIKE upper(:cityName)")
    fun getCityByName(cityName: String): City?

}
package hr.trailovic.weatherqinfo.database

import androidx.lifecycle.LiveData
import androidx.room.*
import hr.trailovic.weatherqinfo.model.City
import io.reactivex.Observable


@Dao
interface CityDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCity(city: City) //Rx

    @Delete
    suspend fun removeCity(city: City)

    @Query("DELETE FROM city WHERE upper(name) LIKE upper(:cityName)")
    suspend fun removeCityByName(cityName: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCity(city: City) //Rx

    @Query("DELETE FROM city")
    suspend fun removeAll()

    // - GET

    // Rx
    @Query("SELECT * FROM city")
    fun getAllCitiesRx(): Observable<List<City>>

    @Query("SELECT * FROM city")
    fun getAllCitiesLD(): LiveData<List<City>>


    @Query("SELECT * FROM city")
    suspend fun getAllCitiesList(): List<City>


    @Query("SELECT * FROM city WHERE upper(name) LIKE upper(:cityName)")
    fun getCityByName(cityName: String): City?

}
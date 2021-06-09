package hr.trailovic.weatherqinfo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.model.*
import hr.trailovic.weatherqinfo.repo.WeatherRepository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "wVM:::"

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepo: WeatherRepository) :
    ViewModel() {

    private val disposables = CompositeDisposable()

    //    private var areRxChannelsOpen: Boolean
    /**
     * Rx streams are disabled when data is being removed from local DB
     * (because local DB data is set to be observed all the time by Rx streams)
     * */
    private var areStreamsEnabled: Boolean // disable streams when removing data

    private val citiesCrossReference = mutableMapOf<Coord, String>() // coord:fullName

    private val weatherTodayNeedsUpdate = mutableMapOf<City, Boolean>()
    private val weatherWeekNeedsUpdate = mutableMapOf<City, Boolean>()

    private val messageMLD = MutableLiveData<String>() // message to user
    val messageLD: LiveData<String> = messageMLD

    private val loadingMLD = MutableLiveData<Boolean>() // progress indicator
    val loadingLD: LiveData<Boolean> = loadingMLD

    private val weatherTodayListMLD = MutableLiveData<List<WeatherToday>?>() // weather today
    val weatherTodayListLD: LiveData<List<WeatherToday>?> = weatherTodayListMLD

    private val weatherWeekListListMLD = MutableLiveData<List<List<WeatherWeek>>?>() // weather week
    val weatherWeekListListLD: LiveData<List<List<WeatherWeek>>?> = weatherWeekListListMLD

    private val cityResponseListMLD = MutableLiveData<List<CityResponse>>() // cities
    val cityResponseListLD: LiveData<List<CityResponse>> = cityResponseListMLD

    private val myLocationWeatherTodayMLD = MutableLiveData<WeatherToday>() // my location
    val myLocationWeatherTodayLD: LiveData<WeatherToday> = myLocationWeatherTodayMLD

    /**
     * Open Rx channels and set init values for code control variables
     * */
    init {
        areStreamsEnabled = true
        loadingMLD.value = false

        checkWeatherTodayRX()
        loadWeatherTodayRx()

        checkWeatherWeekRx()
        loadWeatherWeekRx()

        keepCrossReferencesUpdated()
    }

    /*city*/

    /**
     * Remove individual city from local DB, and its related weather data
     * */
    fun removeCityData(city: City) {
        viewModelScope.launch {
            disableRxStreamsAndExecute {
                weatherRepo.removeWeatherWeekByCityNameSuspend(city.fullName)
                weatherRepo.removeWeatherTodayByCityNameSuspend(city.fullName)
                weatherRepo.removeCitySuspend(city)
            }
        }
    }

    /**
     * Clean local DB for cities and weather data
     * */
    fun removeAllData() {
        viewModelScope.launch {
            disableRxStreamsAndExecute {
                weatherRepo.removeAllCitiesSuspend()
                weatherRepo.removeAllWeatherWeekSuspend()
                weatherRepo.removeAllWeatherTodaySuspend()
            }
            Log.d(TAG, "removeAllData: ${Thread.currentThread().name}")
        }
    }

    /**
     * Remove stored weather data while keeping the list of saved locations.
     * App will call APIs and request fresh weather data on next spp start
     * */
    fun removeWeatherDataOnly() {
        viewModelScope.launch {
            disableRxStreamsAndExecute {
                weatherRepo.removeAllWeatherWeekSuspend()
                weatherRepo.removeAllWeatherTodaySuspend()
            }
        }
    }

    /**
     * Blank text will be ignored
     * */
    fun dismissErrorMessage() {
        messageMLD.postValue("")
    }

    /**
     * Load LiveData with Cities data
     * */
    fun getAllCitiesLD(): LiveData<List<City>> {
        return weatherRepo.getAllCitiesLD()
    }

    /**
     * To simplify code and reduce DB calls, keep essential data updated and available
     * */
    private fun keepCrossReferencesUpdated() {
        val d = weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
            .concatMapIterable { it }
            .observeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    val coord = Coord(it.lon, it.lat)
                    citiesCrossReference[coord] = it.fullName
                }
            )
        disposables.add(d)
    }

    /* <<< Add City */

    /**
     * Stream opens once the user provides string to be searched.
     * Stream completes when result or error come up
     * */
    fun checkCityRx(cityName: String) {
        Observable.just(cityName)
            .subscribeOn(Schedulers.io())
            .flatMap {
                weatherRepo.getCitiesListRx(it)
            }
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<List<CityResponse>?> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: checkCityRx")
                    disposables.add(d)
                }

                override fun onNext(t: List<CityResponse>) {
                    cityResponseListMLD.postValue(t)
                }

                override fun onError(e: Throwable) {
                    messageMLD.postValue("Error requesting data for $cityName")
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: checkCityRx")
                }
            })
    }

    /**
     * Save city to local DB: user picks one of the locations that were provided by api (backend)
     * */
    fun saveCity(cityResponse: CityResponse) {
        val city = convertCityResponse(cityResponse)
        city?.let {
            viewModelScope.launch {
                weatherRepo.addCitySuspend(it)
            }
        }
    }

    /**
     * To have blank fields on the next call
     * */
    fun cleanCitySearchData() {
        cityResponseListMLD.postValue(emptyList())
    }
    /* Add City >>> */

    /* <<< Update Weather Today DB and LiveData */
    /**
     * Observe Cities in local DB,
     * check the age of data,
     * call api to update DB if WeatherToday data is not fresh enough
     * */
    private fun checkWeatherTodayRX() {
        weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
            .map {
                if (areStreamsEnabled)
                    it
                else
                    emptyList()
            }
            .flatMap { cityList ->
                var weatherTodayDB: WeatherToday?
                cityList.forEach { city ->
                    weatherTodayDB = weatherRepo.getWeatherTodayForCity(city)
                    weatherTodayNeedsUpdate[city] =
                        weatherTodayDB?.timeTag?.isItOlderThanOneHour() ?: true
                }
                Observable.just(weatherTodayNeedsUpdate)
            }
            .map { map ->
                map.filter { entry ->
                    entry.value
                }
            }
            .concatMapIterable {
                it.keys
            }
            .flatMap { city ->
                weatherRepo.fetchWeatherTodayForCityRx(city)
            }
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<WeatherTodayResponse> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: checkWeatherTodayRX")
                    disposables.add(d)
                }

                override fun onNext(t: WeatherTodayResponse) {
                    Log.d(TAG, "onNext: checkWeatherTodayRX")
                    viewModelScope.launch {
                        val locationDescription =
                            citiesCrossReference[t.coord] ?: "error for ${t.coord}"
                        val weatherToday =
                            convertWeatherTodayApiResponse(locationDescription, t)
                        weatherRepo.addWeatherTodaySuspend(weatherToday)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: checkWeatherTodayRX", e)
                    messageMLD.postValue("error checkWeatherTodayRx")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: checkWeatherTodayRX")
                }
            })
    }

    /**
     * Observe WeatherToday data in local DB and update LiveData accordingly
     * */
    private fun loadWeatherTodayRx() {
        weatherRepo.getAllWeatherTodayRx()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<List<WeatherToday>> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: loadWeatherTodayRx")
                    disposables.add(d)
                }

                override fun onNext(t: List<WeatherToday>) {
                    Log.d(TAG, "onNext: loadWeatherTodayRx ${t.size}")
                    weatherTodayListMLD.postValue(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: loadWeatherTodayRx", e)
                    messageMLD.postValue("error loadWeatherTodayRx")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: loadWeatherTodayRx")
                }
            })
    }
    /* Update Weather Today DB and LiveData >>> */

    /* <<< Update Weather Week DB and LiveData */
    /**
     * Observe Cities in local DB,
     * check the age of data,
     * call api to update DB if WeatherWeek data is not fresh enough
     * */
    private fun checkWeatherWeekRx() {
        weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
            .map {
                if (areStreamsEnabled)
                    it
                else
                    emptyList()
            }
            .flatMap { cityList ->
                cityList.forEach { city ->
                    val weatherWeekDB = weatherRepo.getWeatherWeekForCity(city)
                    val timeTag = weatherWeekDB?.firstOrNull()?.timeTag
                    weatherWeekNeedsUpdate[city] =
                        (timeTag?.isItOlderThan12Hours() ?: true) || (timeTag?.isItToday()?.not() ?: true)
                }
                Observable.just(weatherWeekNeedsUpdate)
            }
            .map { map ->
                map.filter { entry ->
                    entry.value
                }
            }
            .concatMapIterable {
                it.keys
            }
            .flatMap { city ->
                weatherRepo.fetchWeatherWeekForCityRx(city)
            }
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<WeatherWeekResponse> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: checkWeatherWeekRx")
                    disposables.add(d)
                }

                override fun onNext(t: WeatherWeekResponse) {
                    Log.d(TAG, "onNext: checkWeatherWeekRx")
                    viewModelScope.launch {
                        val coord = Coord(t.lon, t.lat)
                        val locationDescription =
                            citiesCrossReference[coord] ?: "error for $coord"
                        val weatherWeek =
                            convertWeatherWeekApiResponse(locationDescription, t)
//                        weatherWeek.forEach {
//                            weatherRepo.addWeatherWeekSuspend(it)
//                        }
                        weatherRepo.addWeatherWeekListSuspend(weatherWeek)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: checkWeatherWeekRx", e)
                    messageMLD.postValue("error checkWeatherWeekRx")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: checkWeatherWeekRx")
                }
            })
    }

    /**
     * Observe WeatherWeek data in local DB and update LiveData accordingly
     * */
    private fun loadWeatherWeekRx() {
        weatherRepo.getAllWeatherWeekRx()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<List<WeatherWeek>> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: loadWeatherWeekRx")
                    disposables.add(d)
                }

                override fun onNext(t: List<WeatherWeek>) {
                    Log.d(TAG, "onNext: loadWeatherWeekRx")
                    //todo
                    val x = t.groupBy {
                        it.locationFullName
                    }
                    val newData = mutableListOf<List<WeatherWeek>>()
                    x.keys.forEach { key ->
                        x[key]?.let { list ->
                            newData.add(list)
                        }
                    }
                    weatherWeekListListMLD.postValue(newData)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: loadWeatherWeekRx", e)
                    messageMLD.postValue("error loadWeatherWeekRx")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: loadWeatherWeekRx")
                }
            })
    }
    /* Update Weather Week DB and LiveData >>> */

    /* <<< MyLocation Weather Info */

    fun fetchWeatherTodayForMyLocation(lon: Double, lat: Double) {
        weatherRepo.fetchWeatherTodayForLocationRxSingle(lon, lat)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : SingleObserver<WeatherTodayResponse> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: myLocation")
                    disposables.add(d)
                }

                override fun onSuccess(t: WeatherTodayResponse) {
                    Log.d(TAG, "onSuccess: myLocation")
                    val country = t.sys.country
                    val place = t.name
                    val description = if (place.isNotBlank() && country.isNotBlank()) {
                        ": $place, $country"
                    } else if (place.isNotBlank()) {
                        ": $place"
                    } else {
                        ""
                    }
                    val locationName = "My location$description"
                    val weatherToday = convertWeatherTodayApiResponse(locationName, t)
                    myLocationWeatherTodayMLD.postValue(weatherToday)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: myLocation", e)
                    messageMLD.postValue(e.message)
                }
            })
    }

    /* MyLocation Weather Info >>> */

    /**
     * This function is used to disable Rx streams when removing data
     * */
    private suspend fun disableRxStreamsAndExecute(block: suspend () -> Unit) {
        try {
            areStreamsEnabled = false
            block()
        } finally {
            areStreamsEnabled = true
        }
    }

    /**
     * Take care of disposables
     * */
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
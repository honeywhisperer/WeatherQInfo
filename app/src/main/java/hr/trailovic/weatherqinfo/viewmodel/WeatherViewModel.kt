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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val TAG = "wVM:::"

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepo: WeatherRepository) :
    ViewModel() {

    private val disposables = CompositeDisposable()

    private var areRxChannelsOpen = false

    private val citiesCrossReference = mutableMapOf<Coord, String>() // coord:fullName

    private val messageMLD = MutableLiveData<String>()
    val messageLD: LiveData<String> = messageMLD

    private val loadingMLD = MutableLiveData<Boolean>(false)
    val loadingLD: LiveData<Boolean> = loadingMLD

    private val weatherTodayListMLD = MutableLiveData<List<WeatherToday>?>()
    val weatherTodayListLD: LiveData<List<WeatherToday>?> = weatherTodayListMLD

    private val weatherWeekListListMLD = MutableLiveData<List<List<WeatherWeek>>?>()
    val weatherWeekListListLD: LiveData<List<List<WeatherWeek>>?> = weatherWeekListListMLD

    private val cityResponseListMLD = MutableLiveData<List<CityResponse>>()
    val cityResponseListLD: LiveData<List<CityResponse>> = cityResponseListMLD

    /*city*/

    fun removeCityData(city: City) {
        val lockA = Mutex()

        viewModelScope.launch(Dispatchers.IO) {
            lockA.withLock {
                weatherRepo.removeWeatherWeekByCityNameSuspend(city.fullName)
            }
            lockA.withLock {
                weatherRepo.removeWeatherTodayByCityNameSuspend(city.fullName)
            }
            lockA.withLock {
                weatherRepo.removeCitySuspend(city)
            }
        }
    }

    fun removeAllData() {
        val lockA = Mutex()
        viewModelScope.launch {
            lockA.withLock {
                weatherRepo.removeAllWeatherWeekSuspend()
            }
            lockA.withLock {
                weatherRepo.removeAllWeatherTodaySuspend()
            }
            lockA.withLock {
                weatherRepo.removeAllCitiesSuspend()
            }
            Log.d(TAG, "removeAllData: ${Thread.currentThread().name}")
        }
    }

    fun removeWeatherDataOnly(){
        val lockA = Mutex()

        viewModelScope.launch(Dispatchers.IO) {
            lockA.withLock {
                weatherRepo.removeAllWeatherWeekSuspend()
            }
            lockA.withLock {
                weatherRepo.removeAllWeatherTodaySuspend()
            }
        }
    }

    fun dismissErrorMessage() {
        messageMLD.postValue("")
    }

    fun getAllCitiesLD(): LiveData<List<City>> {
        return weatherRepo.getAllCitiesLD()
    }

    fun openRxChannels() {
        if (areRxChannelsOpen.not()) {
            checkWeatherTodayRX()
            loadWeatherTodayRx()

            checkWeatherWeekRx()
            loadWeatherWeekRx()

            keepCrossReferencesUpdated()

            areRxChannelsOpen = true
        }
    }

    /*Rx*/


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

    fun saveCity(cityResponse: CityResponse) {
        val city = convertCityResponse(cityResponse)
        city?.let {
            viewModelScope.launch {
                weatherRepo.addCitySuspend(it)
            }
        }
    }

    fun cleanCitySearchData() {
        cityResponseListMLD.postValue(emptyList())
    }
    /* Add City >>> */

    /* <<< Update Weather Today DB and LiveData */
    private val weatherTodayNeedsUpdate = mutableMapOf<City, Boolean>()

    private fun checkWeatherTodayRX() {
        weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
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
                    viewModelScope.launch(Dispatchers.IO) {
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
    private val weatherWeekNeedsUpdate = mutableMapOf<City, Boolean>()

    private fun checkWeatherWeekRx() {
        weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
            .flatMap { cityList ->
                cityList.forEach { city ->
                    val weatherWeekDB = weatherRepo.getWeatherWeekForCity(city)
                    weatherWeekNeedsUpdate[city] =
                        weatherWeekDB?.firstOrNull()?.timeTag?.isItOlderThan12Hours() ?: true
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
                    viewModelScope.launch(Dispatchers.IO) {
                        val coord = Coord(t.lon, t.lat)
                        val locationDescription =
                            citiesCrossReference[coord] ?: "error for $coord"
                        val weatherWeek =
                            convertWeatherWeekApiResponse(locationDescription, t)
                        weatherWeek.forEach {
                            weatherRepo.addWeatherWeekSuspend(it)
                        }
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

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
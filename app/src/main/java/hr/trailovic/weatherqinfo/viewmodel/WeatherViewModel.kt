package hr.trailovic.weatherqinfo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.trailovic.weatherqinfo.capitalizeEveryWord
import hr.trailovic.weatherqinfo.convertWeatherTodayApiResponse
import hr.trailovic.weatherqinfo.convertWeatherWeekApiResponse
import hr.trailovic.weatherqinfo.model.City
import hr.trailovic.weatherqinfo.model.WeatherToday
import hr.trailovic.weatherqinfo.model.WeatherWeek
import hr.trailovic.weatherqinfo.repo.WeatherRepository
import io.reactivex.Observable
import io.reactivex.Observer
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

    private val messageMLD = MutableLiveData<String>()
    public val messageLD: LiveData<String> = messageMLD

    private val loadingMLD = MutableLiveData<Boolean>(false)
    public val loadingLD: LiveData<Boolean> = loadingMLD

    private val weatherTodayListMLD = MutableLiveData<List<WeatherToday>?>()
    public val weatherTodayListLD: LiveData<List<WeatherToday>?> = weatherTodayListMLD

    private val weatherWeekListMLD = MutableLiveData<List<WeatherWeek>?>()
    public val weatherWeekListLD: LiveData<List<WeatherWeek>?> = weatherWeekListMLD

    private val weatherWeekListListMLD = MutableLiveData<List<List<WeatherWeek>>?>()
    public val weatherWeekListListLD: LiveData<List<List<WeatherWeek>>?> = weatherWeekListListMLD

    /*city*/

    fun removeCityData(city: City) {
        viewModelScope.launch {
            weatherRepo.removeCity(city)
            //remove weather today by city
            //remove weather week by city
        }
    }

    fun removeAllData() {
        viewModelScope.launch {
            weatherRepo.removeAllCities()
            weatherRepo.removeAllWeatherToday()
            weatherRepo.removeAllWeatherWeek()
            Log.d(TAG, "removeAllData: ${Thread.currentThread().name}")
        }
    }

    fun checkAndAddCity(userInput: String) {
        val cityName = userInput.capitalizeEveryWord()
        val d = Observable.just(cityName)
            .subscribeOn(Schedulers.io())
            .flatMap {
                weatherRepo.fetchCoordinatesForCity(it)
            }
            .subscribeBy(
                onError = { messageMLD.postValue("Error while fetching data $cityName") },
                onNext = { weatherRepo.addCity(City(cityName, it.coord.lon, it.coord.lat)) }
            )
        disposables.add(d)
    }


    /*weather today*/

    fun fetchWeatherToday() {
        Log.d(TAG, "fetchWeatherToday: start")

        weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
            .map {
                weatherTodayListMLD.postValue(null)
                it
            }
            .concatMapIterable { it ->
                Log.d(TAG, "fetchWeatherToday: concatMapIterable: ${it.size}")
                it
            }
            .flatMap { city ->
                weatherRepo.fetchWeatherTodayForCity(city)
                    .also {
                        Log.d(TAG, "fetchWeatherToday: ${city.name}")
                    }
                    .map {
                        convertWeatherTodayApiResponse(city.name, it)
                    }
            }
            .subscribe(object : Observer<WeatherToday> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                    Log.d(TAG, "onSubscribe: ")
                }

                override fun onNext(t: WeatherToday) {
                    weatherRepo.addWeatherToday(t)
                    val weatherValue = weatherTodayListMLD.value?.toMutableList()
                        ?: emptyList<WeatherToday>().toMutableList()
                    weatherValue.add(t)
                    weatherTodayListMLD.postValue(weatherValue)
                    Log.d(TAG, "onNext: ${t.city}")
                }

                override fun onError(e: Throwable) {
                    messageMLD.postValue("Error fetching/storing weather")
                    Log.e(TAG, "fetchWeatherToday: onError: ${e.message}", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "fetchWeatherToday: onComplete")
                }
            })
    }

    fun fetchWeatherWeek() {
        Log.d(TAG, "fetchWeatherWeek: start")

        weatherRepo.getAllCitiesRx()
            .subscribeOn(Schedulers.io())
            .map {
                weatherWeekListMLD.postValue(null)
                weatherWeekListListMLD.postValue(null)
                it
            }
            .concatMapIterable { it ->
                Log.d(TAG, "fetchWeatherWeek: concatMapIterable: ${it.size}")
                it
            }
            .flatMap { city ->
                weatherRepo.fetchWeatherWeekForCity(city)
                    .also {
                        Log.d(TAG, "fetchWeatherWeek: ${city.name}")
                    }
                    .map {
                        convertWeatherWeekApiResponse(city.name, it)
                    }
            }
            .subscribe(object : Observer<List<WeatherWeek>> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                    Log.d(TAG, "onSubscribe: ")
                }

                override fun onNext(t: List<WeatherWeek>) {
                    t.forEach {
                        weatherRepo.addWeatherWeek(it)
                    }

                    val weatherValue = weatherWeekListMLD.value?.toMutableList()
                        ?: emptyList<WeatherWeek>().toMutableList()
                    weatherValue.addAll(t)
                    weatherWeekListMLD.postValue(weatherValue)

                    val weatherListValue = weatherWeekListListMLD.value?.toMutableList() ?: emptyList<List<WeatherWeek>>().toMutableList()
                    weatherListValue.add(t)
                    weatherWeekListListMLD.postValue(weatherListValue)
                    Log.d(TAG, "onNext: ${t[0].location}")
                }

                override fun onError(e: Throwable) {
                    messageMLD.postValue("Error fetching/storing weather week")
                    Log.e(TAG, "fetchWeatherWeek: onError: ${e.message}", e)
                }

                override fun onComplete() {
                    Log.d(TAG, "fetchWeatherWeek: onComplete")
                }
            })
    }

    fun loadWeatherWeek() {

    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
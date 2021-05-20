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
import hr.trailovic.weatherqinfo.model.*
import hr.trailovic.weatherqinfo.repo.WeatherRepository
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
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

    private var newCityName = ""
    private val cityObservable: Subject<String> = PublishSubject.create()
    private val cityObservableSubscribed = cityObservable.toSerialized().subscribeOn(Schedulers.io())

    /*city*/

    fun removeCityData(city: City) {
        viewModelScope.launch {
            weatherRepo.removeCity(city)
            /*Weather Today and Weather week data will be re-fetched
             after city DB status is changed*/
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

    fun addCity(userInput: String) {
        newCityName = userInput.capitalizeEveryWord()
        cityObservable.onNext(newCityName)
    }

    fun getAllCitiesLD(): LiveData<List<City>> {
        return weatherRepo.getAllCitiesLD()
    }

    fun openRxChannels() {
        checkAndAddCity()
        fetchWeatherToday()
        fetchWeatherWeek()
    }

    /*Rx*/

    private fun checkAndAddCity() {
        Log.d(TAG, "checkAndAddCity: Started")

        cityObservableSubscribed
            .observeOn(Schedulers.io())
            .map {
                Log.d(TAG, "checkAndAddCity: map: |${it}|")
                Log.d(TAG, "checkAndAddCity: Map running on ${Thread.currentThread().name}")
                it
            }
            .map {
                Log.d(TAG, "checkAndAddCity: FlatMap running on ${Thread.currentThread().name}")
                WeatherTodayResponseWrapper(weatherRepo.fetchCoordinatesForCity(it))
            }
            .subscribe(object : Observer<WeatherTodayResponseWrapper> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                    Log.d(TAG, "checkAndAddCity: onSubscribe ${Thread.currentThread().name}")
                }

                override fun onNext(t: WeatherTodayResponseWrapper) {
                    if (t.weatherTodayResponse != null && t.weatherTodayResponse.cod == 200) {
                        val resp = t.weatherTodayResponse.coord
                        weatherRepo.addCity(City(newCityName, resp.lon, resp.lat))
                    } else {
                        messageMLD.postValue("Error while fetching data $newCityName")
                    }
                }

                override fun onError(e: Throwable) {
                    messageMLD.postValue("Error while fetching data $newCityName ... restart app")
                }

                override fun onComplete() {
                    Log.d(
                        TAG,
                        "checkAndAddCity: onComplete called on ${Thread.currentThread().name}"
                    )
                }
            })
    }

    /*weather today*/

    private fun fetchWeatherToday() {
        Log.d(TAG, "fetchWeatherToday: Started")

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

    private fun fetchWeatherWeek() {
        Log.d(TAG, "fetchWeatherWeek: Started")

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

                    val weatherListValue = weatherWeekListListMLD.value?.toMutableList()
                        ?: emptyList<List<WeatherWeek>>().toMutableList()
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

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
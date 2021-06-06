package hr.trailovic.weatherqinfo

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.SystemClock
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import hr.trailovic.weatherqinfo.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

fun displayFragment(fragmentActivity: FragmentActivity, fragment: Fragment) {
    fragmentActivity
        .supportFragmentManager
        .beginTransaction()
        .replace(R.id.mainFrame, fragment)
        .commit()
}

fun showDialog(
    context: Context,
    title: String,
    negativeButtonText: String,
    positiveButtonText: String,
    positiveButtonAction: () -> Unit
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setNegativeButton(negativeButtonText, null)
        .setPositiveButton(positiveButtonText) { _1, _2 ->
            positiveButtonAction()
        }
        .show()
}

/*Date and Time*/

/**
 * 20/05/2020 14:25:05
 *
 * intended to be used on parameter that represents seconds (not millis)
 **/
fun Long.toDateTimeString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date(this + timezoneOffset)
    return format.format(date)
}

/**
 * 20/05/2021
 *
 * intended to be used on parameter that represents seconds (not millis)
 **/
fun Long.toDateString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date(this + timezoneOffset)
    return format.format(date)
}

/**
 * Thursday, May 20
 *
 * intended to be used on parameter that represents seconds (not millis)
 **/
fun Long.toLongDateNameString(timezoneOffset: Int = 0): String {
//    val format = SimpleDateFormat("EEE, MMM d, ''yy", Locale.ROOT)
    val format = SimpleDateFormat("EEEE, MMM d", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date(this + timezoneOffset)
    return format.format(date)
}

/**
 * 20/05
 *
 * Thu
 *
 * intended to be used on parameter that represents seconds (not millis)
 **/
fun Long.toDateNameString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("dd/MM\nEEE", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date(this + timezoneOffset)
    return format.format(date)
}

/**
 * 14:25
 *
 * intended to be used on parameter that represents seconds (not millis)
 **/
fun Long.toTimeString(timezoneOffset: Int = 0): String {
//    val format = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
    val format = SimpleDateFormat("HH:mm", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // *** UTC or GMT? any difference?
    val date = Date(this + timezoneOffset)
    return format.format(date)
}

fun Long.isItOlderThanOneHour(): Boolean {
    val now = System.currentTimeMillis()
    return (now - this) > (60 * 60 * 1000)
}

fun Long.isItOlderThan12Hours(): Boolean {
    val now = System.currentTimeMillis()
    return (now - this) > (12 * 60 * 60 * 1000)
}

/*String formatting*/

fun Double.oneDecimal(): String {
    return String.format("%.1f", this)
}

fun String.capitalizeEveryWord(): String {
    val words = this.lowercase().split(" ")
    return words.joinToString(" ") {
        it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

fun String.fixUserInput(): String {
    val removedMultiWhitespaces = this.replace("\\s{1,}".toRegex(), " ")
    val stringList = removedMultiWhitespaces.split(",")
    val resultList = mutableListOf<String>()
    stringList.forEach {
        resultList.add(it.trim())
    }
    resultList[0] = resultList[0].capitalizeEveryWord()
    return resultList.joinToString(",")
}


/*String add units*/

/**
 * Use this to add unit at the end
 **/
fun String.temperature(): String {
    return "$this°C"
}

/**
 * Use this to add unit at the end
 **/
fun String.pressure(): String {
    return "$this hPa"
}

/**
 * Use this to add unit at the end
 **/
fun String.humidity(): String {
    return "$this%"
}

/**
 * Use this to add unit at the end
 **/
fun String.windSpeed(): String {
    return "${this}m/s"
}

/**
 * Use this to add unit at the end
 **/
fun String.rain(): String {
    return "${this}mm"
}

/**
 * Use this to create a text related to difference in measured temperature and real feel
 **/
fun Double.generateFeelsLikeTemperatureText(realTemperature: Double): String {
    return if (abs(this - realTemperature) < 0.6)
        "and feels the same"
    else
        "feels like ${this.oneDecimal().temperature()}"
}

/*Converters*/
//System.currentTimeMillis()
fun convertWeatherTodayApiResponse(
    cityName: String,
    weatherTodayResponse: WeatherTodayResponse
): WeatherToday =
    WeatherToday(
//        weatherTodayResponse.cod,
        cityName,
//        weatherTodayResponse.sys.country,
        (weatherTodayResponse.sys.sunrise + weatherTodayResponse.timezone) * 1000,
        (weatherTodayResponse.sys.sunset + weatherTodayResponse.timezone) * 1000,
        weatherTodayResponse.main.temp,
        weatherTodayResponse.main.feels_like,
        weatherTodayResponse.main.temp_min,
        weatherTodayResponse.main.temp_max,
        weatherTodayResponse.main.pressure,
        weatherTodayResponse.main.humidity,
        weatherTodayResponse.weather[0].main,
        weatherTodayResponse.weather[0].description,
        weatherTodayResponse.weather[0].icon,
    )

fun convertWeatherWeekApiResponse(
    location: String,
    weatherWeekResponse: WeatherWeekResponse
): List<WeatherWeek> {
    val listOfConverted = mutableListOf<WeatherWeek>()
    for (day in weatherWeekResponse.daily) {
        val newDay = WeatherWeek(
            location,
            (day.sunrise + weatherWeekResponse.timezoneOffset) * 1000,
            (day.sunset + weatherWeekResponse.timezoneOffset) * 1000,
            day.temp.day,
            day.temp.min,
            day.temp.max,
            day.weather[0].description,
            day.weather[0].icon,
            day.pressure,
            day.humidity,
            day.windSpeed,
            day.rain,
            day.uvi
        )
        listOfConverted.add(newDay)
    }
    return listOfConverted
}

fun convertCityResponse(cityResponse: CityResponse): City? {
    var newCity: City? = null
    with(cityResponse) {
        if (name != null && country != null && lat != null && lon != null) {
            newCity = City(name, country, state, lon, lat)
        }
    }
    return newCity
}

/*Weather API response*/

fun String.toWeatherIconUrl() =
    BuildConfig.WEATHER_IMG_PREFIX + this + BuildConfig.WEATHER_IMG_SUFIX

/*Dialog Fragment*/

fun DialogFragment.setFullScreen() {
    dialog?.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}

fun DialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun someTest() {
    val x = IntArray(10) { it * it }
}
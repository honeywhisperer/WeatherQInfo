package hr.trailovic.weatherqinfo

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import hr.trailovic.weatherqinfo.model.WeatherToday
import hr.trailovic.weatherqinfo.model.WeatherTodayResponse
import hr.trailovic.weatherqinfo.model.WeatherWeek
import hr.trailovic.weatherqinfo.model.WeatherWeekResponse
import java.text.SimpleDateFormat
import java.util.*

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

/*
*  intended to be used on parameter that represents seconds (not millis)
* */
fun Long.toDateTimeString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date((this + timezoneOffset) * 1000)
    return format.format(date)
}

/*
*  intended to be used on parameter that represents seconds (not millis)
* */
fun Long.toDateString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date((this + timezoneOffset) * 1000)
    return format.format(date)
}

/*
*  intended to be used on parameter that represents seconds (not millis)
* */
fun Long.toShortDateString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("dd/MM", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // ***
    val date = Date((this + timezoneOffset) * 1)
    return format.format(date)
}

/*
*  intended to be used on parameter that represents seconds (not millis)
* */
fun Long.toTimeString(timezoneOffset: Int = 0): String {
    val format = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC") // *** UTC or GMT? any difference?
    val date = Date((this + timezoneOffset) * 1000)
    return format.format(date)
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

fun convertWeatherTodayApiResponse(
    cityName: String,
    weatherTodayResponse: WeatherTodayResponse
): WeatherToday =
    WeatherToday(
        weatherTodayResponse.cod,
        cityName,
        weatherTodayResponse.sys.country,
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
//        weatherTodayResponse.coord.lon,
//        weatherTodayResponse.coord.lat,
//        System.currentTimeMillis(),
//        (weatherTodayResponse.dt + weatherTodayResponse.timezone) * 1000
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

/*Dialog Fragment*/

fun String.toWeatherIconUrl() =
    BuildConfig.WEATHER_IMG_PREFIX + this + BuildConfig.WEATHER_IMG_SUFIX

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
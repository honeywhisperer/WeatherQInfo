package hr.trailovic.weatherqinfo.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentDetailsWeekBinding
import hr.trailovic.weatherqinfo.model.WeatherWeek
import javax.inject.Inject

@AndroidEntryPoint
class DetailsWeekFragment : BaseDialogFragment<FragmentDetailsWeekBinding>() {

    private var weatherWeekData: WeatherWeek? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weatherWeekData = it.getParcelable(ARG_PAR)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsWeekBinding {
        return FragmentDetailsWeekBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setFullScreen()
        setInfo()
        setListeners()
    }

    private fun setListeners() {
        binding.root.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setInfo() {
        weatherWeekData?.let { weatherWeek ->
            with(binding) {
                tvLocation.text = weatherWeek.locationFullName
                tvDate.text = weatherWeek.sunrise.toLongDateNameString()
                ("Sunrise " + weatherWeek.sunrise.toTimeString()).also { tvSunrise.text = it }
                ("Sunset " + weatherWeek.sunset.toTimeString()).also { tvSunset.text = it }
                ("Temperature " + weatherWeek.tempDay.oneDecimal()
                    .temperature()).also { tvTempDay.text = it }
                ("Min " + weatherWeek.tempMin.oneDecimal().temperature()).also {
                    tvTempMin.text = it
                }
                ("Max " + weatherWeek.tempMax.oneDecimal().temperature()).also {
                    tvTempMax.text = it
                }
                tvWeatherDescription.text = weatherWeek.weatherDescription
                ("Pressure " + weatherWeek.pressure.toString().pressure()).also {
                    tvPressure.text = it
                }
                ("Humidity " + weatherWeek.humidity.toString().humidity()).also {
                    tvHumidity.text = it
                }
                ("Wind speed " + weatherWeek.windSpeed.oneDecimal()
                    .windSpeed()).also { tvWindSpeed.text = it }
                ("Rain " + weatherWeek.rain.oneDecimal().rain()).also { tvRain.text = it }
                ("UVI index " + weatherWeek.uvi.oneDecimal()).also { tvUvi.text = it }

                glide
                    .load(weatherWeek.weatherIcon.toWeatherIconUrl())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivIcon)
            }
        }
    }

    companion object {
        private const val ARG_PAR = "weather week data"

        @JvmStatic
        fun newInstance(weatherWeek: WeatherWeek) =
            DetailsWeekFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAR, weatherWeek)
                }
            }
    }
}
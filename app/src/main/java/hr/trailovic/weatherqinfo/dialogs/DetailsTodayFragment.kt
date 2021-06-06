package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentDetailsTodayBinding
import hr.trailovic.weatherqinfo.model.WeatherToday
import javax.inject.Inject

@AndroidEntryPoint
class DetailsTodayFragment : BaseDialogFragment<FragmentDetailsTodayBinding>() {

    private var weatherTodayData: WeatherToday? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weatherTodayData = it.getParcelable(ARG_PAR)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsTodayBinding {
        return FragmentDetailsTodayBinding.inflate(inflater, container, false)
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
        weatherTodayData?.let {weatherToday->
            with(binding) {
                tvCity.text = weatherToday.locationFullName
                tvDate.text = weatherToday.sunrise.toLongDateNameString()
                "Sunrise ${weatherToday.sunrise.toTimeString()}".also { tvSunrise.text = it }
                "Sunset ${weatherToday.sunset.toTimeString()}".also { tvSunset.text = it }
                ("Temperature " + weatherToday.temp.oneDecimal().temperature()).also { tvTemperature.text = it }
                tvFeelsLike.text = weatherToday.feels_like.generateFeelsLikeTemperatureText(weatherToday.temp)
                ("Min " + weatherToday.temp_min.oneDecimal().temperature()).also { tvTempMin.text = it }
                ("Max " + weatherToday.temp_max.oneDecimal().temperature()).also { tvTempMax.text = it }
                ("Pressure " + weatherToday.pressure.toString().pressure()).also { tvPressure.text = it }
                ("Humidity " + weatherToday.humidity.toString().humidity()).also { tvHumidity.text = it }
                tvDescription.text = weatherToday.description
                glide
                    .load(weatherToday.icon.toWeatherIconUrl())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivIcon)
            }
        }
    }

    companion object {

        private const val ARG_PAR = "weather today data"
        @JvmStatic
        fun newInstance(weatherToday: WeatherToday) = DetailsTodayFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PAR, weatherToday)
            }
        }
    }
}
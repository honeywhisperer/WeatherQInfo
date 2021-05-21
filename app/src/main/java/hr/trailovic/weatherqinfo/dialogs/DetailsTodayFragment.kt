package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.databinding.FragmentDetailsTodayBinding
import hr.trailovic.weatherqinfo.model.WeatherToday
import javax.inject.Inject

@AndroidEntryPoint
class DetailsTodayFragment : DialogFragment() {

    private var _binding: FragmentDetailsTodayBinding? = null
    private val binding get() = _binding!!

    private var weatherTodayData: WeatherToday? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weatherTodayData = it.getParcelable(ARG_PAR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                tvCity.text = weatherToday.cityFullName
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
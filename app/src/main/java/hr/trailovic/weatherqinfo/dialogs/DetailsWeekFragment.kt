package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.databinding.FragmentDetailsWeekBinding
import hr.trailovic.weatherqinfo.model.WeatherWeek
import javax.inject.Inject

@AndroidEntryPoint
class DetailsWeekFragment : DialogFragment() {

    private var _binding: FragmentDetailsWeekBinding? = null
    private val binding get() = _binding!!

    private var weatherWeekData: WeatherWeek? = null

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weatherWeekData = it.getParcelable(ARG_PAR)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsWeekBinding.inflate(inflater, container, false)
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
        weatherWeekData?.let { weatherWeek ->
            with(binding) {
                tvLocation.text = weatherWeek.location
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
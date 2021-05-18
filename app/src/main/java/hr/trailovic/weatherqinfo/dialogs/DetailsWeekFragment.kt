package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.RequestManager
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
    }

    private fun setInfo() {
        weatherWeekData?.let {weatherWeek ->
            with(binding){
                tvLocation.text = weatherWeek.location
                tvSunrise.text = weatherWeek.sunrise.toTimeString()
                tvSunset.text = weatherWeek.sunset.toTimeString()
                tvTempDay.text = weatherWeek.tempDay.oneDecimal()
                tvTempMin.text = weatherWeek.tempMin.oneDecimal()
                tvTempMax.text = weatherWeek.tempMax.oneDecimal()
                tvWeatherDescription.text = weatherWeek.weatherDescription
                tvPressure.text = weatherWeek.pressure.toString()
                tvHumidity.text = weatherWeek.humidity.toString()
                tvWindSpeed.text = weatherWeek.windSpeed.oneDecimal()
                tvRain.text = weatherWeek.rain.oneDecimal()
                tvUvi.text = weatherWeek.uvi.oneDecimal()

                glide
                    .load(weatherWeek.weatherIcon.toWeatherIconUrl())
                    .fitCenter()
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
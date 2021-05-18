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
    }

    private fun setInfo() {
        weatherTodayData?.let {weatherToday->
            with(binding) {
                tvCity.text = weatherToday.city
                tvSunrise.text = weatherToday.sunrise.toTimeString()
                tvSunset.text = weatherToday.sunset.toTimeString()
                tvTemperature.text = weatherToday.temp.oneDecimal()
                tvFeelsLike.text = weatherToday.feels_like.oneDecimal()
                tvTempMin.text = weatherToday.temp_min.oneDecimal()
                tvTempMax.text = weatherToday.temp_max.oneDecimal()
                tvPressure.text = weatherToday.pressure.toString()
                tvHumidity.text = weatherToday.humidity.toString()
                tvDescription.text = weatherToday.description
                glide
                    .load(weatherToday.icon.toWeatherIconUrl())
                    .fitCenter()
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
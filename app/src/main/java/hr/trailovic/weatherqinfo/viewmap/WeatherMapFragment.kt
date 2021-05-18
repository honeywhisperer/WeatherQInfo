package hr.trailovic.weatherqinfo.viewmap

import android.view.LayoutInflater
import android.view.ViewGroup
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherMapBinding

class WeatherMapFragment : BaseFragment<FragmentWeatherMapBinding>() {
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeatherMapBinding {
        return FragmentWeatherMapBinding.inflate(inflater, container, false)
    }

    override fun setup() {

    }

    companion object{
        fun newInstance() = WeatherMapFragment()
    }
}
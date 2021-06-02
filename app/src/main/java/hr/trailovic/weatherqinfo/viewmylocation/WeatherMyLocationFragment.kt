package hr.trailovic.weatherqinfo.viewmylocation

import android.view.LayoutInflater
import android.view.ViewGroup
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherMyLocationBinding

class WeatherMyLocationFragment : BaseFragment<FragmentWeatherMyLocationBinding>() {
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeatherMyLocationBinding {
        return FragmentWeatherMyLocationBinding.inflate(inflater, container, false)
    }

    override fun setup() {

    }

    companion object{
        fun newInstance() = WeatherMyLocationFragment()
    }
}
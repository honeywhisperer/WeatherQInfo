package hr.trailovic.weatherqinfo.viewmap

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
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
        setWebView()
    }

    private fun setWebView() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.loadUrl("https://openweathermap.org/weathermap?basemap=map&cities=false&layer=windspeed&lat=45.81314&lon=15.9775&zoom=1")
    }

    companion object{
        fun newInstance() = WeatherMapFragment()
    }
}
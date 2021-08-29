package hr.trailovic.weatherqinfo.view.viewmap

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherMapBinding
import hr.trailovic.weatherqinfo.model.CustomWebViewClient

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

        /** Disable cookies */
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(false)
        cookieManager.setAcceptThirdPartyCookies(binding.webView, false)

//        binding.webView.webViewClient = WebViewClient() // old
        /** Open links in the browser (another app) */
        binding.webView.webViewClient = CustomWebViewClient(requireContext(), BASE_URL) // new

        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.loadUrl(BASE_URL)
    }

    companion object{
        private const val BASE_URL = "https://openweathermap.org/weathermap?basemap=map&cities=false&layer=windspeed&lat=45.81314&lon=15.9775&zoom=1"
        fun newInstance() = WeatherMapFragment()
    }
}
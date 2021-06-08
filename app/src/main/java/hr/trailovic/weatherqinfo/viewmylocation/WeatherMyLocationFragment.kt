package hr.trailovic.weatherqinfo.viewmylocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherMyLocationBinding
import hr.trailovic.weatherqinfo.dialogs.DetailsTodayFragment
import hr.trailovic.weatherqinfo.oneDecimal
import hr.trailovic.weatherqinfo.showDialog
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel

private const val TAG = "mlF:::"
private const val LOCATION_PERMISSION_1 = Manifest.permission.ACCESS_COARSE_LOCATION
private const val LOCATION_PERMISSION_2 = Manifest.permission.ACCESS_FINE_LOCATION
private const val LOCATION_REQUEST_CODE = 199

class WeatherMyLocationFragment : BaseFragment<FragmentWeatherMyLocationBinding>() {

    private lateinit var locationManager: LocationManager
    private val viewModel: WeatherViewModel by activityViewModels()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeatherMyLocationBinding {
        return FragmentWeatherMyLocationBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        Log.d(TAG, "setup: ")
        setLocationManager()
        bind()
        setListeners()
    }

    private fun bind() {
        viewModel.myLocationWeatherTodayLD.observe(viewLifecycleOwner){
            DetailsTodayFragment.newInstance(it).show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun setLocationManager() {
        Log.d(TAG, "setLocationManager: ")
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    private fun setListeners() {
        Log.d(TAG, "setListeners: ")
        binding.btnGetInfo.setOnClickListener {
            if (arePermissionsGranted(arrayOf(LOCATION_PERMISSION_1, LOCATION_PERMISSION_2))) {
                val x = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                x?.let {
                    showInfo("Lat: ${it.latitude.oneDecimal()}\nLon: ${it.longitude.oneDecimal()}")
                    viewModel.fetchWeatherTodayForMyLocation(x.longitude, x.latitude)
                } ?: showInfo("Error with location reading")
            } else {
                askForPermissions(arrayOf(LOCATION_PERMISSION_1, LOCATION_PERMISSION_2))
            }
        }
    }

    private fun askForPermissions(permissions: Array<String>) {
        Log.d(TAG, "askForPermissions: ${permissions.toList()}")
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            LOCATION_REQUEST_CODE
        )
    }

    private fun showInfo(message: String) {
        binding.tvInfo.text = message
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        Log.d(TAG, "arePermissionsGranted: ${permissions.toList()}")
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    it
                )
                != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }

    companion object {
        fun newInstance() = WeatherMyLocationFragment()
    }
}
package hr.trailovic.weatherqinfo.viewtoday

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.annotation.GlideModule
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherTodayBinding
import hr.trailovic.weatherqinfo.dialogs.DetailsTodayFragment
import hr.trailovic.weatherqinfo.model.WeatherToday
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel
import javax.inject.Inject

private const val TAG = "wtF:::"

@AndroidEntryPoint
class WeatherTodayFragment : BaseFragment<FragmentWeatherTodayBinding>() {

    private val viewModel: WeatherViewModel by activityViewModels()

//    @Inject
//    lateinit var glideRequestManager: RequestManager

    @Inject
    lateinit var weatherTodayAdapter: WeatherTodayAdapter

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeatherTodayBinding {
        return FragmentWeatherTodayBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        Log.d(TAG, "setup: Start")
        setWeatherTodayRV()
        bind()
//        start()
    }

//    private fun start() {
//        viewModel.fetchWeatherToday()
//    }

    private fun bind() {
        viewModel.weatherTodayListLD.observe(viewLifecycleOwner, { weatherList ->
            weatherList?.let {
                weatherTodayAdapter.setItems(it)
                Log.d(TAG, "bind: setSomeItems")
            }
                ?: run {
                    weatherTodayAdapter.setItems(emptyList())
                    Log.d(TAG, "bind: setEmptyList")
                }
        })


        /*
        viewModel.weatherTodayListLD.observe(viewLifecycleOwner){
            var weatherText = " - - - "
            it?.forEach {
               weatherText = it.toString() + "\n\n" + weatherText
            }
            binding.tvWeatherToday.text = weatherText
        }
        */
    }

    private fun setWeatherTodayRV() {
        weatherTodayAdapter.listener = object : OnWeatherTodayItemInteraction{
            override fun showDetails(weatherToday: WeatherToday) {
                DetailsTodayFragment.newInstance(weatherToday).show(requireActivity().supportFragmentManager, null)
            }
        }
        with(binding.rvWeatherToday) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weatherTodayAdapter
        }
    }


    companion object {
        fun newInstance() = WeatherTodayFragment()
    }
}
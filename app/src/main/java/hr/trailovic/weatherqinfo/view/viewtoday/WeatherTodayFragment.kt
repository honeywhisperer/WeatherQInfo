package hr.trailovic.weatherqinfo.view.viewtoday

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherTodayBinding
import hr.trailovic.weatherqinfo.view.dialogs.DetailsTodayFragment
import hr.trailovic.weatherqinfo.model.WeatherToday
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel
import javax.inject.Inject

private const val TAG = "wtF:::"

@AndroidEntryPoint
class WeatherTodayFragment : BaseFragment<FragmentWeatherTodayBinding>() {

    private val viewModel: WeatherViewModel by activityViewModels()

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
    }

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
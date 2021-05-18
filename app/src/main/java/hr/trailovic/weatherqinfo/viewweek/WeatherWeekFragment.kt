package hr.trailovic.weatherqinfo.viewweek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherWeekBinding
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel


@AndroidEntryPoint
class WeatherWeekFragment : BaseFragment<FragmentWeatherWeekBinding>() {

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeatherWeekBinding {
        return FragmentWeatherWeekBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        bind()
    }

    private fun bind() {
        viewModel.weatherWeekListListLD.observe(viewLifecycleOwner){ listList ->
            var weatherText = " - - - \n\n"
            listList?.forEach {list->
                weatherText += " * * * ${list[0].location} * * * \n\n"
                list.forEach {
                    weatherText += it.toString() + "\n\n"
                }
            }
            binding.tvWeatherWeek.text = weatherText
        }
    }

    companion object{
        fun newInstance() = WeatherWeekFragment()
    }
}
package hr.trailovic.weatherqinfo.viewweek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherWeekBinding
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel
import javax.inject.Inject

private const val TAG = "wwF:::"

@AndroidEntryPoint
class WeatherWeekFragment : BaseFragment<FragmentWeatherWeekBinding>() {

    private val viewModel: WeatherViewModel by activityViewModels()

    @Inject
    lateinit var outerAdapter: WeatherWeekOuterAdapter

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWeatherWeekBinding {
        return FragmentWeatherWeekBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setWeatherWeekRV()
        bind()
    }

    private fun setWeatherWeekRV() {
        with(binding.rvWeatherWeek){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = outerAdapter
        }
    }

    private fun bind() {
//        viewModel.weatherWeekListListLD.observe(viewLifecycleOwner){ listList ->
//            var weatherText = " - - - \n\n"
//            listList?.forEach {list->
//                weatherText += " * * * ${list[0].location} * * * \n\n"
//                list.forEach {
//                    weatherText += it.toString() + "\n\n"
//                }
//            }
//            binding.tvWeatherWeek.text = weatherText
//        }
        viewModel.weatherWeekListListLD.observe(viewLifecycleOwner){listList->
            listList?.let { outerAdapter.setItems(it) } ?: outerAdapter.setItems(emptyList())
        }
    }

    companion object{
        fun newInstance() = WeatherWeekFragment()
    }
}
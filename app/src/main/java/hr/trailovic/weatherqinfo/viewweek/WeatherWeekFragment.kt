package hr.trailovic.weatherqinfo.viewweek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentWeatherWeekBinding
import hr.trailovic.weatherqinfo.dialogs.DetailsWeekFragment
import hr.trailovic.weatherqinfo.model.WeatherWeek
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
        outerAdapter.listener = object : OnWeatherWeekItemInteraction{
            override fun showDetails(weatherWeek: WeatherWeek) {
                DetailsWeekFragment.newInstance(weatherWeek).show(requireActivity().supportFragmentManager, null)
            }
        }

        with(binding.rvWeatherWeek){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = outerAdapter
        }
    }

    private fun bind() {
        viewModel.weatherWeekListListLD.observe(viewLifecycleOwner){listList->
            listList?.let { outerAdapter.setItems(it) } ?: outerAdapter.setItems(emptyList())
        }
    }

    companion object{
        fun newInstance() = WeatherWeekFragment()
    }
}
package hr.trailovic.weatherqinfo.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentListCitiesBinding
import hr.trailovic.weatherqinfo.model.City
import hr.trailovic.weatherqinfo.showDialog
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel

class ListCitiesFragment : BaseDialogFragment<FragmentListCitiesBinding>() {

    private val citiesAdapter = ListCitiesAdapter()

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListCitiesBinding {
        return FragmentListCitiesBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setListCitiesRV()
        bind()
    }

    override fun setWidth() {
        setFullScreen()
    }

    private fun bind() {
        viewModel.getAllCitiesLD().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvCities.visibility = View.GONE
                binding.tvNoDataInfo.visibility = View.VISIBLE
                binding.tvNoDataInfo.text = "no cities to show"
            } else {
                binding.rvCities.visibility = View.VISIBLE
                binding.tvNoDataInfo.visibility = View.GONE
                citiesAdapter.setItems(it)
            }
        }
    }

    private fun setListCitiesRV() {
        citiesAdapter.listener = object : OnCityItemInteraction {
            override fun delete(city: City) {
                showDialog(requireContext(), "Remove ${city.name}?", "Cancel", "Remove") {
                    viewModel.removeCityData(city)
                }
            }

            override fun dismissDialog() {
                dialog?.dismiss()
            }
        }
        with(binding.rvCities) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = citiesAdapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListCitiesFragment()
    }
}
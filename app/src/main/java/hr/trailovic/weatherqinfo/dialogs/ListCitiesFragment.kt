package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import hr.trailovic.weatherqinfo.databinding.FragmentListCitiesBinding
import hr.trailovic.weatherqinfo.model.City
import hr.trailovic.weatherqinfo.setFullScreen
import hr.trailovic.weatherqinfo.showDialog
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel

class ListCitiesFragment : DialogFragment() {

    private var _binding: FragmentListCitiesBinding? = null
    private val binding get() = _binding!!

    private val citiesAdapter = ListCitiesAdapter()

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        setListCitiesRV()
        bind()
    }

    private fun bind() {
        viewModel.getAllCitiesLD().observe(viewLifecycleOwner) {
            citiesAdapter.setItems(it)
        }
    }

    private fun setListCitiesRV() {
        citiesAdapter.listener = object : OnCityItemInteraction {
            override fun delete(city: City) {
                showDialog(requireContext(), "Remove ${city.name}?", "Cancel", "Remove"){
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
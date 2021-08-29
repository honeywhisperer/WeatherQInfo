package hr.trailovic.weatherqinfo.view.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentDialogAddBinding
import hr.trailovic.weatherqinfo.model.CityResponse
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel

@AndroidEntryPoint
class DialogAddFragment : BaseDialogFragment<FragmentDialogAddBinding>() {

    private val viewModel: WeatherViewModel by activityViewModels()

    private val dialogAddAdapter = DialogAddAdapter(object: OnCityResponseItemInteraction{
        override fun addCityToList(cityResponse: CityResponse) {
            //todo: call VM function which receives CityResponse, converts it to City and stores it
            viewModel.saveCity(cityResponse)
            viewModel.cleanCitySearchData()
            dialog?.dismiss()
        }
    })

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDialogAddBinding {
        return FragmentDialogAddBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setListeners()
        setCityResponseRV()
        bind()
    }

    override fun cleanFragment() {
        viewModel.cleanCitySearchData()
        super.cleanFragment()
    }

    private fun bind() {
        viewModel.cityResponseListLD.observe(viewLifecycleOwner){
            dialogAddAdapter.setItems(it)
        }
    }

    private fun setCityResponseRV() {
        with(binding.rvCityResponseList){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dialogAddAdapter
        }
    }

    private fun setListeners() {
        binding.btnSearch.setOnClickListener {
            val userInput = binding.etCityName.text.toString()
            if (userInput.isBlank()) {
                binding.etCityName.error = "City name is required"
            } else {
                viewModel.checkCityRx(userInput)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DialogAddFragment()
    }
}
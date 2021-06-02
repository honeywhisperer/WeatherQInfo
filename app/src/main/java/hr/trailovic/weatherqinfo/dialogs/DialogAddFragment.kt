package hr.trailovic.weatherqinfo.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentDialogAddBinding
import hr.trailovic.weatherqinfo.fixUserInput
import hr.trailovic.weatherqinfo.setFullScreen
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel

@AndroidEntryPoint
class DialogAddFragment : BaseDialogFragment<FragmentDialogAddBinding>() {

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDialogAddBinding {
        return FragmentDialogAddBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setFullScreen()
        setListeners()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            val userInput = binding.etCityName.text.toString()
            if (userInput.isBlank()) {
                binding.etCityName.error = "City name is required"
            } else {
                viewModel.addCity(userInput.fixUserInput())
                dialog?.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DialogAddFragment()
    }
}
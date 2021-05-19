package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.databinding.FragmentDialogAddBinding
import hr.trailovic.weatherqinfo.setFullScreen
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel

@AndroidEntryPoint
class DialogAddFragment : DialogFragment() {

    private var _binding: FragmentDialogAddBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        setListeners()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            val userInput = binding.etCityName.text.toString()
            if (userInput.isBlank()) {
                binding.etCityName.error = "City name is required"
            } else {
                viewModel.addCity(userInput)
                dialog?.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DialogAddFragment()
    }
}
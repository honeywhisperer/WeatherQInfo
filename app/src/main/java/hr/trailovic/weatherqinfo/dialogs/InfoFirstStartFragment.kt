package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentInfoFirstStartBinding
import hr.trailovic.weatherqinfo.setFullScreen

class InfoFirstStartFragment : DialogFragment() {

    private var _binding: FragmentInfoFirstStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoFirstStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        setInfoText()
        setListeners()
    }

    private fun setListeners() {
        binding.root.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setInfoText() {
        val infoText =
            """Weather Q Info
            |
            |Features:
            |1. Shows current weather situation for your cities
            |2. Shows weather forecast for the following 7 days 
            |3. Press on each weather status for more details
            |
            |Future features:
            |1. Weather conditions for current location
            |2. Weather statuses on the map
            |3. ...
        """.trimMargin()
        binding.tvAppInfo.text = infoText
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFirstStartFragment()
    }
}
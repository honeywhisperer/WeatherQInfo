package hr.trailovic.weatherqinfo.view.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentInfoFirstStartBinding

class InfoFirstStartFragment : BaseDialogFragment<FragmentInfoFirstStartBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoFirstStartBinding {
        return FragmentInfoFirstStartBinding.inflate(inflater, container, false)
    }

    override fun setup() {
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
            |This app uses openwethermap.org api services.
            |
            |Features:
            |1. Shows current weather situation for your cities
            |2. Shows weather forecast for the following 7 days 
            |3. Press on each weather status for more details
            |4. View weather data on the map
            |
            |Future features:
            |1. Weather conditions for current location
            |2. ...
            |
            |
            |************************
            |*** Important Note ****
            |************************
            |
            |Make sure to provide your openweathermap.org api key before start using application
        """.trimMargin()
        binding.tvAppInfo.text = infoText
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoFirstStartFragment()
    }
}
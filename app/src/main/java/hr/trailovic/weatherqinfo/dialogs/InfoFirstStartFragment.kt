package hr.trailovic.weatherqinfo.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import hr.trailovic.weatherqinfo.base.BaseFragment
import hr.trailovic.weatherqinfo.databinding.FragmentInfoFirstStartBinding

class InfoFirstStartFragment : BaseFragment<FragmentInfoFirstStartBinding>() {
    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoFirstStartBinding {
        return FragmentInfoFirstStartBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setInfoText()
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

//todo
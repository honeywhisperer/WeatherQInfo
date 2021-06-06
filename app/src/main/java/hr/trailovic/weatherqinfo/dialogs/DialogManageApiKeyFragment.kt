package hr.trailovic.weatherqinfo.dialogs

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.R
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentDialogManageApiKeyBinding
import hr.trailovic.weatherqinfo.model.SharedPreferencesHelper
import hr.trailovic.weatherqinfo.setFullScreen
import hr.trailovic.weatherqinfo.showDialog
import javax.inject.Inject

@AndroidEntryPoint
class DialogManageApiKeyFragment : BaseDialogFragment<FragmentDialogManageApiKeyBinding>() {

    @Inject
    lateinit var prefsHelper: SharedPreferencesHelper

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDialogManageApiKeyBinding {
        return FragmentDialogManageApiKeyBinding.inflate(inflater, container, false)
    }

    override fun setup() {
        setFullScreen()
        setView()
        setListeners()
    }

    private fun setView() {
        val api = prefsHelper.readApiKey()
        if (api.isNotBlank())
            binding.ivApiKeyStatus.setImageResource(R.drawable.ic_ok)
        else
            binding.ivApiKeyStatus.setImageResource(R.drawable.ic_not_ok)
    }

    private fun setListeners() {
        binding.btnStoreApi.setOnClickListener {
            val newApiKey = binding.etNewApiKey.text.toString()
            if (newApiKey.isNotBlank()) {
                prefsHelper.storeApiKey(newApiKey)
                binding.etNewApiKey.setText("")
                binding.ivApiKeyStatus.setImageResource(R.drawable.ic_ok)
            }
//            dialog?.dismiss()
        }
        binding.btnLoadApiKey.setOnClickListener {
            binding.etNewApiKey.setText(prefsHelper.readApiKey())
        }
        binding.btnRemoveApiKey.setOnClickListener {
            showDialog(requireContext(), "Remove API key?", "Cancel", "Remove") {
                prefsHelper.storeApiKey("")
                binding.ivApiKeyStatus.setImageResource(R.drawable.ic_not_ok)
                binding.etNewApiKey.setText("")
            }
        }

        binding.btnVisitWebPage.setOnClickListener {
            val url = "http://www.openweathermap.org/"
            val newIntent = Intent().apply {
                setAction(Intent.ACTION_VIEW)
                setData(Uri.parse(url))
            }
            startActivity(newIntent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DialogManageApiKeyFragment()
    }
}
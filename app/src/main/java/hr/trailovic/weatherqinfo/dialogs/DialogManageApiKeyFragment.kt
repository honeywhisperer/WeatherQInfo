package hr.trailovic.weatherqinfo.dialogs

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseDialogFragment
import hr.trailovic.weatherqinfo.databinding.FragmentDialogManageApiKeyBinding
import hr.trailovic.weatherqinfo.model.SharedPreferencesHelper
import hr.trailovic.weatherqinfo.setFullScreen
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
        binding.tvCurrentApiKey.text = prefsHelper.readApiKey()
    }

    private fun setListeners() {
        binding.ibDeleteApiKey.setOnClickListener {
            prefsHelper.storeApiKey("")
            binding.tvCurrentApiKey.text = prefsHelper.readApiKey()
        }

        binding.ibStoreNewApiKey.setOnClickListener {
            val newApiKey = binding.etNewApiKey.text.toString()
            if (newApiKey.isNotBlank()){
                prefsHelper.storeApiKey(newApiKey)
                binding.etNewApiKey.setText("")
                binding.tvCurrentApiKey.text = prefsHelper.readApiKey()
            }
//            dialog?.dismiss()
        }

        binding.tvCurrentApiKey.setOnLongClickListener {
            binding.etNewApiKey.setText(binding.tvCurrentApiKey.text)
            true
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
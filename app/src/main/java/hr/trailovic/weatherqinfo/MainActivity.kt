package hr.trailovic.weatherqinfo

import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.base.BaseActivity
import hr.trailovic.weatherqinfo.databinding.ActivityMainBinding
import hr.trailovic.weatherqinfo.dialogs.BottomNavigationFragment
import hr.trailovic.weatherqinfo.dialogs.DialogAddFragment
import hr.trailovic.weatherqinfo.dialogs.InfoFirstStartFragment
import hr.trailovic.weatherqinfo.dialogs.ListCitiesFragment
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel
import hr.trailovic.weatherqinfo.viewtoday.WeatherTodayFragment
import javax.inject.Inject

private const val TAG = "mA:::"

private const val KEY_FIRST_START = "ApplicationFirstStartIndicator"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: WeatherViewModel by viewModels()

    @Inject
    lateinit var prefs: SharedPreferences

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setup() {
        openRxChannels()
        setAppBar()
        setInitView()
        bind()
    }

    private fun openRxChannels() {
        viewModel.openRxChannels()
    }

    private fun bind() {
        viewModel.messageLD.observe(this) {
            showSnackbar(it)
        }
        viewModel.loadingLD.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showSnackbar(message: String) {
        //todo can this (not blank check) be done better, completely in view model ?
        if (message.isNotBlank()) {
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Dismiss") {
                viewModel.dismissErrorMessage()
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }

    private fun setAppBar() {
        setSupportActionBar(binding.bottomAppBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setInitView() {
        displayFragment(this, WeatherTodayFragment.newInstance())

        val isThisFirstApplicationStart = prefs.getBoolean(KEY_FIRST_START, true)

        if (isThisFirstApplicationStart) {
            InfoFirstStartFragment.newInstance().show(supportFragmentManager, TAG)
        }

        val editor = prefs.edit()
        editor.putBoolean(KEY_FIRST_START, false)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                BottomNavigationFragment.newInstance().show(supportFragmentManager, TAG)
                true
            }
            R.id.action_add -> {
                DialogAddFragment.newInstance().show(supportFragmentManager, TAG)
                true
            }
            R.id.action_remove_all -> {
                showDialog(this, "Remove all cities?", "Cancel", "Remove") {
                    viewModel.removeAllData()
                }
                true
            }
            R.id.action_cities -> {
                ListCitiesFragment.newInstance().show(supportFragmentManager, TAG)
                true
            }
            R.id.action_info -> {
                InfoFirstStartFragment.newInstance().show(supportFragmentManager, TAG)
                true
            }
            else -> false
        }
    }
}
package hr.trailovic.weatherqinfo.view

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.trailovic.weatherqinfo.R
import hr.trailovic.weatherqinfo.base.BaseActivity
import hr.trailovic.weatherqinfo.databinding.ActivityMainBinding
import hr.trailovic.weatherqinfo.view.dialogs.*
import hr.trailovic.weatherqinfo.displayFragment
import hr.trailovic.weatherqinfo.model.SharedPreferencesHelper
import hr.trailovic.weatherqinfo.model.consume
import hr.trailovic.weatherqinfo.showDialog
import hr.trailovic.weatherqinfo.viewmodel.WeatherViewModel
import hr.trailovic.weatherqinfo.view.viewtoday.WeatherTodayFragment
import javax.inject.Inject

private const val TAG = "mA:::"


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: WeatherViewModel by viewModels()

    @Inject
    lateinit var prefsHelper: SharedPreferencesHelper

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setup() {
        setAppBar()
        setInitView()
        bind()
    }

    private fun bind() {
        viewModel.messageLD.observe(this) {oneTimeEvent->
            oneTimeEvent.consume {message->
                showSnackbar(message)
            }
        }
        viewModel.loadingLD.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showSnackbar(message: String) {
//        if (message.isNotBlank()) {
            val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Dismiss") {
//                viewModel.dismissErrorMessage()
                snackbar.dismiss()
            }
            snackbar.anchorView = binding.bottomAppBar
            snackbar.show()
//        }
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

        val isThisFirstApplicationStart = prefsHelper.readFirstStart()

        if (isThisFirstApplicationStart) {
            InfoFirstStartFragment.newInstance().show(supportFragmentManager, TAG)
            showSnackbar("Before start using the app, provide your own openweathermap.org API key")
        }

        prefsHelper.storeFirstStart(false)
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
            R.id.action_remove_weather->{
                showDialog(this, "Remove weather data but keep list of locations?", "Cancel", "Remove"){
                    viewModel.removeWeatherDataOnly()
                }
                true
            }
            R.id.action_refresh_weather->{
                viewModel.refreshCitiesList()
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
            R.id.action_api_key -> {
                DialogManageApiKeyFragment.newInstance().show(supportFragmentManager, TAG)
                true
            }
            else -> false
        }
    }
    /* For location permission request */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
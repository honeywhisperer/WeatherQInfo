package hr.trailovic.weatherqinfo

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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

private const val TAG = "mA:::"
private const val APP_PREFS_NAME = "WeatherQPrefsFile"
private const val KEY_FIRST_START = "ApplicationFirstStartIndicator"


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: WeatherViewModel by viewModels()

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
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
        viewModel.loadingLD.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
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
        val prefs = getSharedPreferences(APP_PREFS_NAME, MODE_PRIVATE)
        val isThisFirstApplicationStart = prefs.getBoolean(KEY_FIRST_START, true)

        val editor = getSharedPreferences(APP_PREFS_NAME, MODE_PRIVATE).edit()
        editor.putBoolean(KEY_FIRST_START, false)
        editor.apply()

        val welcomeMessage: String

        if (isThisFirstApplicationStart){
            displayFragment(this, InfoFirstStartFragment.newInstance())
            welcomeMessage = "W E L C O M E"
        }
        else{
            displayFragment(this, WeatherTodayFragment.newInstance())
            welcomeMessage = "W E L C O M E   B A C K"
        }
        Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()
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
            R.id.action_cities->{
                ListCitiesFragment.newInstance().show(supportFragmentManager, TAG)
                true
            }
            R.id.action_info->{
                displayFragment(this, InfoFirstStartFragment.newInstance())
                true
            }
            else -> false
        }
    }
}
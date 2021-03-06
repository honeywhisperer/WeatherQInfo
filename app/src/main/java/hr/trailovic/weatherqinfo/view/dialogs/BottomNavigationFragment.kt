package hr.trailovic.weatherqinfo.view.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.trailovic.weatherqinfo.R
import hr.trailovic.weatherqinfo.databinding.FragmentBottomNavigationBinding
import hr.trailovic.weatherqinfo.view.viewmap.WeatherMapFragment
import hr.trailovic.weatherqinfo.view.viewmylocation.WeatherMyLocationFragment
import hr.trailovic.weatherqinfo.view.viewtoday.WeatherTodayFragment
import hr.trailovic.weatherqinfo.view.viewweek.WeatherWeekFragment

class BottomNavigationFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomNavigationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavigation()
    }

    private fun setNavigation() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_today -> {
                    displayFragment(WeatherTodayFragment.newInstance())
                    dialog?.dismiss()
                    true
                }
                R.id.item_week -> {
                    displayFragment(WeatherWeekFragment.newInstance())
                    dialog?.dismiss()
                    true
                }
                R.id.item_map->{
                    displayFragment(WeatherMapFragment.newInstance())
                    dialog?.dismiss()
                    true
                }
                R.id.item_my_location->{
                    displayFragment(WeatherMyLocationFragment.newInstance())
                    dialog?.dismiss()
                    true
                }
                else -> {
                    displayMessage("Error occurred")
                    false
                }
            }
        }
    }

    private fun displayFragment(fragment: Fragment) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, fragment)
            .commit()
    }

    private fun displayMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BottomNavigationFragment()
    }
}
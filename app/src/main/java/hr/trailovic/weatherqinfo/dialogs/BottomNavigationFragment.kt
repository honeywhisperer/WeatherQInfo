package hr.trailovic.weatherqinfo.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.trailovic.weatherqinfo.R
import hr.trailovic.weatherqinfo.databinding.FragmentBottomNavigationBinding
import hr.trailovic.weatherqinfo.viewtoday.WeatherTodayFragment
import hr.trailovic.weatherqinfo.viewweek.WeatherWeekFragment

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
        //todo
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_today -> {
                    displayFragment(WeatherTodayFragment.newInstance())
//                    storeFragmentChange(MainActivity.FRAGMENT_TODAY)
                    dialog?.dismiss()
                    true
                }
                R.id.item_week -> {
                    displayFragment(WeatherWeekFragment.newInstance())
//                    storeFragmentChange(MainActivity.FRAGMENT_WEEK)
                    dialog?.dismiss()
                    true
                }
                R.id.item_map->{
                    displayMessage("To be implemented: Map")
                    //todo
//                    storeFragmentChange(MainActivity.FRAGMENT_MAP)
                    dialog?.dismiss()
                    true
                }
                R.id.item_my_location->{
                    displayMessage("To be implemented: My Location")
                    //todo
//                    storeFragmentChange(MainActivity.FRAGMENT_LOCATION)
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

//    private fun storeFragmentChange(value: String) {
//        val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
//        with(prefs.edit()){
//            putString(MainActivity.KEY_STORED_FRAGMENT, value)
//            apply()
//        }
//    }

    private fun displayFragment(fragment: Fragment) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, fragment)
            .commit()

        val x = requireActivity()
    }

    private fun displayMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BottomNavigationFragment()
    }
}
package hr.trailovic.weatherqinfo.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun onDestroyView() {
        _binding = null
        cleanFragment()
        super.onDestroyView()
    }

    /**
     * Inflate and return ViewBinding
     * */
    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Called in onViewCreated
     * */
    abstract fun setup()

    /**
     * Called in onDestroyView
     * */
    open fun cleanFragment() {}
}
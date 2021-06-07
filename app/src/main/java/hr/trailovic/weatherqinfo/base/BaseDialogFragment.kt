package hr.trailovic.weatherqinfo.base

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {
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
        setWidth()
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

    /**
     * You can call setFullScreen() or setWidthPercent(Int)
     * default: setWidthPercent(90)
     * */
    protected open fun setWidth() {
        setWidthPercent(90)
    }

    protected fun setFullScreen() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    protected fun setWidthPercent(percentage: Int) {
        val percent = percentage.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
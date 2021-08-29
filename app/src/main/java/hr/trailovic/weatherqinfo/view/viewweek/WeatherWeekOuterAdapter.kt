package hr.trailovic.weatherqinfo.view.viewweek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import hr.trailovic.weatherqinfo.databinding.ItemWeatherWeekOuterBinding
import hr.trailovic.weatherqinfo.model.WeatherWeek
import javax.inject.Inject

class WeatherWeekOuterAdapter @Inject constructor(
    private val glideRequestManager: RequestManager,
) : RecyclerView.Adapter<WeatherWeekOuterAdapter.WeatherWeekOuterViewHolder>() {

    private val weatherWeekList = mutableListOf<List<WeatherWeek>>()

    var listener: OnWeatherWeekItemInteraction? = null

    fun setItems(list: List<List<WeatherWeek>>) {
        val diff = DiffUtil.calculateDiff(WeatherWeekDiffCallback(weatherWeekList, list))
        with(weatherWeekList) {
            clear()
            addAll(list)
        }
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherWeekOuterViewHolder {
        return WeatherWeekOuterViewHolder(
            ItemWeatherWeekOuterBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherWeekOuterViewHolder, position: Int) {
        holder.bind(weatherWeekList[position])
    }

    override fun getItemCount(): Int = weatherWeekList.size

    inner class WeatherWeekOuterViewHolder(private val itemWeatherWeekOuterBinding: ItemWeatherWeekOuterBinding) :
        RecyclerView.ViewHolder(itemWeatherWeekOuterBinding.root) {


        fun bind(oneLocationWeatherWeek: List<WeatherWeek>) {
            itemWeatherWeekOuterBinding.tvLocation.text =
                oneLocationWeatherWeek[0].locationFullName

            val innerAdapter = WeatherWeekInnerAdapter(glideRequestManager)
            innerAdapter.listener = object : OnWeatherWeekItemInteraction {
                override fun showDetails(weatherWeek: WeatherWeek) {
                    listener?.showDetails(weatherWeek)
                }

            }
            with(itemWeatherWeekOuterBinding.rvWeatherSeven) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = innerAdapter
            }
            innerAdapter.setItems(oneLocationWeatherWeek)
        }
    }
}

class WeatherWeekDiffCallback(
    private val oldList: List<List<WeatherWeek>>,
    private val newList: List<List<WeatherWeek>>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition][0].locationFullName == newList[newItemPosition][0].locationFullName &&
                oldList[oldItemPosition][0].tempDay == newList[newItemPosition][0].tempDay &&
                oldList[oldItemPosition][0].timeTag == newList[newItemPosition][0].timeTag
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition][0].tempDay == newList[newItemPosition][0].tempDay
    }
}
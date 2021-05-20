package hr.trailovic.weatherqinfo.viewweek

import android.view.LayoutInflater
import android.view.ViewGroup
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
        with(weatherWeekList) {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
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
                oneLocationWeatherWeek[0].location

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
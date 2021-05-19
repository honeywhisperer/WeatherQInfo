package hr.trailovic.weatherqinfo.viewweek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.databinding.ItemWeatherWeekInnerBinding
import hr.trailovic.weatherqinfo.model.WeatherWeek

class WeatherWeekInnerAdapter(private val glideRequestManager: RequestManager) :
    RecyclerView.Adapter<WeatherWeekInnerAdapter.WeatherWeekInnerViewHolder>() {

    var listener: OnWeatherWeekItemInteraction? = null

    private val weatherWeekList = mutableListOf<WeatherWeek>()

    fun setItems(list: List<WeatherWeek>) {
        with(weatherWeekList) {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherWeekInnerViewHolder {
        return WeatherWeekInnerViewHolder(
            ItemWeatherWeekInnerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherWeekInnerViewHolder, position: Int) {
        holder.bind(weatherWeekList[position])
    }

    override fun getItemCount(): Int = weatherWeekList.size

    inner class WeatherWeekInnerViewHolder(private val itemWeatherWeekInnerBinding: ItemWeatherWeekInnerBinding) :
        RecyclerView.ViewHolder(itemWeatherWeekInnerBinding.root) {
        init {
            itemWeatherWeekInnerBinding.root.setOnLongClickListener {
                listener?.showDeatils(weatherWeekList[layoutPosition])
                true
            }
        }
        fun bind(weatherWeek: WeatherWeek) {
            with(itemWeatherWeekInnerBinding) {
                tvDate.text = weatherWeek.sunrise.toShortDateString()
                tvTemperatureMax.text = weatherWeek.tempMax.oneDecimal()
                tvTemperatureMin.text = weatherWeek.tempMin.oneDecimal()
                glideRequestManager
                    .load(weatherWeek.weatherIcon.toWeatherIconUrl())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .fitCenter()
                    .into(ivWeatherIcon)
            }
        }
    }
}

interface OnWeatherWeekItemInteraction {
    fun showDeatils(weatherWeek: WeatherWeek)
}
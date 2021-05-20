package hr.trailovic.weatherqinfo.viewtoday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import hr.trailovic.weatherqinfo.*
import hr.trailovic.weatherqinfo.databinding.ItemWeatherTodayBinding
import hr.trailovic.weatherqinfo.model.WeatherToday
import javax.inject.Inject

class WeatherTodayAdapter @Inject constructor(private val glideRequestManager: RequestManager) :
    RecyclerView.Adapter<WeatherTodayAdapter.WeatherTodayViewHolder>() {

    var listener: OnWeatherTodayItemInteraction? = null

    private val weatherList = mutableListOf<WeatherToday>()

    fun setItems(list: List<WeatherToday>) {
        with(weatherList) {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherTodayViewHolder {
        val itemWeatherTodayBinding =
            ItemWeatherTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherTodayViewHolder(itemWeatherTodayBinding)
    }

    override fun onBindViewHolder(holder: WeatherTodayViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    override fun getItemCount(): Int = weatherList.size

    inner class WeatherTodayViewHolder(private val itemWeatherTodayBinding: ItemWeatherTodayBinding) :
        RecyclerView.ViewHolder(itemWeatherTodayBinding.root) {
        init {
            itemWeatherTodayBinding.root.setOnLongClickListener {
                listener?.showDetails(weatherList[layoutPosition])
                true
            }
        }
        fun bind(weatherToday: WeatherToday) {
            with(itemWeatherTodayBinding) {
                tvLocation.text = weatherToday.city
                tvDescription.text = weatherToday.description
                tvTemperature.text = weatherToday.temp.oneDecimal().temperature()
                tvTemperatureFeelsLike.text = weatherToday.feels_like.generateFeelsLikeTemperatureText(weatherToday.temp)

                glideRequestManager
                    .load(weatherToday.icon.toWeatherIconUrl())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivWeatherIcon)
            }
        }
    }
}

interface OnWeatherTodayItemInteraction {
    fun showDetails(weatherToday: WeatherToday)
}
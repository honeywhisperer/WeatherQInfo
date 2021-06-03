package hr.trailovic.weatherqinfo.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.trailovic.weatherqinfo.databinding.ItemCityResponseBinding
import hr.trailovic.weatherqinfo.model.CityResponse

class DialogAddAdapter(private val listener: OnCityResponseItemInteraction) : RecyclerView.Adapter<DialogAddAdapter.CityResponseViewHolder>() {

    private val cityResponseList = mutableListOf<CityResponse>()

    fun setItems(list: List<CityResponse>) {
        with(cityResponseList) {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityResponseViewHolder {
        val itemCityResponseBinding =
            ItemCityResponseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityResponseViewHolder(itemCityResponseBinding)
    }

    override fun onBindViewHolder(holder: CityResponseViewHolder, position: Int) {
        holder.bind(cityResponseList[position])
    }

    override fun getItemCount(): Int = cityResponseList.size

    inner class CityResponseViewHolder(private val itemCityResponseBinding: ItemCityResponseBinding) :
        RecyclerView.ViewHolder(itemCityResponseBinding.root) {
        init {
            itemCityResponseBinding.root.setOnClickListener {
                listener.addCityToList(cityResponseList[layoutPosition])
            }
        }
        fun bind(cityResponse: CityResponse) {
            with(itemCityResponseBinding) {
                cityResponse.state?.let {
                    tvLocationInfo.text = "${cityResponse.name}, ${cityResponse.state}, ${cityResponse.country}"
                } ?: run{
                    tvLocationInfo.text = "${cityResponse.name}, ${cityResponse.country}"
                }
                tvLocationCoordinates.text = "lat=${cityResponse.lat} lon=${cityResponse.lon}"
            }
        }
    }
}

interface OnCityResponseItemInteraction{
    fun addCityToList(cityResponse: CityResponse)
}
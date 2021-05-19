package hr.trailovic.weatherqinfo.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.trailovic.weatherqinfo.databinding.ItemCityBinding
import hr.trailovic.weatherqinfo.model.City

class ListCitiesAdapter : RecyclerView.Adapter<ListCitiesAdapter.ListCitiesViewHolder>() {

    var listener: OnCityItemInteraction? = null

    private val citiesList = mutableListOf<City>()

    fun setItems(list: List<City>) {
        with(citiesList) {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCitiesViewHolder {
        return ListCitiesViewHolder(
            ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListCitiesViewHolder, position: Int) {
        holder.bind(citiesList[position])
    }

    override fun getItemCount(): Int = citiesList.size

    inner class ListCitiesViewHolder(private val itemCityBinding: ItemCityBinding) :
        RecyclerView.ViewHolder(itemCityBinding.root) {
        init {
            itemCityBinding.ivDelete.setOnClickListener {
                listener?.delete(citiesList[layoutPosition])
            }
            itemCityBinding.root.setOnClickListener {
                listener?.dismissDialog()
            }
        }

        fun bind(city: City) {
            itemCityBinding.tvCity.text = city.name
        }
    }
}

interface OnCityItemInteraction {
    fun delete(city: City)
    fun dismissDialog()
}
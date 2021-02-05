package cesar.school.exercise_fruits.ui.adapter

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import cesar.school.exercise_fruits.ui.MainActivity
import cesar.school.exercise_fruits.R
import cesar.school.exercise_fruits.databinding.ItemFruitBinding
import cesar.school.exercise_fruits.model.Fruit

class FruitAdapter(
        private val context: Context,
        private val originalFruitsList: ArrayList<Fruit>,
        private val callback: (Fruit, Int) -> Unit): RecyclerView.Adapter<FruitAdapter.VH>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemFruitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vh = VH(binding)
        vh.itemView.setOnClickListener {
            val fruit = fruits[vh.adapterPosition]
            callback(fruit, vh.adapterPosition)
        }
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val fruit = fruits[position]
        holder.fruitName.text = fruit.name
        holder.fruitBenefits.text = fruit.benefits
        if (fruit.photo != null) {
            holder.fruitPhoto.setImageDrawable(fruitPhotos.getDrawable(fruit.photo))
        } else {
            fruit.photoAdded?.let { holder.fruitPhoto.setImageBitmap(listNewPhotos[it]) }
        }
    }

    override fun getItemCount(): Int = fruits.size

    fun isFiltered(): Boolean {
        return originalFruitsList.size != fruits.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                fruits.clear()
                fruits.addAll(results.values as List<Fruit>)
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredResults: List<Fruit> = if (constraint.isEmpty()) {
                    originalFruitsList
                } else {
                    getFilteredResults(constraint.toString())
                }
                val results = FilterResults()
                results.values = filteredResults
                return results
            }
        }
    }

    private fun getFilteredResults(constraint: String): List<Fruit> {
        val results = ArrayList<Fruit>()
        when (constraint) {

            MainActivity.REMOVE_DUPLICATED_ORDERED_ALPHABETICALLY -> {
                val temp = ArrayList<Fruit>(originalFruitsList.distinctBy { fruit -> fruit.name })
                results.addAll(temp.sortedBy { fruit -> fruit.name.toLowerCase() })
            }

            MainActivity.REMOVE_DUPLICATED -> {
                results.addAll(originalFruitsList.distinctBy { fruit -> fruit.name })
            }

            MainActivity.ORDERED_ALPHABETICALLY_STATE -> {
                results.addAll(originalFruitsList.sortedBy { fruit -> fruit.name.toLowerCase() })
            }

            else -> {
                results.addAll(originalFruitsList.filter { fruit -> fruit.name.toLowerCase().trim().contains(constraint.toLowerCase().trim()) })
            }
        }
        return results
    }

    companion object {
        var fruits = arrayListOf<Fruit>()
    }

    init {
        fruits.addAll(originalFruitsList)
    }

    private val fruitPhotos: TypedArray by lazy {
        context.resources.obtainTypedArray(R.array.fruitPhotos)
    }

    private var listNewPhotos = MainActivity.listNewPhotos

    class VH(itemView: ItemFruitBinding): RecyclerView.ViewHolder(itemView.root) {
        val fruitName: TextView = itemView.itemFruitName
        val fruitBenefits: TextView = itemView.itemFruitDescription
        val fruitPhoto: ImageView = itemView.itemFruitPhoto
    }
}
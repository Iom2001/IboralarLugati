package uz.creater.iboralarlugati.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.creater.iboralarlugati.databinding.ItemCategoryBinding
import uz.creater.iboralarlugati.models.Category

class CategoryAdapter(var onCategoryClick: OnCategoryClick) :
    ListAdapter<Category, CategoryAdapter.Vh>(
        CategoryDiffUtils()
    ) {

    inner class Vh(private var binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(category: Category) {

            binding.name.text = category.name

            binding.itemLayout.setOnClickListener {
                onCategoryClick.onOnCategoryItemClick(category)
            }

            binding.vector.setOnClickListener {
                onCategoryClick.onCategoryIconClick(category, binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

    class CategoryDiffUtils : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.equals(newItem)
        }

    }

    interface OnCategoryClick {
        fun onOnCategoryItemClick(category: Category)
        fun onCategoryIconClick(category: Category, binding: ItemCategoryBinding)
    }
}
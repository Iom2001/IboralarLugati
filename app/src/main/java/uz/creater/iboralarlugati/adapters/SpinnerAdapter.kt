package uz.creater.iboralarlugati.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import uz.creater.iboralarlugati.databinding.ItemSpinnerBinding
import uz.creater.iboralarlugati.models.Category

class SpinnerAdapter(var list: List<Category>) : BaseAdapter() {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val categoryViewHolder: CategoryViewHolder = if (convertView == null) {
            val binding =
                ItemSpinnerBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
            CategoryViewHolder(binding)
        } else {
            CategoryViewHolder(ItemSpinnerBinding.bind(convertView))
        }
        categoryViewHolder.itemSpinnerBinding.tv.text = list[position].name
        return categoryViewHolder.itemView
    }

    inner class CategoryViewHolder {
        var itemView: View
        var itemSpinnerBinding: ItemSpinnerBinding

        constructor(itemSpinnerBinding: ItemSpinnerBinding) {
            itemView = itemSpinnerBinding.root
            this.itemSpinnerBinding = itemSpinnerBinding
        }
    }
}
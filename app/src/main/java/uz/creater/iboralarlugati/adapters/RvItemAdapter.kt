package uz.creater.iboralarlugati.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.creater.iboralarlugati.databinding.ItemRvBinding
import uz.creater.iboralarlugati.models.WordDic

class RvItemAdapter(var onRvItemClick: OnRvItemClick) :
    ListAdapter<WordDic, RvItemAdapter.Vh>(RvItemDiffUtil()) {

    inner class Vh(private var binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(wordDic: WordDic) {

            binding.name.text = wordDic.name
            binding.translation.text = wordDic.translation
            binding.itemLayout.setOnClickListener {
                onRvItemClick.onRvItemClick(wordDic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

    class RvItemDiffUtil : DiffUtil.ItemCallback<WordDic>() {

        override fun areItemsTheSame(oldItem: WordDic, newItem: WordDic): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: WordDic, newItem: WordDic): Boolean {
            return oldItem.equals(newItem)
        }

    }

    interface OnRvItemClick {
        fun onRvItemClick(wordDic: WordDic)
    }
}
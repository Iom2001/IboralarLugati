package uz.creater.iboralarlugati.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.creater.iboralarlugati.databinding.ItemWordBinding
import uz.creater.iboralarlugati.models.WordDic

class WordDicAdapter(var onWordDicClick: OnWordDicClick) :
    ListAdapter<WordDic, WordDicAdapter.Vh>(WordDicDiffUtils()) {

    inner class Vh(var binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(wordDic: WordDic) {
            binding.name.text = wordDic.name
            binding.translation.text = wordDic.translation

            binding.itemLayout.setOnClickListener {
                onWordDicClick.onWordDicItemClick(wordDic)
            }

            binding.vector.setOnClickListener {
                onWordDicClick.onWordDicIconClick(wordDic, binding)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

    class WordDicDiffUtils : DiffUtil.ItemCallback<WordDic>() {

        override fun areItemsTheSame(oldItem: WordDic, newItem: WordDic): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: WordDic, newItem: WordDic): Boolean {
            return oldItem.equals(newItem)
        }

    }

    interface OnWordDicClick {

        fun onWordDicItemClick(wordDic: WordDic)

        fun onWordDicIconClick(wordDic: WordDic, binding: ItemWordBinding)

    }
}
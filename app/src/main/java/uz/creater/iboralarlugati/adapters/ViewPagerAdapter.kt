package uz.creater.iboralarlugati.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.creater.iboralarlugati.fragments.PagerFragment
import uz.creater.iboralarlugati.models.Category

class ViewPagerAdapter(private var list: List<Category>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        if (list[position].name != null) {
            return PagerFragment.newInstance(list[position].name!!)
        }
        return PagerFragment()
    }
}
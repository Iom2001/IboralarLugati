package uz.creater.iboralarlugati.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.creater.iboralarlugati.fragments.FirstFragment
import uz.creater.iboralarlugati.fragments.SecondFragment

class DemoFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return FirstFragment()
        }
        return SecondFragment()
    }
}
package uz.creater.iboralarlugati

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import nl.joery.animatedbottombar.AnimatedBottomBar
import uz.creater.iboralarlugati.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)

        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                if (newIndex == 0) {
                    navController.popBackStack(R.id.listLikeFragment, true)
                    navController.navigate(R.id.firstHostFragment)
                } else if (newIndex == 1) {
                    navController.popBackStack(R.id.firstHostFragment, true)
                    navController.navigate(R.id.listLikeFragment)
                }
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {

            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }

    fun setBottomNavGone() {
        binding.bottomBar.visibility = View.GONE
    }

    fun setBottomNavVisible() {
        binding.bottomBar.visibility = View.VISIBLE
    }

    fun setBottomNav(a: Int) {
        if (a == 1) {
            binding.bottomBar.selectTabAt(0)
        } else {
            binding.bottomBar.selectTabAt(1)
        }
    }
}
package uz.creater.iboralarlugati.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nl.joery.animatedbottombar.AnimatedBottomBar
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.adapters.DemoFragmentAdapter
import uz.creater.iboralarlugati.databinding.AddCategoryDialogBinding
import uz.creater.iboralarlugati.databinding.FragmentSecondHostBinding
import uz.creater.iboralarlugati.models.Category

class SecondHostFragment : Fragment() {

    private lateinit var binding: FragmentSecondHostBinding
    private lateinit var demoFragmentAdapter: DemoFragmentAdapter
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var userUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userUid = auth.currentUser?.uid.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondHostBinding.inflate(layoutInflater)
        demoFragmentAdapter = DemoFragmentAdapter(requireActivity())
        binding.viewPager.adapter = demoFragmentAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                if (position == 0)
                    binding.bottomBar.selectTabAt(0)
                else binding.bottomBar.selectTabAt(1)
                super.onPageSelected(position)
            }

        })

        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                if (newIndex == 0) {
                    binding.viewPager.currentItem = 0
                } else binding.viewPager.currentItem = 1
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
            }
        })

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addBtn.setOnClickListener {
            if (binding.viewPager.currentItem == 0) {
                val alertDialog = AlertDialog.Builder(requireContext())
                val dialog = alertDialog!!.create()
                dialog.setCancelable(false)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                val dialogView: View = layoutInflater.inflate(
                    R.layout.add_category_dialog,
                    null,
                    false
                )
                dialog.setView(dialogView)
                val bindDialog = AddCategoryDialogBinding.bind(dialogView)
                bindDialog.saveBtn.setOnClickListener {
                    var name = bindDialog.nameCategory.text.toString()
                    var category = Category()
                    category.name = name
                    db.collection("category$userUid").document(category.name!!).set(category)
                        .addOnSuccessListener {
                            dialog.dismiss()
                            Toast.makeText(context, "Category is added", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            dialog.dismiss()
                            Toast.makeText(context, "Category is not added", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                bindDialog.cancelBtn.setOnClickListener {
                    Toast.makeText(context, "Category is canceled", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                if (true) {
                    val navOption = NavOptions.Builder()
                    navOption.setEnterAnim(R.anim.slide_in)
                    navOption.setExitAnim(R.anim.fade_out)
                    navOption.setPopEnterAnim(R.anim.fade_in)
                    navOption.setPopExitAnim(R.anim.slide_out)
                    findNavController().navigate(R.id.addFragment, Bundle(), navOption.build())
                } else {
                    Toast.makeText(context, "Add the category first", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }
}
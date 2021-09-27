package uz.creater.iboralarlugati.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import uz.creater.iboralarlugati.MainActivity
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.adapters.ViewPagerAdapter
import uz.creater.iboralarlugati.databinding.FragmentFirstHostBinding
import uz.creater.iboralarlugati.models.Category
import uz.creater.iboralarlugati.models.CheckAdded
import uz.creater.iboralarlugati.models.WordDic

class FirstHostFragment : Fragment() {

    private lateinit var binding: FragmentFirstHostBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var categoryList: ArrayList<Category>
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
        binding = FragmentFirstHostBinding.inflate(inflater)
        categoryList = ArrayList()
        viewPagerAdapter = ViewPagerAdapter(categoryList, requireActivity())
        binding.viewPager.adapter = viewPagerAdapter

        binding.addButton.setOnClickListener {
            (activity as MainActivity?)?.setBottomNavGone()
            val navOption = NavOptions.Builder()
            navOption.setEnterAnim(R.anim.slide_in)
            navOption.setExitAnim(R.anim.fade_out)
            navOption.setPopEnterAnim(R.anim.fade_in)
            navOption.setPopExitAnim(R.anim.slide_out)
            findNavController().navigate(R.id.secondHostFragment, Bundle(), navOption.build())
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = categoryList[position].name
        }.attach()
        addData()
        checkUser()
        return binding.root
    }

    private fun checkUser() {
        db.collection("userIsNew").document(userUid).get().addOnSuccessListener { result ->
            val bool = result.toObject(CheckAdded::class.java)
            if (bool == null || !bool.isAdded) {
                db.collection("defaultCategory").get().addOnSuccessListener { result ->
                    for (document in result) {
                        val category = document.toObject(Category::class.java)
                        db.collection("category$userUid").document(category.name!!).set(category)
                    }
                }
                db.collection("defaultData").get().addOnSuccessListener { result ->
                    for (document in result) {
                        val wordDic = document.toObject(WordDic::class.java)
                        db.collection("data$userUid").document(wordDic.name!!).set(wordDic)
                    }
                }
                val checkAdded = CheckAdded(true)
                db.collection("userIsNew").document(userUid).set(checkAdded)

//                setup()
//                setupCacheSize()
            }
        }
    }

//    private fun setup() {
//        val db = Firebase.firestore
//
//        val settings = firestoreSettings {
//            isPersistenceEnabled = true
//        }
//        db.firestoreSettings = settings
//    }
//
//    private fun setupCacheSize() {
//        val settings = firestoreSettings {
//            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
//        }
//        db.firestoreSettings = settings
//    }

    private fun addData() {
        db.collection("category$userUid").get().addOnSuccessListener { result ->
            for (document in result) {
                val category = document.toObject(Category::class.java)
                categoryList.add(category)
            }
            viewPagerAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setBottomNavVisible()
        (activity as MainActivity?)?.setBottomNav(1)
    }
}
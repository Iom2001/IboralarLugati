package uz.creater.iboralarlugati.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.creater.iboralarlugati.MainActivity
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.adapters.RvItemAdapter
import uz.creater.iboralarlugati.databinding.FragmentPagerBinding
import uz.creater.iboralarlugati.models.WordDic

private const val ARG_PARAM1 = "param1"

class PagerFragment : Fragment() {

    private var categoryId: String? = null
    private lateinit var binding: FragmentPagerBinding
    private lateinit var rvItemAdapter: RvItemAdapter
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var userUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getString(ARG_PARAM1)
        }
        userUid = auth.currentUser?.uid.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPagerBinding.inflate(inflater)
        rvItemAdapter = RvItemAdapter(object : RvItemAdapter.OnRvItemClick {

            override fun onRvItemClick(wordDic: WordDic) {
                (activity as MainActivity?)?.setBottomNavGone()
                val navOption = NavOptions.Builder()
                navOption.setEnterAnim(R.anim.slide_in)
                navOption.setExitAnim(R.anim.fade_out)
                navOption.setPopEnterAnim(R.anim.fade_in)
                navOption.setPopExitAnim(R.anim.slide_out)
                val bundle = Bundle()
                bundle.putString(ARG_PARAM1, wordDic.name!!)
                findNavController().navigate(R.id.infoFragment, bundle, navOption.build())
            }

        })

        binding.rvItem.adapter = rvItemAdapter

        loadData()

        return binding.root
    }

    private fun loadData() {
        db.collection("data$userUid").whereEqualTo("categoryId", categoryId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    var list = ArrayList<WordDic>()
                    for (document in value) {
                        val wordDic = document.toObject(WordDic::class.java)
                        list.add(wordDic)
                    }
                    rvItemAdapter.submitList(list)
                }
            }
    }

    companion object {

        private const val TAG = "PagerFragment"

        @JvmStatic
        fun newInstance(categoryId: String) =
            PagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, categoryId)
                }
            }
    }

}
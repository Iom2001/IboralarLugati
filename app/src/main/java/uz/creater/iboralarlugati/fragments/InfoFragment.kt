package uz.creater.iboralarlugati.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.databinding.FragmentInfoBinding
import uz.creater.iboralarlugati.models.WordDic

private const val ARG_PARAM1 = "param1"

class InfoFragment : Fragment() {

    private var wordDicId: String? = null
    private lateinit var binding: FragmentInfoBinding
    private lateinit var wordDic: WordDic
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var userUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordDicId = it.getString(ARG_PARAM1)
        }
        userUid = auth.currentUser?.uid.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(inflater)
        db.collection("data$userUid").document(wordDicId!!).get().addOnSuccessListener {
            wordDic = it.toObject(WordDic::class.java)!!
            binding.toolbarTitle.text = wordDic.name
            Picasso.get().load(wordDic.image).into(binding.image)
            binding.name.text = wordDic.name
            binding.translation.text = wordDic.translation
            if (wordDic.wordGood) {
                binding.imageHeart.tag = "liked"
                binding.imageHeart.setImageResource(R.drawable.heart_pink)
            } else {
                binding.imageHeart.tag = "like"
            }
            binding.imageHeart.setOnClickListener {
                if (binding.imageHeart.tag == "like") {
                    binding.imageHeart.setImageResource(R.drawable.heart_pink)
                    binding.imageHeart.tag = "liked"
                    wordDic.wordGood = true
                    db.collection("data$userUid").document(wordDicId!!).set(wordDic)
                } else {
                    binding.imageHeart.setImageResource(R.drawable.ic_heart_white)
                    binding.imageHeart.tag = "like"
                    wordDic.wordGood = false
                    db.collection("data$userUid").document(wordDicId!!).set(wordDic)
                }
            }
        }
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }
}
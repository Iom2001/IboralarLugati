package uz.creater.iboralarlugati.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.adapters.WordDicAdapter
import uz.creater.iboralarlugati.databinding.CheckDialogBinding
import uz.creater.iboralarlugati.databinding.FragmentSecondBinding
import uz.creater.iboralarlugati.databinding.ItemWordBinding
import uz.creater.iboralarlugati.models.WordDic

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SecondFragment : Fragment() {

    private lateinit var binding: FragmentSecondBinding
    private lateinit var wordAdapter: WordDicAdapter
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
    ): View? {
        binding = FragmentSecondBinding.inflate(inflater)
        wordAdapter = WordDicAdapter(object : WordDicAdapter.OnWordDicClick {

            override fun onWordDicItemClick(wordDic: WordDic) {

            }

            override fun onWordDicIconClick(wordDic: WordDic, bindingItem: ItemWordBinding) {
                var listItems = arrayOf("O'zgartirish", "O'chirish")
                var popUpAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(),
                    R.layout.popup_menu,
                    R.id.details,
                    listItems
                )
                val albumPopup = ListPopupWindow(requireContext())
                albumPopup.setContentWidth(500)
                albumPopup.setAdapter(popUpAdapter)
                albumPopup.height = ListPopupWindow.WRAP_CONTENT
                albumPopup.anchorView = bindingItem.vector
                val background = ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.res_black_menuroundfilled_corner
                )
                albumPopup.setBackgroundDrawable(background)
                albumPopup.isModal = true
                albumPopup.setDropDownGravity(Gravity.END)
                albumPopup.setOnItemClickListener { parent, view, position, id ->
                    if (position == 0) {
                        val navOption = NavOptions.Builder()
                        navOption.setEnterAnim(R.anim.slide_in)
                        navOption.setExitAnim(R.anim.fade_out)
                        navOption.setPopEnterAnim(R.anim.fade_in)
                        navOption.setPopExitAnim(R.anim.slide_out)
                        var bundle = Bundle()
                        bundle.putString(ARG_PARAM2, "b")
                        bundle.putString(ARG_PARAM1, wordDic.name!!)
                        albumPopup.dismiss()
                        findNavController().navigate(R.id.addFragment, bundle, navOption.build())
                    } else {
                        val alertDialog = AlertDialog.Builder(requireContext())
                        val dialog = alertDialog!!.create()
                        dialog.setCancelable(false)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                        val dialogView: View = layoutInflater.inflate(
                            R.layout.check_dialog,
                            null,
                            false
                        )
                        dialog.setView(dialogView)
                        val bindDialog = CheckDialogBinding.bind(dialogView)
                        bindDialog.title.text = "Bu kategoriyani o’chirasizmi?"
                        bindDialog.okBtn.setOnClickListener {
                            db.collection("data$userUid").document(wordDic.name!!).delete()
                            dialog.dismiss()
                            Toast.makeText(context, "Word is deleted", Toast.LENGTH_SHORT)
                                .show()
                        }
                        bindDialog.cancelBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                        albumPopup.dismiss()
                    }
                }
                albumPopup.show()
            }

        })
        binding.rvWord.adapter = wordAdapter
        loadData()
        return binding.root
    }

    private fun loadData() {
        db.collection("data$userUid")
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
                    wordAdapter.submitList(list)
                }
            }
    }

    companion object {
        private const val TAG = "SecondFragment"
    }

}
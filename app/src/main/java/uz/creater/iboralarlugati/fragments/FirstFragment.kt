package uz.creater.iboralarlugati.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.adapters.CategoryAdapter
import uz.creater.iboralarlugati.databinding.AddCategoryDialogBinding
import uz.creater.iboralarlugati.databinding.CheckDialogBinding
import uz.creater.iboralarlugati.databinding.FragmentFirstBinding
import uz.creater.iboralarlugati.databinding.ItemCategoryBinding
import uz.creater.iboralarlugati.models.Category

class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var categoryAdapter: CategoryAdapter
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
        binding = FragmentFirstBinding.inflate(inflater)
        categoryAdapter = CategoryAdapter(object : CategoryAdapter.OnCategoryClick {

            override fun onOnCategoryItemClick(category: Category) {

            }

            override fun onCategoryIconClick(category: Category, bindingItem: ItemCategoryBinding) {
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
                        bindDialog.nameCategory.setText(category.name)
                        bindDialog.saveBtn.setOnClickListener {
                            var name = bindDialog.nameCategory.text.toString()
                            val oldName = category.name
                            category.name = name
                            db.collection("category$userUid").document(oldName!!).delete()
                            db.collection("category$userUid").document(name).set(category)
                            dialog.dismiss()
                            Toast.makeText(context, "Category is updated", Toast.LENGTH_SHORT)
                                .show()
                        }
                        bindDialog.cancelBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
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
                            db.collection("category$userUid").document(category.name!!).delete()
                            dialog.dismiss()
                            Toast.makeText(context, "Category is deleted", Toast.LENGTH_SHORT)
                                .show()
                        }
                        bindDialog.cancelBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                    albumPopup.dismiss()
                }
                albumPopup.show()
            }
        })
        binding.rvCategory.adapter = categoryAdapter

        loadData()

        return binding.root
    }

    private fun loadData() {
        db.collection("category$userUid").addSnapshotListener { result, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            var categoryList = ArrayList<Category>()
            for (document in result!!) {
                val category = document.toObject(Category::class.java)
                categoryList.add(category)
            }
            categoryAdapter.submitList(categoryList)
            categoryAdapter.notifyDataSetChanged()
        }
    }
}
package uz.creater.iboralarlugati.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import uz.creater.iboralarlugati.BuildConfig
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.adapters.SpinnerAdapter
import uz.creater.iboralarlugati.databinding.FragmentAddBinding
import uz.creater.iboralarlugati.databinding.SelectDialogBinding
import uz.creater.iboralarlugati.models.Category
import uz.creater.iboralarlugati.models.WordDic
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var userUid: String
    private lateinit var wordDic: WordDic
    private lateinit var imageUri: Uri
    var test: String = "a"
    private lateinit var photoURI: Uri
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var spinnerAdapter: SpinnerAdapter
    private var wordDicId: String = ""
    private var boolean = false
    private lateinit var referenceStorage: StorageReference
    private val storage = Firebase.storage
    private var getEdData = false
    private lateinit var dialog: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            test = it.getString(ARG_PARAM2).toString()
            if (test == "b")
                wordDicId = it.getString(ARG_PARAM1).toString()
        }
        userUid = auth.currentUser?.uid.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        referenceStorage = storage.getReference("iboralar rasmi")
        val imageFile = createImageFile()
        photoURI =
            FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, imageFile)

        categoryList = ArrayList()
        spinnerAdapter = SpinnerAdapter(categoryList)
        binding.spinnerDialog.adapter = spinnerAdapter
        loadCategory()
        binding.image.setOnClickListener {
            checkPermissions()
        }

        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveBtn.setOnClickListener {
            if (getEdData) {
                var name = binding.nameWord.text.toString()
                var translate = binding.translate.text.toString()
                var category = categoryList[binding.spinnerDialog.selectedItemPosition]
                if (!this::imageUri.isInitialized || !boolean) {
                    Toast.makeText(context, "Please select the image!!!", Toast.LENGTH_SHORT).show()
                } else if (name.isNotBlank() && translate.isNotBlank()) {
                    closeKerBoard()
                    val m = System.currentTimeMillis()
                    val uploadTask = referenceStorage.child("iboralar rasmi").child(m.toString())
                        .putFile(imageUri)
                    val llPadding = 30
                    val ll = LinearLayout(context)
                    ll.orientation = LinearLayout.HORIZONTAL
                    ll.setPadding(llPadding, llPadding, llPadding, llPadding)
                    ll.gravity = Gravity.CENTER
                    var llParam = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    llParam.gravity = Gravity.CENTER
                    ll.layoutParams = llParam
                    val progressBar = ProgressBar(requireContext())
                    progressBar.isIndeterminate = true
                    progressBar.setPadding(0, 0, llPadding, 0)
                    progressBar.layoutParams = llParam
                    llParam = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    llParam.gravity = Gravity.CENTER
                    val tvText = TextView(requireContext())
                    tvText.text = "Loading ..."
                    tvText.setTextColor(Color.parseColor("#000000"))
                    tvText.textSize = 20f
                    tvText.layoutParams = llParam
                    ll.addView(progressBar)
                    ll.addView(tvText)
                    val builder: androidx.appcompat.app.AlertDialog.Builder =
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    builder.setCancelable(false)
                    builder.setView(ll)
                    dialog = builder.create()
                    dialog.show()
                    if (wordDicId != "") {
                        db.collection("data$userUid").document(wordDic.name!!).delete()
                            .addOnSuccessListener {
                                wordDic.name = name
                                wordDic.translation = translate
                                wordDic.categoryId = category.name
                                wordDic.image = imageUri.toString()
                                wordDic.wordGood = false
                                db.collection("data$userUid").document(name).set(wordDic)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Word is updated",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        binding.nameWord.setText("")
                                        binding.translate.setText("")
                                        boolean = false
                                        binding.spinnerDialog.setSelection(0)
                                        binding.image.setImageURI(null)
                                        dialog.dismiss()
                                        findNavController().popBackStack()
                                    }.addOnFailureListener {
                                        dialog.dismiss()
                                    }
                            }.addOnFailureListener {
                                Toast.makeText(context, "Word is not updated", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                    } else {
                        uploadTask.addOnSuccessListener { it ->
                            if (it.task.isSuccessful) {
                                val downloadUrl = it.metadata?.reference?.downloadUrl
                                downloadUrl?.addOnSuccessListener { imgUri ->
                                    wordDic = WordDic()
                                    wordDic.name = name
                                    wordDic.translation = translate
                                    wordDic.categoryId = category.name
                                    wordDic.image = imgUri.toString()

                                    db.collection("data$userUid").document(name).set(wordDic)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Data uploaded successfully!!!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            dialog.dismiss()
                                            binding.nameWord.setText("")
                                            binding.translate.setText("")
                                            boolean = false
                                            binding.spinnerDialog.setSelection(0)
                                            binding.image.setImageURI(null)
                                        }.addOnFailureListener {
                                            dialog.dismiss()
                                        }
                                }
                                downloadUrl?.addOnFailureListener {
                                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        }.addOnFailureListener {
                            dialog.dismiss()
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    closeKerBoard()
                } else {
                    Toast.makeText(context, "Please fill the blanks", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please wait...", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun loadCategory() {
        db.collection("category$userUid").get().addOnSuccessListener { result ->
            for (document in result) {
                val category = document.toObject(Category::class.java)
                categoryList.add(category)
            }
            spinnerAdapter.notifyDataSetChanged()
            getEdData = true
            checkPosition()
        }
    }

    private fun checkPosition() {
        if (wordDicId != "") {
            binding.toolbarTitle.text = "So’z tahrirlash"
            db.collection("data$userUid").document(wordDicId!!).get().addOnSuccessListener {
                wordDic = it.toObject(WordDic::class.java)!!
                Picasso.get().load(wordDic.image).into(binding.image)
                imageUri = Uri.parse(wordDic.image!!)
                boolean = true
                binding.nameWord.setText(wordDic.name)
                binding.translate.setText(wordDic.translation)
                for (category in categoryList) {
                    if (category.name == wordDic.categoryId) {
                        binding.spinnerDialog.setSelection(
                            categoryList.indexOf(
                                category
                            )
                        )
                        break
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    p0?.let {
                        if (p0.areAllPermissionsGranted()) {
                            val alertDialog = AlertDialog.Builder(requireContext())
                            val dialog = alertDialog!!.create()
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                            val dialogView: View = layoutInflater.inflate(
                                R.layout.select_dialog,
                                null,
                                false
                            )
                            dialog.setView(dialogView)
                            val bindDialog = SelectDialogBinding.bind(dialogView)

                            bindDialog.galleryTv.setOnClickListener {
                                pickImageFromNewGallery()
                                dialog.dismiss()
                            }

                            bindDialog.cameraTv.setOnClickListener {
                                getTakeImageContent.launch(photoURI)
                                dialog.dismiss()
                            }
                            dialog.show()
                        } else if (p0.isAnyPermissionPermanentlyDenied) {
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setCancelable(false)
                                .setMessage("Please accept our permissions")
                                .setPositiveButton("Ok") { _, _ ->
                                    val fragmentActivity: FragmentActivity = requireActivity()
                                    fragmentActivity.startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts(
                                                "package",
                                                fragmentActivity.packageName,
                                                null
                                            )
                                        )
                                    )
                                }
                                .show()
                        } else {
                            Toast.makeText(context, "Please give ", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).withErrorListener {
                Toast.makeText(context, "${it.name}", Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            binding.image.setImageURI(uri)
            imageUri = uri
            boolean = true
        }

    private fun pickImageFromNewGallery() {
        getImageContent.launch("image/*")
    }

    private val getTakeImageContent =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                binding.image.setImageURI(photoURI)
                imageUri = photoURI
                boolean = true
            }
        }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val externalFilesDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${format}",
            ".jpg",
            externalFilesDir
        ).apply {
            absolutePath
        }
    }

    private fun closeKerBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            trimCache(requireContext())
            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    private fun trimCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        // The directory is now empty so delete it
        return dir!!.delete()
    }
}
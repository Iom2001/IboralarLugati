package uz.creater.iboralarlugati.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.creater.iboralarlugati.MainActivity
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.databinding.FragmentRegistrationBinding
import uz.creater.iboralarlugati.models.User

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.setBottomNavVisible()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener {
            if (binding.usernameEditText.text.isNullOrBlank()) {
                binding.usernameTextInput.error = getString(R.string.error_username)
            } else if (!isEmailValid(binding.emailEditText.text)) {
                binding.emailTextInput.error = getString(R.string.error_email)
            } else if (!isPasswordValid(binding.passwordEditText.text)) {
                binding.passwordTextInput.error = getString(R.string.error_password)
            } else {
                binding.usernameTextInput.error = null // Clear the error
                binding.emailEditText.error = null // Clear the error
                binding.passwordTextInput.error = null // Clear the error
                val username = binding.usernameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                binding.usernameEditText.setText("")
                binding.emailEditText.setText("")
                binding.passwordEditText.setText("")
                closeKerBoard()
                createAccount(username, email, password)
            }
        }

        binding.usernameEditText.setOnKeyListener { _, _, _ ->
            if (!binding.passwordEditText.text.isNullOrBlank()) {
                binding.usernameTextInput.error = null
            }
            false
        }

        binding.emailEditText.setOnKeyListener { _, _, _ ->
            if (isEmailValid(binding.emailEditText.text)) {
                binding.emailTextInput.error = null
            }
            false
        }

        binding.passwordEditText.setOnKeyListener { _, _, _ ->
            if (isPasswordValid(binding.passwordEditText.text)) {
                binding.passwordTextInput.error = null //Clear the error
            }
            false
        }

        binding.cancelButton.setOnClickListener {

            findNavController().popBackStack(
                R.id.signInFragment,
                true
            )

            findNavController().popBackStack(
                R.id.registrationFragment,
                true
            )
        }

        return binding.root
    }

    private fun closeKerBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity?)?.setBottomNavGone()
    }

    private fun navigate() {

        findNavController().navigate(
            R.id.firstHostFragment
        )
        findNavController().popBackStack(
            R.id.signInFragment,
            true
        )
        findNavController().popBackStack(
            R.id.registrationFragment,
            true
        )
    }

    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }

    private fun isEmailValid(text: Editable?): Boolean {
        if (text != null) {
            if (text?.length!! > 10) {
                return text.substring(text.length - 10) == "@gmail.com"
            }
        }
        return false
    }

    private fun createAccount(username: String, email: String, password: String) {
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
        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
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
        val dialog = builder.create()
        dialog.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val uid = auth.currentUser?.uid
                    val user = User(uid, username, email, password)
                    db.collection("users").document("$uid").set(user).addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Registration completed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        navigate()
                    }.addOnFailureListener {
                        Log.w(TAG, "Error adding document", it)
                        Toast.makeText(context, "Error adding user", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
            }
    }

    companion object {
        private const val TAG = "RegistrationFragment"
    }
}
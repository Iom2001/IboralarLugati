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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.creater.iboralarlugati.MainActivity
import uz.creater.iboralarlugati.R
import uz.creater.iboralarlugati.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigate()
        }
        binding.nextButton.setOnClickListener {
            if (!isEmailValid(binding.emailEditText.text)) {
                binding.emailTextInput.error = getString(R.string.error_email)
            } else if (!isPasswordValid(binding.passwordEditText.text)) {
                binding.passwordTextInput.error = getString(R.string.error_password)
            } else {
                binding.emailEditText.error = null // Clear the error
                binding.passwordTextInput.error = null // Clear the error
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                binding.emailEditText.setText("")
                binding.passwordEditText.setText("")
                closeKerBoard()
                signIn(email, password)
            }
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
        }

        binding.goRegistration.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity?)?.setBottomNavGone()
    }

    private fun navigate() {

        findNavController().popBackStack(
            R.id.signInFragment,
            true
        )
        findNavController().navigate(R.id.firstHostFragment)
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

    private fun closeKerBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun signIn(email: String, password: String) {
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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    navigate()
                    dialog.dismiss()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Email or Password incorrect",
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
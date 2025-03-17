package com.example.listascolaborativas.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.R
import com.example.listascolaborativas.databinding.FragmentLoginBinding
import com.example.listascolaborativas.databinding.FragmentRecoverAccountBinding
import com.example.listascolaborativas.helper.FirebaseHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val  binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnSend.setOnClickListener { valideteData() }
    }
    private fun valideteData(){
        val email = binding.addEmail.text.toString().trim()

        if (email.isNotEmpty()){

            _binding!!.progressBar.isVisible = true

            recoverAccountUser(email)
        }else{
            Toast.makeText(requireContext(), "Informe seu email!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun recoverAccountUser(email: String){

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "verifique seu Email!!", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(
                    requireContext(),
                    FirebaseHelper.validError(task.exception?.message ?: ""),
                    Toast.LENGTH_SHORT
                ).show()
                _binding!!.progressBar.isVisible = false
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
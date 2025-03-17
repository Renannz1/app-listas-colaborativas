package com.example.listascolaborativas.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.R
import com.example.listascolaborativas.databinding.FragmentLoginBinding
import com.example.listascolaborativas.databinding.FragmentRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val  binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }
    private fun initClicks(){
        binding.btnCriar.setOnClickListener{valideteData()}
    }

    private fun valideteData(){
        val email = binding.addEmail.text.toString().trim()
        val senha = binding.addSenha.text.toString().trim()

        if (email.isNotEmpty()){

            if (senha.isNotEmpty()){

                _binding!!.progressBar.isVisible = true

                registerUser(email, senha)
            }else{
                Toast.makeText(requireContext(), "Informe sua senha!", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(requireContext(), "Informe seu email!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, senha: String){

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                } else {
                    _binding!!.progressBar.isVisible = false
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
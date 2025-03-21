package com.example.listascolaborativas.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.listascolaborativas.R
import com.example.listascolaborativas.databinding.FragmentSplashBinding
import com.example.listascolaborativas.model.LifecycleLogger
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val  binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed(this::checkAuth, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        LifecycleLogger.addLog("SplashFragment", "onStop")
    }

    private fun checkAuth(){
        val user = Firebase.auth.currentUser
        if (user != null) {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LifecycleLogger.addLog("SplashFragment", "onStart")
    }

    override fun onStart() {
        super.onStart()
        LifecycleLogger.addLog("SplashFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        LifecycleLogger.addLog("SplashFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        LifecycleLogger.addLog("SplashFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        LifecycleLogger.addLog("SplashFragment", "onStop")
    }

}
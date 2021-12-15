package com.example.panzehir.view.loginsignup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.panzehir.R
import com.example.panzehir.databinding.FragmentForgotPasswordBinding
import com.example.panzehir.databinding.MedicationTrackingFragmentBinding


class ForgotPassword : Fragment() {

    private var _binding: FragmentForgotPasswordBinding?=null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentForgotPasswordBinding.inflate(inflater,container,false)
        return binding.root
    }


}
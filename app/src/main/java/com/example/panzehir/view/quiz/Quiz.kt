package com.example.panzehir.view.quiz

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.panzehir.R
import com.example.panzehir.databinding.MedicationTrackingFragmentBinding
import com.example.panzehir.databinding.QuizFragmentBinding
import com.example.panzehir.viewModel.QuizViewModel

class Quiz : Fragment() {
    private var _binding: QuizFragmentBinding?=null
    private val binding get() = _binding!!
    private  val viewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=QuizFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }


}
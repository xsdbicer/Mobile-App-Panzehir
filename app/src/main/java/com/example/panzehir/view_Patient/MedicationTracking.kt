@file:Suppress("UNREACHABLE_CODE")

package com.example.panzehir.view_Patient

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.panzehir.adapters.MedicationRecyclerAdapter
import com.example.panzehir.databinding.MedicationTrackingFragmentBinding
import com.example.panzehir.viewModelPatient.MedicationTrackingViewModel

class MedicationTracking : Fragment() {
    private var _binding: MedicationTrackingFragmentBinding?=null
    private val binding get() = _binding!!
    private val adapter=MedicationRecyclerAdapter()
    private  val viewModel: MedicationTrackingViewModel by lazy{
        ViewModelProvider(this)[MedicationTrackingViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=MedicationTrackingFragmentBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.medicationRecycler.layoutManager=LinearLayoutManager(context)
        binding.medicationRecycler.adapter=adapter
    }
}
package com.example.panzehir.View_RelativesOfThePatient

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.panzehir.R
import com.example.panzehir.databinding.CardMachingGameFragmentBinding
import com.example.panzehir.databinding.FragmentHomeRelativesOfThePatientBinding
import com.example.panzehir.model.medication
import com.example.panzehir.utilities.Constants
import com.example.panzehir.utilities.ConstantsForRelativesMedication
import com.example.panzehir.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

class HomeRelativesOfThePatient : Fragment() {
    private var _binding: FragmentHomeRelativesOfThePatientBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeRelativesOfThePatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = context?.let { PreferenceManager(it) }!!
        getUser()
        binding.seeMoreMedication.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_homeRelativesOfThePatient_to_addMedicationPage)
        }
        binding.ProfileLinearLayout.setOnClickListener{Navigation.findNavController(it).navigate(R.id.action_homeRelativesOfThePatient_to_profile_RelativesPatient)}
        getMedication()

    }
    @SuppressLint("SetTextI18n")
    private fun getUser(){
        binding.nameOfPerson.text=preferenceManager.getString(Constants.KEY_FIRST_NAME_PATIENT)+" "+preferenceManager.getString(
            Constants.KEY_LAST_NAME_PATIENT)
        binding.typeOfBlood.text=preferenceManager.getString(Constants.KEY_BLOOD_PATIENT)
        val now = Calendar.getInstance()
        val nowYear=now.get(Calendar.YEAR)
//        println("nowYear  "+ nowYear)
        //birthday kullanıcı bilgisi boş geliyor.
        //val age=nowYear-preferenceManager.getString(Constants.KEY_BIRTHDAY_PATIENT)!!.toInt()
        //binding.typeOfBlood.text=age
    }
    @SuppressLint("SetTextI18n")
    fun getMedication(){
        val myUserId = preferenceManager.getString(ConstantsForRelativesMedication.KEY_PATIENT_ID)
        println(myUserId+":+++++ userId")
        var i =0
        val database = FirebaseFirestore.getInstance()
        database.collection(ConstantsForRelativesMedication.KEY_COLLECTION_MEDICATION)
            .whereEqualTo(ConstantsForRelativesMedication.KEY_PATIENT_ID,myUserId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    for (documentSnapshot: QueryDocumentSnapshot in it.result) {
                        if (i==0){

                            binding.medicaiton1.text= documentSnapshot.getString(ConstantsForRelativesMedication.KEY_NAME_MEDICATION).toString()+" "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_TIME).toString()+"\'da "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_AMOUNT).toString()+"tane "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_HUNGRY_OR_NOT).toString()
                            i++
                        }
                        else if(i==1){
                            binding.medicaiton2.text= documentSnapshot.getString(ConstantsForRelativesMedication.KEY_NAME_MEDICATION).toString()+" "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_TIME).toString()+"\'da "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_AMOUNT).toString()+"tane "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_HUNGRY_OR_NOT).toString()
                            i++
                        }
                        else{
                            binding.medicaiton3.text= documentSnapshot.getString(ConstantsForRelativesMedication.KEY_NAME_MEDICATION).toString()+" "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_TIME).toString()+"\'da "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_AMOUNT).toString()+"tane "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_HUNGRY_OR_NOT).toString()
                        }


                    }
                }
            }
    }


}
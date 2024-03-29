package com.example.panzehir.view_Patient

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.telecom.TelecomManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.panzehir.R
import com.example.panzehir.databinding.HomeFragmentBinding
import com.example.panzehir.utilities.Constants
import com.example.panzehir.utilities.ConstantsForRelativesMedication
import com.example.panzehir.utilities.PreferenceManager
import com.example.panzehir.viewModelPatient.HomeViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

class Home : Fragment(), SensorEventListener {
    private var _binding: HomeFragmentBinding?=null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    //Step Counter
    private var sensorManager: SensorManager? = null
    private var running = false
    private var previousTotalSteps = 0f
    private var totalSteps = 0f

    private  val viewModel: HomeViewModel by lazy { ViewModelProvider(this)[HomeViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding=HomeFragmentBinding.inflate(inflater,container,false)
        sensorManager = this.activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = context?.let { PreferenceManager(it) }!!
        getWater()
        // Navigation
        binding.ProfileLinearLayout.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_home2_to_profile) }
        binding.seeMoreMemories.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_home2_to_memories2) }
        binding.seeMoreMedication.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_home2_to_medicationTracking) }

        binding.signOut.setOnClickListener{ signOut() }
        binding.callEmergencyLayout.setOnClickListener { emergencyAlertButton() }
        binding.callFamilyLayout.setOnClickListener { callMyFamilyAlertButton(it) }

        getMedication()
        getUser()
        //Step Counter
        loadDataStep()
        resetSteps()

    }
    @SuppressLint("SetTextI18n")
    fun getWater(){
        val myUserId = preferenceManager.getString(Constants.KEY_ID_PATIENT)
        val date=getCurrentDate()
        val database = FirebaseFirestore.getInstance()
        database.collection(ConstantsForRelativesMedication.KEY_COLLECTION_WATER).orderBy(date).get()
            .addOnCompleteListener{
                if (it.isSuccessful && it.result != null) {
                    for (documentSnapshot: QueryDocumentSnapshot in it.result) {
                        if(myUserId==documentSnapshot.get(Constants.KEY_ID_PATIENT).toString()){
                            binding.waterTextview.text= documentSnapshot.getString(date).toString()+" bardak"
                        }
                    }
                }
            }
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())
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

                            binding.medication1.text= documentSnapshot.getString(ConstantsForRelativesMedication.KEY_NAME_MEDICATION).toString()+" "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_TIME).toString()+"\'da "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_AMOUNT).toString()+"tane "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_HUNGRY_OR_NOT).toString()
                            i++
                        }
                        else if(i==1){
                            binding.medication2.text= documentSnapshot.getString(ConstantsForRelativesMedication.KEY_NAME_MEDICATION).toString()+" "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_TIME).toString()+"\'da "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_AMOUNT).toString()+"tane "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_HUNGRY_OR_NOT).toString()
                            i++
                        }
                        else{
                            binding.medication3.text= documentSnapshot.getString(ConstantsForRelativesMedication.KEY_NAME_MEDICATION).toString()+" "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_TIME).toString()+"\'da "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_AMOUNT).toString()+"tane "+
                                    documentSnapshot.getString(ConstantsForRelativesMedication.KEY_HUNGRY_OR_NOT).toString()
                        }


                    }
                }
            }
    }

    private fun signOut() {
        Toast.makeText(context, "Çıkış yapılıyor", Toast.LENGTH_SHORT).show()
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_ID_PATIENT)!!)
        val updates: HashMap<String, Any> = HashMap()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clearPreference()
                val intent= Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Çıkış yapılamadı", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun getUser(){
        binding.nameOfPerson.text=preferenceManager.getString(Constants.KEY_FIRST_NAME_PATIENT)+" "+preferenceManager.getString(Constants.KEY_LAST_NAME_PATIENT)
        binding.typeOfBlood.text=preferenceManager.getString(Constants.KEY_BLOOD_PATIENT)
        val gender=preferenceManager.getString(Constants.KEY_GENDER_PATIENT)
        if (gender=="Erkek"){
            binding.photo.setBackgroundResource(R.drawable.dede)
        }
        else {
            binding.photo.setBackgroundResource(R.drawable.me)
        }

       binding.ageOfPerson.text=preferenceManager.getString(Constants.KEY_BIRTHDAY_PATIENT)
    }

    private fun callMyFamilyAlertButton(view: View) { Navigation.findNavController(view).navigate(R.id.action_home2_to_friendList) }

    private fun emergencyAlertButton(){
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Arama")
            .setMessage("Acil Durum Hattını Aramak İstiyor Musunuz?")
            .setPositiveButton("Ara") { dialog, which ->
                Toast.makeText(this.context, "Arama yönlendirilmektedir.", Toast.LENGTH_SHORT).show()
                val phone = 112
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel: $phone")
                startActivity(callIntent)
            }
            .setNegativeButton("Arama") { dialog, which ->
            Toast.makeText(this.context, "Arama iptal edildi.", Toast.LENGTH_SHORT).show()
            }
        builder.show()


    }

    //Step Counter
    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this.context, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        println("onSensorChanged")
        var tv_stepsTaken = binding.NumberOfStepText
        if (running) {
            totalSteps = event!!.values[0]
            preferenceManager.putString("totalSteps",totalSteps.toString())
           previousTotalSteps=preferenceManager.getString("previousTotalSteps")!!.toFloat()
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            tv_stepsTaken.text = ("$currentSteps")

            saveDataStep()
        }
    }
    fun resetSteps(){

        binding.NumberOfStepText.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this.context, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        binding.NumberOfStepText.setOnLongClickListener {
            previousTotalSteps = preferenceManager.getString("totalSteps")!!.toFloat()
            preferenceManager.putString("previousTotalSteps",previousTotalSteps.toString())
            binding.NumberOfStepText.text = 0.toString()
            saveDataStep()
            true
        }

    }
    private fun saveDataStep() {
        val step: HashMap<String, Any> = HashMap()
        val tc=preferenceManager.getString(Constants.KEY_ID_PATIENT)!!
        step["step"] = binding.NumberOfStepText.text
        step["previousStep"]= preferenceManager.getString("totalSteps")!!
        step[Constants.KEY_ID_PATIENT]=tc
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection("Step_Counter")
            .document(tc)
            .set(step)
            .addOnSuccessListener {
                Toast.makeText(context, "adım takibi güncellendi: ", Toast.LENGTH_SHORT).show()
                preferenceManager.putString("key_step", binding.NumberOfStepText.text.toString())
                preferenceManager.putString("previousStep", preferenceManager.getString("totalSteps")!!)

            }
            .addOnFailureListener { Toast.makeText(context, "Error: " + it.message, Toast.LENGTH_SHORT).show() }
    }
    private fun loadDataStep() {

        val myUserId = preferenceManager.getString(Constants.KEY_ID_PATIENT)
        val database = FirebaseFirestore.getInstance()
        database.collection("Step_Counter")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null ) {
                    for (documentSnapshot: QueryDocumentSnapshot in it.result) {
                        if(myUserId==documentSnapshot.getString("patient_id").toString()){
                            binding.NumberOfStepText.text=documentSnapshot.getString("step")!!.toString()
                            preferenceManager.putString("totalSteps",documentSnapshot.getString("step")!!.toString())
                            preferenceManager.putString("previousStep",documentSnapshot.getString("previousStep")!!.toString())

                        }
                    }
                }
            }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

}
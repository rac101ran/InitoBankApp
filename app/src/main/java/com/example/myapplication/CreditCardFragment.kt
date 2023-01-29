package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentCreditCardBinding
import com.example.myapplication.models.Atm
import com.example.myapplication.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule


class CreditCardFragment : Fragment() {

    private lateinit var binding : FragmentCreditCardBinding
    private lateinit var dbprivate : DatabaseReference
    private lateinit var dbpublic : DatabaseReference
    private lateinit var auth : FirebaseAuth
    var currentAccount : String = ""
    var currentName : String = ""
    var type : String = ""
    private var alertDialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()


        val sharedPreferences = context?.getSharedPreferences(context?.packageName.toString(), Context.MODE_PRIVATE)
        currentAccount = sharedPreferences?.getString("current_account_cache","").toString()
        currentName = sharedPreferences?.getString("current_name_cache","").toString()
        type = sharedPreferences?.getString("current_type_cache","").toString()
        binding = FragmentCreditCardBinding.inflate(layoutInflater)

        alertDialog = context?.let { ProgressDialogHelper.getDialog(it,layoutInflater) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertDialog?.show()
     //   Timer().schedule(3000) {

     //   }


        Log.e("account",currentAccount)
        Log.e("type",type)
        Log.e("name",currentName)

        lifecycleScope.launch(Dispatchers.IO) {
            val auth = FirebaseAuth.getInstance().currentUser
            val snapshot = FirebaseDatabase.getInstance().getReference("bank").child("users").child(auth?.uid.toString()).child("accounts").child(type).child(currentAccount).child("atm").get().await()
            val atm = snapshot.getValue(Atm::class.java)
            Log.e("info::",atm.toString())
            withContext(Dispatchers.Main) {
                binding.tvCardName.text = currentName
                var presentPin = ""
                if (atm != null) {
                    for(i in 0..15) {
                        if(i>0 && i % 4 == 0) presentPin+="-"
                        presentPin+=atm.pin[i]
                    }
                }
                alertDialog?.dismiss()
                binding.tvCardNumber.text = presentPin
                val validFrom = "From Date: "+ atm?.validFrom
                val validThru = "Till Date: "+atm?.validThru
                binding.tvcreditcardFrom.text = validFrom
                binding.tvcreditcardThru.text = validThru
                binding.tvcvvvalue.text = atm?.cvv
            }
        }

        binding.btcreditcard.setOnClickListener {
             when {
                 binding.etamount.text.isNullOrEmpty() -> binding.etamount.requestFocus()
                 else -> {
                     val amountToWithdraw = binding.etamount.text.toString()
                     lifecycleScope.launch(Dispatchers.IO) {
                             val auth = FirebaseAuth.getInstance().currentUser?.uid
                             val  dbPrivateBalancePath = FirebaseDatabase.getInstance().getReference("bank").child("users").child(auth.toString()).child("accounts").child(type).child(currentAccount).child("balance")
                             val balance = dbPrivateBalancePath.get().await().getValue(String::class.java)?:"0.0"

                             val dbPublic = FirebaseDatabase.getInstance().getReference("bank").child("accounts").child(currentAccount)
                             var dailyLimit = dbPublic.child("dailyLimit").get().await().getValue(String::class.java)?:"0.0"
                             var monthlyLimit = dbPublic.child("monthlyCount").get().await().getValue(String::class.java)?:"0.0"
                             val dbPublicEventPath = dbPublic.child("events")

                             val currentTimestamp = Timestamp(System.currentTimeMillis()).toString().split("-")
                             var fine : Double = 0.0
                             if(dbPublicEventPath.child("withdraw").get().await().exists()) {
                                 val lastWithPath = dbPublicEventPath.child("withdraw").limitToLast(1).get().await().key.toString().split("-")
                                 if(lastWithPath[0] != currentTimestamp[0] || lastWithPath[1] != currentTimestamp[1]) {
                                      monthlyLimit = "0"
                                      dailyLimit = "0.0"
                                 }else if(lastWithPath[1] == currentTimestamp[1]) {
                                      // monthlyLimit = (monthlyLimit.toInt() + 1).toString()
                                     val curTimestamp = System.currentTimeMillis()
                                     val givenTimestamp = "2023-01-20 22:46:31"
                                     val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                     val date = simpleDateFormat.parse(givenTimestamp)
                                     val givenTimestampInMillis = date?.time

                                     val after24Hour = givenTimestampInMillis?.plus(86400000)
                                     if (curTimestamp >= after24Hour!!) {
                                         dailyLimit = "0.0"
                                     }else {
                                         Log.e("valid","?")
                                     }
                                         if(monthlyLimit.toString().toInt() >= 5) {
//                                            amountToWithdraw = (amountToWithdraw.toDouble() + 500).toString()
                                             fine = 500.0
                                         }

                                 }
                             }
                            Log.d("amounttowithdraw",amountToWithdraw.toString())
                            Log.d("dailylimit",dailyLimit.toString())

                             if(amountToWithdraw.toDouble() + fine <= balance?.toDouble()!! && dailyLimit?.toDouble()!! + amountToWithdraw.toDouble() <=50000 && amountToWithdraw.toDouble() <= 20000) {
                                   dbPrivateBalancePath.setValue((balance.toDouble() - (amountToWithdraw.toDouble() + fine)).toString()).await()
                                   dailyLimit = (dailyLimit.toDouble() + amountToWithdraw.toDouble()).toString()
                                   monthlyLimit = (monthlyLimit?.toInt()!! + 1).toString()
                                   dbPublic.child("dailyLimit").setValue(dailyLimit).await()
                                   dbPublic.child("monthlyCount").setValue(monthlyLimit).await()
                                   val map = HashMap<String,String>()
                                   map["amount"] = amountToWithdraw
                                   map["description"] = "atm card"
                                   dbPublicEventPath.child("withdraw").child(Timestamp(System.currentTimeMillis()).toString().split(".")[0]).setValue(map).await()
                             }

                         }
                     }
                 }


        }
}



}

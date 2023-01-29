package com.example.myapplication.models

import android.accounts.Account
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.PassbookFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTransactionBinding
import com.example.myapplication.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class TransactionFragment : Fragment() {

    private lateinit var binding : FragmentTransactionBinding
    private var currentAccount = ""
    private var type = ""
    private var alertDialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentTransactionBinding.inflate(layoutInflater)


        val sharedPreferences = context?.getSharedPreferences(context?.packageName.toString(), Context.MODE_PRIVATE)
        currentAccount = sharedPreferences?.getString("current_account_cache","").toString()
      //  currentName = sharedPreferences?.getString("current_name_cache","").toString()
        type = sharedPreferences?.getString("current_type_cache","").toString()

        alertDialog = context?.let { ProgressDialogHelper.getDialog(it,layoutInflater) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.btTransferMOney.setOnClickListener {
             when {
                 binding.etTransferAccountNumid.text.isNullOrEmpty() -> binding.etTransferAccountNumid.requestFocus()
                 binding.etTranserAmt.text.isNullOrEmpty() -> binding.etTranserAmt.requestFocus()
                 else -> {
                       alertDialog?.show()
                       val amountToTransfer =  binding.etTranserAmt.text.toString().toDouble()
                       val accountToTransfer = binding.etTransferAccountNumid.text.toString()
                       val desciption = binding.etTransferMoneyDesc.text.toString()
                       binding.btTransferMOney.isEnabled = false
                       lifecycleScope.launch(Dispatchers.IO) {
                             val authCurrent = FirebaseAuth.getInstance().currentUser?.uid
                             val myPublicTransactions = FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                                 .child(currentAccount).get().await().getValue(Transactions::class.java)

                             val dbPrivateBalance =  FirebaseDatabase.getInstance().getReference("bank").child("users").child(authCurrent.toString()).child("accounts").child(type).child(currentAccount).child("balance")
                             var balance = dbPrivateBalance.get().await().getValue(String::class.java)?.toDouble()

                             if(myPublicTransactions?.balance!!.toDouble() > 0 && balance!=null) {
                                   balance+= myPublicTransactions.balance.toDouble()
                                   myPublicTransactions.balance = "0"
                                   dbPrivateBalance.setValue(balance.toString()).await()
                                 FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                                     .child(currentAccount).child("balance").setValue("0").await()
                             }
                             val accountsPublicPath = FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                             val dbPublicAccountsGet = accountsPublicPath.get().await()
                             var balanceValue : Double = 0.0
                             if(dbPublicAccountsGet.hasChild(accountToTransfer)) {
                                 val balancePath = accountsPublicPath.child(accountToTransfer).child("balance")
                                 if(balancePath.get().await().getValue(String::class.java) != "") {
                                        balanceValue = balancePath.get().await().getValue(String::class.java)?.toDouble()?:0.0
                                 }
                                 val privateBalance = dbPrivateBalance.get().await().getValue(String::class.java)?.toDouble()
                      //           Log.e("private balance",privateBalance.toString())
                                 if(privateBalance!=null && privateBalance >= amountToTransfer + (0.05  * amountToTransfer)) {
                                     balanceValue += amountToTransfer
                                     balancePath.setValue(balanceValue.toString()).await()
                                     val eventPath = accountsPublicPath.child(accountToTransfer).child("events")
                                     val eventMap : HashMap<String, String> = HashMap()
                                     eventMap["type"] = "credit"
                                     eventMap["from"] = currentAccount
                                     eventMap["amount"] = amountToTransfer.toString()
                                     eventMap["description"] = desciption
                                     val timeStamp = Timestamp(System.currentTimeMillis()).toString().split(".")[0]
                                     eventPath.child(timeStamp).setValue(eventMap).await()

                                     dbPrivateBalance.setValue((privateBalance - amountToTransfer + (0.05  * amountToTransfer)).toString()).await()

                                     val myeventPath = FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                                         .child(currentAccount).child("events")

                                     eventMap["type"] = "debit"
                                     eventMap["to"] = accountToTransfer
                                     eventMap["amount"] = amountToTransfer.toString()
                                     eventMap["description"] = "Amount credited for $desciption"

                                     myeventPath.child(timeStamp).setValue(eventMap).await()

                                     withContext(Dispatchers.Main) {
                                          alertDialog?.dismiss()
                                          Toast.makeText(context,"Money Transfer complete",Toast.LENGTH_SHORT).show()
                                        //  activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerid,PassbookFragment())?.commit()
                                           binding.btTransferMOney.isEnabled = true
                                     }
                                 }else {
                                       withContext(Dispatchers.Main) {
                                           Toast.makeText(context,"You do not have sufficient balance",Toast.LENGTH_SHORT).show()
                                       }
                                 }
                             }else {
                                 Toast.makeText(context,"This Account Doesn't exist",Toast.LENGTH_SHORT).show()
                             }
                       }
                 }
             }

        }
    }

}
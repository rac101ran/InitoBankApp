package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentCreatesavingsaccfragmentBinding
import com.example.myapplication.models.*
import com.example.myapplication.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Createsavingsaccfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Createsavingsaccfragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCreatesavingsaccfragmentBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var sharedPreferences : SharedPreferences? = null
    private var accountType = ""
    private var alertDialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreatesavingsaccfragmentBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("bank").child("users")

        sharedPreferences = context?.getSharedPreferences(context?.packageName.toString(),Context.MODE_PRIVATE)

        alertDialog = context?.let { ProgressDialogHelper.getDialog(it,layoutInflater) }

        return binding.root
    }

    private fun getReferralCode(name: String): Int {
        var referralvalue = 0
        val referralCodes = Array<Int>(26) { 0 }
        referralCodes[0] = 1;
        for (i in 1..25) referralCodes[i] = (2 * referralCodes[i - 1]) + i + 1
        for (ch in name) {
            referralvalue += referralCodes[ch.uppercaseChar().toInt() - 65]
        }
        return referralvalue
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvnrv.text = sharedPreferences?.getString("nrv","0")
        val interest = sharedPreferences?.getString("interest","0.0")
        accountType = sharedPreferences?.getString("current_type_cache","")!!

        if(interest?.toDouble()!! > 0.0) {
             binding.tvinterestaccount.text = "Interest : $interest%"
        }else {
             binding.tvinterestaccount.text = "No Interest"
        }

        binding.btsaving.setOnClickListener {
            when {
                binding.etsavingNameid.text.isNullOrEmpty() -> binding.etsavingNameid.requestFocus()
                binding.etsavingEmailid.text.isNullOrEmpty() -> binding.etsavingEmailid.requestFocus()
                binding.etsavingdobdid.text.isNullOrEmpty() -> binding.etsavingdobdid.requestFocus()
                binding.etsavingdobmid.text.isNullOrEmpty() -> binding.etsavingdobmid.requestFocus()
                binding.etsavingdobyid.text.isNullOrEmpty() -> binding.etsavingdobyid.requestFocus()
                binding.etsavingAadharid.text.isNullOrEmpty() -> binding.etsavingAadharid.requestFocus()
                binding.etsavingaddress1id.text.isNullOrEmpty() -> binding.etsavingaddress1id.requestFocus()
                binding.etsavingbalanceid.text.isNullOrEmpty() -> binding.etsavingbalanceid.requestFocus()
                binding.etsavingpassword1id.text.isNullOrEmpty() -> binding.etsavingpassword1id.requestFocus()
                binding.etsavingpassword2id.text.isNullOrEmpty() -> binding.etsavingpassword2id.requestFocus()
                binding.etsavingpassword2id.text.toString() != binding.etsavingpassword2id.text.toString() -> {
                    Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    alertDialog?.show()
                    binding.btsaving.isEnabled = false
                    auth.createUserWithEmailAndPassword(
                        binding.etsavingEmailid.text.toString(),
                        binding.etsavingpassword1id.text.toString()
                    ).addOnCompleteListener {
                        val accountId = String.format("%040d",BigInteger(UUID.randomUUID().toString().replace("-", ""), 16)).substring(0,10)
                        val creditCardPin = String.format("%040d",BigInteger(UUID.randomUUID().toString().replace("-", ""), 16)).substring(0,16)
                        val creditCardCvv = String.format("%040d",BigInteger(UUID.randomUUID().toString().replace("-", ""), 16)).substring(0,3)

                        val personalDetails = PersonalDetails(
                            name = binding.etsavingNameid.text.toString() + " " + binding.etsavingName2id.text.toString(),
                            emailid = binding.etsavingEmailid.text.toString(),
                            aadhar = binding.etsavingAadharid.text.toString(),
                            dob = binding.etsavingdobdid.text.toString() + "-" + binding.etsavingdobmid.text.toString() + "-" + binding.etsavingdobyid.text.toString(),
                            address = binding.etsavingaddress1id.text.toString() + " " + binding.etsavingaddress2id.text.toString(),
                            phone = binding.etsavingphoneid.text.toString())

                        val timestamp = Timestamp(System.currentTimeMillis()).toString()
                        val timeInfo = timestamp.split("-")
                        val referralCode = getReferralCode(binding.etsavingNameid.text.toString()).toString()

                        val accountInfo = AccountInfo(
                            accountid = "AC-$accountId",
                            balance = binding.etsavingbalanceid.text.toString(),
                            createdOn =  timestamp,
                            atm = Atm(pin = creditCardPin , cvv = creditCardCvv , validFrom = timeInfo[0] + "/" + timeInfo[1] , validThru = (timeInfo[0].toInt() + 10).toString() + "/" + timeInfo[1]) ,
                            interest = "${sharedPreferences?.getString("interest","0.0")}%$timestamp",
                            nrv =  sharedPreferences?.getString("nrv","0")!!,
                            referralCode = referralCode
                        )
                        sharedPreferences?.edit()?.putString("current_account_cache","AC-$accountId")?.apply()
                        sharedPreferences?.edit()?.putString("current_name_cache",personalDetails.name)?.apply()


                        val savingAccount = SavingAccount(personalDetails = personalDetails , accountInfo  = accountInfo)

                        lifecycleScope.launch(Dispatchers.IO) {
                           // auth.createUserWithEmailAndPassword(binding.etsavingEmailid.text.toString(),binding.etsavingpassword1id.text.toString()).await()
                            db.child(auth.uid.toString()).child("personal").setValue(savingAccount.personalDetails).await()
                            db.child(auth.uid.toString()).child("accounts").child(accountType).child("AC-$accountId").setValue(savingAccount.accountInfo).await()
                            FirebaseDatabase.getInstance().getReference("bank").child("accounts").child("AC-$accountId").setValue(Transactions(events = HashMap<String,Any>(),"0","0", referralCode = referralCode, balance = "0", type = accountType)).await()
                            withContext(Dispatchers.Main) {
                                alertDialog?.dismiss()
                                Toast.makeText(context, "$accountType Account Created", Toast.LENGTH_SHORT).show()
                                val intent = Intent(activity,Account::class.java)
                                startActivity(intent)
                                //Toast.makeText(context, "Welcome toComplete", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnCanceledListener {
                        binding.btsaving.isEnabled = true
                        Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}



//.addOnSuccessListener {
//                                db.child(auth.uid.toString()).child("accounts").child("savings").setValue(savingAccount.accountInfo).addOnSuccessListener {
//                                    FirebaseDatabase.getInstance().getReference("bank").child("accounts").child("AC-$accountId").setValue(Transactions("","0","0", referralCode = referralCode, balance = accountInfo.balance , type = "savings")).addOnSuccessListener {
//                                        Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
//                                        val intent = Intent(activity,Account::class.java)
//                                        startActivity(intent)
//                                    }
//                                }
//                            }.addOnFailureListener{
//                                Toast.makeText(context,"Could not add your personal details",Toast.LENGTH_SHORT).show()
//                            }




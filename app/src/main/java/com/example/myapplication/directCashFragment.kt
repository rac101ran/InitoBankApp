package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.databinding.FragmentCheckbalanceBinding
import com.example.myapplication.databinding.FragmentDirectCashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.sql.Timestamp

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [directCashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class directCashFragment : Fragment() {


    private lateinit var auth : FirebaseAuth
    private lateinit var dbpublic : DatabaseReference
    private lateinit var dbprivate : DatabaseReference
    private lateinit var binding: FragmentDirectCashBinding
    var dailyLimit = 0
    private lateinit var dbadmin : DatabaseReference
    var currentAccount : String = ""
    var balance = 0
    var accountType = ""
    var currentbalance = 0
    private var lastevent = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentDirectCashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        auth = FirebaseAuth.getInstance()
//
//        val sharedPreferences = context?.getSharedPreferences(context?.packageName.toString(), Context.MODE_PRIVATE)
//        currentAccount = sharedPreferences?.getString("current_account_cache","").toString()
//        accountType = sharedPreferences?.getString("current_type_cache","").toString()
//
//
//
//        dbpublic  = FirebaseDatabase.getInstance().getReference("bank").child("accounts").child(currentAccount)
//
//        Log.e("currentid",auth.uid.toString() + "?")
//        dbprivate = FirebaseDatabase.getInstance().getReference("bank").child("users").child(auth.uid.toString()).child("accounts")
//
//

//        dbpublic.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                balance = snapshot.child("balance").value.toString().toInt()
//                account_type = snapshot.child("type").value.toString()
//                lastevent = snapshot.child("events").value.toString()
//                Log.e("BALANCE IN DBPUBLIC ", balance.toString())
//                Log.e("ACCOUNT TYPE IN DBPUBLIC",account_type)
//                Log.e("LAST EVENT IN DBPUBLIC", lastevent)
//
//                dbprivate.child(account_type).addValueEventListener(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        currentbalance = snapshot.child("balance").value.toString().toInt()
//
//                        if(balance > 0) {
//                            Log.e("balance",balance.toString())
//                            currentbalance+=balance
//                            dbpublic.child("balance").setValue("0").addOnSuccessListener {
//                                Log.d("balance:",balance.toString())
//                            }
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//
//        print(account_type + "current?")
//        println(dbprivate.child(account_type).toString() + "    " + account_type.toString() + "account")
//
//
//        dbadmin = FirebaseDatabase.getInstance().getReference("bank").child("Admin").child("pending requests")
//
//        binding.btsavingcashwithdraw.setOnClickListener {
//              when {
//                   binding.etSavingCashWithdraw.text.isNullOrEmpty()  || binding.etSavingCashWithdraw.text.toString().length > 10 -> binding.etSavingCashWithdraw.requestFocus()
//                   binding.etSavingCashWithdrawPassword.text.isNullOrEmpty() -> binding.etSavingCashWithdrawPassword.requestFocus()
//                   else -> {
//                       binding.btsavingcashwithdraw.isEnabled = false
//
//                       dbpublic.addValueEventListener(object : ValueEventListener{
//                           override fun onDataChange(snapshot: DataSnapshot) {
//                               dailyLimit = snapshot.child("dailyLimit").value.toString().toInt()
//
//                               if(lastevent.isNullOrEmpty() || lastevent.split(" ")[0] != Timestamp(System.currentTimeMillis()).toString()) {
//                                      dailyLimit = 0
//
//                               }
//
//                               Log.e("dailyLimit",dailyLimit.toString())
//                               Log.e("dailyLimit",currentbalance.toString())
//                               if(dailyLimit + binding.etSavingCashWithdraw.text.toString().toInt() <= 50000 && currentbalance >= dailyLimit + binding.etSavingCashWithdraw.text.toString().toInt()) {
//                                   Log.e("balancewhen withdrawn",currentAccount)
//                                   val hashMap : HashMap<String, String> = HashMap<String, String> ()
//                                   hashMap["amt"] = binding.etSavingCashWithdraw.text.toString()
//                                   dbadmin.child(currentAccount).child(Timestamp(System.currentTimeMillis()).toString().split(".")[0]).setValue(hashMap).addOnSuccessListener {
//                                          Toast.makeText(context,"Request made to Admin/Cashier",Toast.LENGTH_SHORT).show()
//                                   }
//                               }else {
//                                    binding.btsavingcashwithdraw.isEnabled = true
//                                    Toast.makeText(context,"Enter valid Amount",Toast.LENGTH_SHORT).show()
//                               }
//                           }
//
//                           override fun onCancelled(error: DatabaseError) {
//
//                           }
//
//                       })
//
//                   }
//              }
//        }
    }

}
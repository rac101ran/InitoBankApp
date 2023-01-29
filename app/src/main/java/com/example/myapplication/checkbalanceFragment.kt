package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentCheckbalanceBinding
import com.example.myapplication.models.AccountInfo
import com.example.myapplication.models.Atm
import com.example.myapplication.models.Transactions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.sql.Timestamp


class checkbalanceFragment : Fragment() {

    lateinit var binding: FragmentCheckbalanceBinding
    private var currentAccount = ""
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCheckbalanceBinding.inflate(layoutInflater)


        val sharedPreferences =
            context?.getSharedPreferences(context?.packageName.toString(), Context.MODE_PRIVATE)
        currentAccount = sharedPreferences?.getString("current_account_cache", "").toString()
        type = sharedPreferences?.getString("current_type_cache", "").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            val auth = FirebaseAuth.getInstance().currentUser
            val dbPublic = FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                .child(currentAccount)
            val transactions = dbPublic.get().await().getValue(Transactions::class.java)
            val dbPrivateBalance =
                FirebaseDatabase.getInstance().getReference("bank").child("users")
                    .child(auth?.uid.toString()).child("accounts")
                    .child(type).child(currentAccount).child("balance")
            var balance = dbPrivateBalance.get().await().getValue(String::class.java)?.toLong()

            if (transactions?.balance!!.toLong() > 0 && balance != null) {
                balance += transactions.balance.toLong()
                transactions.balance = "0"
                dbPrivateBalance.setValue(balance.toString()).await()
            }
            withContext(Dispatchers.Main) {
                binding.tvprofilebalance.text = balance.toString()
            }
        }

    }
}
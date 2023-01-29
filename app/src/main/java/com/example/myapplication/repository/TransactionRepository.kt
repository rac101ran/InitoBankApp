package com.example.myapplication.repository

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.models.Transactions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.sql.Timestamp

class TransactionRepository(private val context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    suspend fun getCurrentAccount(): String {
        return sharedPreferences.getString("current_account_cache", "")!!
    }

    suspend fun getType(): String {
        return sharedPreferences.getString("current_type_cache", "")!!
    }

    // update balance then , transfer money
    suspend fun updateBalance(currentAccount: String,type : String , auth : String , livedata : MutableLiveData<HashMap<String,Double>>) {
        val myPublicTransactions =
            FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                .child(currentAccount).get().await().getValue(Transactions::class.java)

        val dbPrivateBalance = FirebaseDatabase.getInstance().getReference("bank").child("users")
            .child(auth).child("accounts").child(type).child(currentAccount)
            .child("balance")
        val balance = dbPrivateBalance.get().await().getValue(String::class.java)?.toDouble()
        val publicBalance = myPublicTransactions?.balance?.toDouble()
        val map = HashMap<String,Double>()
        map["private_balance"] = balance?:0.0
        map["public_balance"] = publicBalance?:0.0
        livedata.postValue(map)
    }
    suspend fun transferMoney(
        auth : String,
        amount: String,
        account: String,
        description: String,
        currentAccount: String,
        type: String,livedata : MutableLiveData<HashMap<String,String>>) {

//        val authCurrent = FirebaseAuth.getInstance().currentUser?.uid
//        val myPublicTransactions =
//            FirebaseDatabase.getInstance().getReference("bank").child("accounts")
//                .child(currentAccount).get().await().getValue(Transactions::class.java)
//
//
//        var balance = dbPrivateBalance.get().await().getValue(String::class.java)?.toDouble()
//
//        if (myPublicTransactions?.balance!!.toDouble() > 0 && balance != null) {
//            balance += myPublicTransactions.balance.toDouble()
//            myPublicTransactions.balance = "0"
//            dbPrivateBalance.setValue(balance.toString()).await()
//            FirebaseDatabase.getInstance().getReference("bank").child("accounts")
//                .child(currentAccount).child("balance").setValue("0").await()
//        }

        val dbPrivateBalance = FirebaseDatabase.getInstance().getReference("bank").child("users")
            .child(auth).child("accounts").child(type).child(currentAccount)
            .child("balance")

        val accountsPublicPath =
            FirebaseDatabase.getInstance().getReference("bank").child("accounts")
        val dbPublicAccountsGet = accountsPublicPath.get().await()

        var balanceValue: Double = 0.0
        if (dbPublicAccountsGet.hasChild(account)) {
            val balancePath = accountsPublicPath.child(account).child("balance")
            if (balancePath.get().await().getValue(String::class.java) != "") {
                balanceValue =
                    balancePath.get().await().getValue(String::class.java)?.toDouble() ?: 0.0
            }
            val privateBalance =
                dbPrivateBalance.get().await().getValue(String::class.java)?.toDouble()
            if (privateBalance != null && privateBalance >= amount.toDouble() + (0.05 * amount.toDouble())) {
                balanceValue += amount.toDouble()
                balancePath.setValue(balanceValue.toString()).await()
                val eventPath = accountsPublicPath.child(account).child("events")
                val eventMap: HashMap<String, String> = HashMap()
                eventMap["type"] = "credit"
                eventMap["from"] = currentAccount
                eventMap["amount"] = amount
                eventMap["description"] = description
                val timeStamp = Timestamp(System.currentTimeMillis()).toString().split(".")[0]
                eventPath.child(timeStamp).setValue(eventMap).await()

                dbPrivateBalance.setValue((privateBalance - amount.toDouble() + (0.05 * amount.toDouble())).toString())
                    .await()

                val myeventPath = FirebaseDatabase.getInstance().getReference("bank").child("accounts")
                    .child(currentAccount).child("events")

                eventMap["type"] = "debit"
                eventMap["to"] = account
                eventMap["amount"] = amount
                eventMap["description"] = "Amount credited for $description"

                myeventPath.child(timeStamp).setValue(eventMap).await()

             //   _successToast.postValue(Event.valueOf(true))



//
//                val myPrivateTransactions =
//                    FirebaseDatabase.getInstance().getReference("bank").child("users")
//                        .child(authCurrent.toString()).child("accounts").child(type)
//                        .child(currentAccount).child("transactions")
//                val map: HashMap<String, String> =

            }else {

            }

        }
    }
    private val _successToast = MutableLiveData<Event>()
    val successToast: LiveData<Event>
        get() = _successToast

    private val _errorToast = MutableLiveData<Event>()
    val errorToast: LiveData<Event>
        get() = _errorToast
}
//
//class TransactionRepository {
//    // ... existing code
//
//    suspend fun transferMoney(amount: Double, account: String, description: String, myPublicTransactions: Transactions, privateBalance: Double): Double {
//        var updatedBalance = privateBalance
//        if (myPublicTransactions.balance.toDouble() > 0) {
//            updatedBalance += myPublicTransactions.balance.toDouble()
//            myPublicTransactions.balance = "0"
//            FirebaseDatabase.getInstance().getReference("bank").child("accounts")
//                .child(currentAccount).child("balance").setValue("0").await()
//        }
//
//        val accountsPublicPath = FirebaseDatabase.getInstance().getReference("bank").child("accounts")
//        val dbPublicAccountsGet = accountsPublicPath.get().await()
//        var balanceValue: Double = 0.0
//
//        if (dbPublicAccountsGet.hasChild(account)) {
//            val balancePath = accountsPublicPath.child(account).child("balance")
//            if (balancePath.get().await().getValue(String::class.java) != "") {
//                balanceValue = balancePath.get().await().getValue(String::class.java)?.toDouble() ?: 0.0
//            }
//
//            if (privateBalance >= amount + (0.05 * amount)) {
//                balanceValue += amount
//                val eventPath = accountsPublicPath.child(account).child("events")
//                val eventMap: HashMap<String, String> = HashMap()
//                eventMap["type"] = "credit"
//                eventMap["from"] = currentAccount
//                eventMap["amount"] = amount.toString()
//                eventMap["description"] = description
//                val timeStamp = Timestamp(System.currentTimeMillis()).toString().split(".")[0]
//                eventPath.child(timeStamp).setValue(eventMap).await()
//
//                // add this line to display the toast message
//                _successToast.postValue(Event(true))
//                return balanceValue
//            }
//        }
//        _errorToast.postValue(Event(true))
//    }
//
//    //this will be used to notify the fragment to show a toast message
//
//
//    private val _errorToast = MutableLiveData<Event<Boolean>>()
//    val errorToast: LiveData<Event<Boolean>>
//        get() = _errorToast
//}

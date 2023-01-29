package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.work.*
import com.example.myapplication.databinding.ActivityCreatenewuserBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.utils.ProgressDialogHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var alertDialog : AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // onCreateOptionsMenu(R.menu.logout_menu)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alertDialog = this.let { ProgressDialogHelper.getDialog(it,layoutInflater) }

        val intent = Intent(this,Createnewuser::class.java)
        binding.btsignup.setOnClickListener {
             alertDialog?.show()
             intent.putExtra("operate","Signup")
             startActivity(intent)
             alertDialog?.dismiss()
        }
        binding.btloginid.setOnClickListener {
             alertDialog?.show()
             intent.putExtra("operate","Login")
             startActivity(intent)
             alertDialog?.dismiss()
        }
        binding.btadmin.setOnClickListener {
            alertDialog?.show()
            intent.putExtra("operate","admin")
            startActivity(intent)
            alertDialog?.dismiss()
        }


//        val workManager = WorkManager.getInstance(applicationContext)
//        val currentDate = Calendar.getInstance()
//        val lastDayOfMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
//        val repeatInterval = (lastDayOfMonth - currentDate.get(Calendar.DAY_OF_MONTH)) * 24 * 60 * 60 * 1000
//        val work = PeriodicWorkRequest.Builder(InterestCalculationWorker::class.java,repeatInterval.toLong(), TimeUnit.MILLISECONDS)
//            .build()
//        workManager.enqueue(work)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_item -> {
                // Perform logout action
                FirebaseAuth.getInstance().signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}


//class InterestCalculationWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
//
//    override suspend fun doWork(): Result {
//        // Fetch INTEREST_RATE from Firebase Realtime Database
//        val interestRate = fetchInterestRateFromFirebase()[0]
//
//        val currentDate = Calendar.getInstance()
//        val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
//        val lastDayOfMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
//        val interestRateForCurrentMonth = (interestRate/12) * (currentDay/lastDayOfMonth)
//        val chargeForCurrentMonth = (currentDay/lastDayOfMonth) * 1000
//
//        // Add interest to all accounts
//        for (account in accounts) {
//            if(account.nrv < MIN_NRV){
//                account.balance -= chargeForCurrentMonth
//            }
//            account.balance += account.balance * interestRateForCurrentMonth
//        }
//        return Result.success()
//    }
//
//    private suspend fun fetchInterestRateFromFirebase(): List<String> {
//        return withContext(Dispatchers.IO) {
//           val adminPath = FirebaseDatabase.getInstance().getReference("bank").child("")
//        }
//    }
//}

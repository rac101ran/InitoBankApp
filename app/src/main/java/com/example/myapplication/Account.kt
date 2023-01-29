package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityAccountBinding
import com.example.myapplication.databinding.FragmentCheckbalanceBinding
import com.example.myapplication.databinding.NavHeaderBinding
import com.example.myapplication.models.TransactionFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.internal.artificialFrame


class Account : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle
    lateinit var binding : ActivityAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        setSupportActionBar(binding.toolbar)

        binding.toolbar.setTitleTextColor(resources.getColor(R.color.white))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewHeader = binding.navView.getHeaderView(0)

        val navViewHeaderBinding : NavHeaderBinding = NavHeaderBinding.bind(viewHeader)

        val sharedPreferences : SharedPreferences = this.getSharedPreferences(this.packageName.toString(),Context.MODE_PRIVATE)

        val currentAccountType = intent.getStringExtra("type")?:sharedPreferences.getString("current_type_cache","")

        val currentAccount = intent.getStringExtra("accountid")?:sharedPreferences.getString("current_account_cache","")

        val currentAccountName = intent.getStringExtra("name")?:sharedPreferences.getString("current_name_cache","")

        sharedPreferences.edit().putString("current_type_cache",currentAccountType).apply()
        sharedPreferences.edit().putString("current_account_cache",currentAccount).apply()
        sharedPreferences.edit().putString("current_name_cache",currentAccountName).apply()

        supportActionBar?.title = currentAccountType

       // sharedPreferences.edit()sharedPreferences.getString("current_type_cache","".putString("current_type_cache",currentAccountType).apply()

        navViewHeaderBinding.tvaccountwindow.text = currentAccountName //intent.getStringExtra("name")?:sharedPreferences.getString("current_name_cache","")
        navViewHeaderBinding.tvaccounttype.text = "Your $currentAccountType Account"



        if(currentAccountType == "savings") {
             binding.navView.inflateMenu(R.menu.nav_drawer_menu)
        }else if(currentAccountType == "current") {
             binding.navView.inflateMenu(R.menu.navmenucurrentaccount)
        }else {
             // loan account
             binding.navView.inflateMenu(R.menu.loanaccountmenu)
        }





        binding.navView.setNavigationItemSelectedListener {
              when(it.itemId) {
                  R.id.mnwithdrawrmoney -> changeFeature(fragment = directCashFragment())
                  R.id.mncheckbalance -> changeFeature(fragment = checkbalanceFragment())
                  R.id.atmcard -> changeFeature(fragment = CreditCardFragment())
                  R.id.currentmenutransfermoney -> changeFeature(fragment = TransactionFragment())
                  R.id.passbookid -> changeFeature(fragment = PassbookFragment())
                  R.id.mnpassbook -> {  binding.toolbar.title = "My Passbook"
                      changeFeature(fragment = PassbookFragment()) }
                  R.id.mnreferralcode2 -> {
                       changeFeature(fragment = referralcodeFragment())
                  }
                  R.id.mnreferralcode -> {
                      changeFeature(fragment = referralcodeFragment())
                  }

                  R.id.logout -> {
                      FirebaseAuth.getInstance().signOut()
                      val intent = Intent(applicationContext,MainActivity::class.java)
                      startActivity(intent)
                  }
              }
            true
        }
    }

    private fun changeFeature(fragment: Fragment) {
        val fragmentManager = supportFragmentManager.beginTransaction()
        fragmentManager.replace(binding.framelayoutid.id,fragment)
        fragmentManager.commit()
        toggle.syncState()
        binding.drawerLayout.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected((item))) {
              return true
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentPassbookBinding
import com.example.myapplication.models.Event
import com.example.myapplication.utils.EventAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.appcompat.widget.Toolbar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PassbookFragment : Fragment() {


    private lateinit var binding : FragmentPassbookBinding
    private var currentAccount = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

            val sharedPreferences = context?.getSharedPreferences(context?.packageName.toString(), Context.MODE_PRIVATE)
            currentAccount = sharedPreferences?.getString("current_account_cache","").toString()
//            currentName = sharedPreferences?.getString("current_name_cache","").toString()
//            type = sharedPreferences?.getString("current_type_cache","").toString()
//
         activity?.actionBar?.title = "PassBook"
         binding = FragmentPassbookBinding.inflate(layoutInflater)
         return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)


        val list : MutableList<Event> = mutableListOf()
        val adapter = EventAdapter(list)
        binding.recyclerPassbook.adapter = adapter
        binding.recyclerPassbook.layoutManager = LinearLayoutManager(context)

          lifecycleScope.launch(Dispatchers.IO) {
               val events = FirebaseDatabase.getInstance().getReference("bank").child("accounts").child(currentAccount).child("events").get().await()

               if(events.hasChildren()) {
                     events.children.forEach {
                          if(it.key.equals("withdraw")) {
                               //list.add(it.key+"&"+it.child("amt").getValue(String::class.java)+"&"+it.child("desc"))
                               val everWithDrawn = it.hasChildren()
                               if(everWithDrawn) {
                                      it.children.forEach { w ->
                                           // list.add(w.key.toString() + "&" + w.child("description").getValue(String::class.java).toString() + "&" + w.child("amt").getValue(String::class.java).toString())
                                          val event = Event(timestamp = w.key.toString()
                                          , description = w.child("description").getValue(String::class.java).toString()
                                          , type = "-" , from = "-" , to = "-" , amount = w.child("amount").getValue(String::class.java).toString())
                                          list.add(event)
                                      }
                               }
                          }else {
                              val event = Event(timestamp = it.key.toString()
                                  , description = it.child("description").getValue(String::class.java).toString()
                                  , type = it.child("type").getValue(String::class.java).toString() , from = "-" , to = "-" , amount = it.child("amount").getValue(String::class.java).toString())

                              if(it.hasChild("from")) {
                                  event.from = it.child("from").getValue(String::class.java).toString()
                              }
                              if(it.hasChild("to")) {
                                  event.to = it.child("to").getValue(String::class.java).toString()
                              }
                              list.add(event)
                          }
                     }

                }
              withContext(Dispatchers.Main) {
                  list.sortWith( compareByDescending { it.timestamp })
                  val adapter = EventAdapter(list)
                  adapter.notifyDataSetChanged()
                  binding.recyclerPassbook.adapter = adapter
                  binding.recyclerPassbook.layoutManager = LinearLayoutManager(context)
              }
          }
    }

}
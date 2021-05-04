package com.sanjaysgangwar.smsboomber.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sanjaysgangwar.smsboomber.R
import com.sanjaysgangwar.smsboomber.databinding.ActivityOtherAppsBinding
import com.sanjaysgangwar.smsboomber.mUtils.Utils.isOnline
import com.sanjaysgangwar.smsboomber.mUtils.Utils.showToast
import com.sanjaysgangwar.smsboomber.mViewHolder.AppsViewHolder
import com.sanjaysgangwar.smsboomber.model.AppsModelClass
import com.squareup.picasso.Picasso

class OtherAppsActivity : AppCompatActivity() {

    private lateinit var bind: ActivityOtherAppsBinding
    private lateinit var myRef: DatabaseReference
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityOtherAppsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        initAllComponents()
    }

    private fun initAllComponents() {
        setUpToolbar()
        setUpRecycler()

    }

    private fun setUpRecycler() {
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Common")
                .child("apps")
        bind.recycler.layoutManager = GridLayoutManager(applicationContext, 2)
    }

    private fun setUpToolbar() {
        bind.toolbar.title = "Other Apps"
        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        if (isOnline(applicationContext)) {
            showToast(applicationContext, "Please Wait ")
            initRecycler()
        } else {
            onBackPressed()
            showToast(applicationContext, "No Internet Connection")
        }
    }

    private fun initRecycler() {
        val option: FirebaseRecyclerOptions<AppsModelClass> =
                FirebaseRecyclerOptions.Builder<AppsModelClass>()
                        .setQuery(myRef.orderByChild("name"), AppsModelClass::class.java)
                        .build()
        val recyclerAdapter =
                object : FirebaseRecyclerAdapter<AppsModelClass, AppsViewHolder>(option) {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
                        val view =
                                LayoutInflater.from(applicationContext)
                                        .inflate(R.layout.show_all_apps, parent, false)
                        return AppsViewHolder(view)
                    }

                    override fun onBindViewHolder(
                            holder: AppsViewHolder,
                            position: Int,
                            model: AppsModelClass
                    ) {
                        holder.name.text = model.name
                        holder.quote.text = model.quote
                        Picasso.get()
                                .load(model.image)
                                .into(holder.image)
                        holder.card.setOnClickListener {
                            operationToPerform(model.samsung)

                        }
                    }


                }

        bind.recycler.adapter = recyclerAdapter
        recyclerAdapter.startListening()
    }

    private fun operationToPerform(link: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        } catch (exception: Exception) {
            showToast(applicationContext, "Coming soon")
        }

    }
}
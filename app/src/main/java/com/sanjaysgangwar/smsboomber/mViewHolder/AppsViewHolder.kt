package com.sanjaysgangwar.smsboomber.mViewHolder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sanjaysgangwar.smsboomber.R

class AppsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    var install: Button = itemView!!.findViewById(R.id.install)
    var quote: TextView = itemView!!.findViewById(R.id.quote)
    var name: TextView = itemView!!.findViewById(R.id.appName)
    var image: ImageView = itemView!!.findViewById(R.id.image)
    var card: CardView = itemView!!.findViewById(R.id.card)

}


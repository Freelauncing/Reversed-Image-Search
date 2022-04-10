package com.reversedimagesearched.reverseimageresult

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.reversedimagesearched.R
import com.reversedimagesearched.data.model.CommonResponse
import com.reversedimagesearched.util.Utility


class ReverseImagesRecyclerViewAdapter(
    val reverImageList: ArrayList<CommonResponse>,
    context: Context
) :
    RecyclerView.Adapter<ReverseImagesRecyclerViewAdapter.MyViewHolder>() {

    var cxt: Context

    init {
        cxt = context
    }

    fun swapList(mreverImageList: ArrayList<CommonResponse>){
        reverImageList.clear()
        Log.v("CHEKOO=>", mreverImageList.size.toString())
        reverImageList.addAll(mreverImageList)
        Log.v("CHEKOO=>", reverImageList.size.toString())
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_products, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reverImageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = reverImageList.get(position)

        holder.floatingActionButton.setOnClickListener {

        }

        holder.image.setOnClickListener {
            val imagePopup = ImagePopup(cxt)
            imagePopup.initiatePopupWithGlide(currentItem.image_link) // Load Image from Drawable
            imagePopup.viewPopup();
        }

        Glide.with(cxt)
            .load(currentItem.image_link.toUri())
            .centerCrop()
            .into(holder.image)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var floatingActionButton:ImageButton

        init {
            image = itemView.findViewById(R.id.image)
            floatingActionButton = itemView.findViewById(R.id.floatingActionButton)
        }
    }
}
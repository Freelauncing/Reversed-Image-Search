package com.reversedimagesearched.allsavedresults

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.reversedimagesearched.R
import com.reversedimagesearched.data.database.DatabaseModel
import com.reversedimagesearched.data.database.ReverseDbHelper
import com.reversedimagesearched.reverseimageresult.ReverseImageResultViewModel


class AllResultsAdapter(
val reverImageList: ArrayList<DatabaseModel>,
val viewModel: AllResultsViewModel,
context: Context
) :
RecyclerView.Adapter<AllResultsAdapter.MyViewHolder>() {

    var cxt: Context

    init {
        cxt = context
    }

    fun swapList(mreverImageList: ArrayList<DatabaseModel>){
        reverImageList.clear()
        Log.v("CHEKOO=>", mreverImageList.size.toString())
        reverImageList.addAll(mreverImageList)
        Log.v("CHEKOO=>", reverImageList.size.toString())
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_result, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reverImageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = reverImageList.get(position)

        holder.floatingActionButton.setOnClickListener {
            viewModel.deleteItem(currentItem._id)
        }

        holder.image.setOnClickListener {
            val imagePopup = ImagePopup(cxt)
            imagePopup.initiatePopupWithGlide(currentItem.downloadedImageUrl) // Load Image from Drawable
            imagePopup.viewPopup();
        }

        holder.realimage.setOnClickListener {
            val imagePopup = ImagePopup(cxt)
            imagePopup.initiatePopupWithGlide(currentItem.realimageurl) // Load Image from Drawable
            imagePopup.viewPopup();
        }

        val bmp = BitmapFactory.decodeByteArray(currentItem.downloadedImage, 0, currentItem.downloadedImage.size)
        holder.image.setImageBitmap(bmp)

        val bmp1 = BitmapFactory.decodeByteArray(currentItem.realimage, 0, currentItem.realimage.size)
        holder.realimage.setImageBitmap(bmp1)

    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var realimage: ImageView
        var floatingActionButton: ImageButton

        init {
            image = itemView.findViewById(R.id.image)
            realimage = itemView.findViewById(R.id.realimage)
            floatingActionButton = itemView.findViewById(R.id.floatingActionButton)
        }
    }


}
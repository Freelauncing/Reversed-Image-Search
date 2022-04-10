package com.reversedimagesearched.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.reversedimagesearched.R
import com.reversedimagesearched.databinding.FragmentHomeBinding
import com.reversedimagesearched.util.EventObserver
import com.reversedimagesearched.util.Utility
import com.reversedimagesearched.util.setupSnackbar
import kotlinx.android.synthetic.main.custom_choice_dialogue.view.*
import java.io.File


class HomeFragment : Fragment() {

    val LOG_TAG:String = "AddProductFragment"
    val AUTHORITY:String = "com.reversedimagesearched.home.HomeFragment"

    private lateinit var viewDataBinding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>{
        AddProductViewModelFactory()
    }

    companion object{
        var Uploaded_Image_Url = ""
    }

    var photoFile: File? = null

    private val pickImageFromGallery_Code = 100
    private val pickImageFromCamera_Code = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewDataBinding = FragmentHomeBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
        setUpListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpListeners() {
        viewModel.openChoiceDialogue.observe(viewLifecycleOwner, EventObserver {
            showImageChoiceDialogue()
        })
//        viewModel.clearImage.observe(viewLifecycleOwner,EventObserver{
//            viewDataBinding.selectedImage.setImageResource(0)
//        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    override fun onResume() {
        super.onResume()
        if(!viewModel.snackbarText.hasActiveObservers())
            setupSnackbar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showImageChoiceDialogue(){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.custom_choice_dialogue, null)
        dialogBuilder.setView(dialogView)

        val btn_camera = dialogView.btn_camera
        val btn_gallery = dialogView.btn_gallery
        val txt_dialog_title = dialogView.txt_dialog_content
        val alertDialog = dialogBuilder.create()
        txt_dialog_title.setText("Choose Image From")

        btn_camera.setOnClickListener {
            alertDialog.dismiss()
            onLaunchCamera()
        }

        btn_gallery.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageFromGallery_Code)
        }

        alertDialog.show()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImageFromGallery_Code) {
            var imageUri: Uri? = data?.data
            val uri = Utility.readUriImage(requireContext(),imageUri!!)
            viewDataBinding.selectedImage.setImageURI(uri)
            viewModel.setProductImageUri(uri!!)
        }
        if (requestCode === pickImageFromCamera_Code && resultCode === Activity.RESULT_OK) {
            // by this point we have the camera photo on disk
            val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            viewModel.setProductImageUri(photoFile!!.toUri())

            // viewDataBinding.selectedImage.setImageBitmap(takenImage)
            viewDataBinding.selectedImage.setImageBitmap(Utility.rotateImageIfRequired(takenImage,photoFile!!.toUri()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile =  Utility.createFileForImage(requireContext())
        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, pickImageFromCamera_Code)
            }
        }
    }
}
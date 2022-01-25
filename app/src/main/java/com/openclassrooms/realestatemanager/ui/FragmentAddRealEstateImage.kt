package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddRealEstateImageBinding


import com.openclassrooms.realestatemanager.utils.PermissionHelper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FragmentAddRealEstateImage : Fragment() {

    private val REQUEST_CODE_TAKE_PHOTO = 200
    private val REQUEST_CODE_EXTERNAL_STORAGE = 400
    private var _binding: FragmentAddRealEstateImageBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var currentPhotoPath: String
    private var listOfPictureUri = ArrayList<Uri>()
    private var currentCategory: String? = null
    private var linearLayoutPhoto: LinearLayoutCompat? = null
    private var city: String? = null
    private var type: String? = null
    private var price: String? = null
    private var state: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRealEstateImageBinding.inflate(inflater, container, false)
        val view = mBinding.root
        linearLayoutPhoto = mBinding.fragmentAddRealEstateLinearLayoutHorizontal
        linearLayoutPhoto?.addView(this.addImageAddPhoto())
        val args = arguments
        city = args?.get("city") as String?
        type = args?.get("type") as String?
        price = args?.get("price") as String?
        state = args?.get("state") as String?
        this.configureListeners()
        return view
    }

    private fun configureListeners() {
        mBinding.fragmentAddRealEstateImageCreateButton.setOnClickListener {
            this.getImages()
        }
    }

    private fun getImages() {
        val replyIntent = Intent()
        replyIntent.putExtra("price", price)
        replyIntent.putExtra("type", type)
        replyIntent.putExtra("city", city)
        replyIntent.putExtra("state", state)
        replyIntent.putExtra("photos", listOfPictureUri[0].toString())
        requireActivity().setResult(Activity.RESULT_OK, replyIntent)
        requireActivity().finish()
    }

    private fun verifyPermissionsForCamera() {
        val permission = PermissionHelper(requireActivity())
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permission.askForPermissions(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            this.capturePhoto()
        }
    }
    private fun verifyPermissionForExternalData() {
        val permission = PermissionHelper(requireActivity())
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permission.askForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            this.searchPhoto(REQUEST_CODE_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri: Uri? = null
        val imageView = ImageView(requireContext())
        try {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                uri = imageUri
            }
        } catch (exception: Exception) {
            exception.stackTrace
        }
        if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    uri = data.clipData!!.getItemAt(i).uri
                }
            } else if (data?.data != null) {
                uri = data.data!!
            }
        }
        if (uri != null) {
            listOfPictureUri.add(uri!!)
            glideImage(uri, imageView)
            addImageOnLayout(imageView)
        }

    }

    private fun glideImage(uri: Uri, imageView: ImageView) {
        Glide.with(mBinding.root)
            .load(uri)
            .centerCrop()
            .into(imageView)
    }

    private fun addImageOnLayout(imageView: ImageView) {
        if (linearLayoutPhoto?.size == 2) {
            linearLayoutPhoto?.removeViewAt(1)
            linearLayoutPhoto?.addView(imageView, 500, 500)
            this.addLinearLayout(imageView, 500, 500)
        } else {
            linearLayoutPhoto?.removeViewAt(0)
            linearLayoutPhoto?.addView(imageView, 500, 500)
            linearLayoutPhoto?.addView(this.addImageAddPhoto())
        }
    }

    private fun addLinearLayout(imageView: ImageView, width: Int, height: Int) {
        val imageLinearLayout = addLayout()
        val params = LinearLayoutCompat.LayoutParams(width, height)
        imageView.layoutParams = params
        linearLayoutPhoto = imageLinearLayout
        linearLayoutPhoto?.addView(addImageAddPhoto())
        mBinding.fragmentAddRealEstateLinearLayoutVertical.addView(imageLinearLayout)
    }

    private fun addTextView(): TextView {
        val categoryTitle = TextView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        categoryTitle.layoutParams = layoutParams
        categoryTitle.text = currentCategory
        return categoryTitle
    }

    private fun addImageAddPhoto(): ImageView {
        val addImage = ImageView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(250, 250)
        layoutParams.gravity = Gravity.CENTER
        addImage.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_add_a_photo_24))
        addImage.layoutParams = layoutParams
        addImage.setOnClickListener {
            showPhotoDialog()
        }
        return addImage
    }

    private fun addLayout(): LinearLayoutCompat {
        val linearLayout = LinearLayoutCompat(requireContext())
        linearLayout.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayoutCompat.HORIZONTAL
        return linearLayout
    }

    private fun capturePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)
        val photoFile: File? = try {
            createImageFile()
        } catch (exception: IOException) {
            null
        }
        photoFile?.also {
            imageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.openclassrooms.realestatemanager.fileProvider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRENCH).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun searchPhoto(requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun showCategoryDialog() {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.category_dialog_box)
        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).setOnClickListener {
            currentCategory = dialog.findViewById<EditText>(R.id.category_dialog_box_input).text.toString()
            dialog.dismiss()
//            addLinearLayout(imageView, 500, 500)
        }
        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showPhotoDialog() {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.category_dialog_box)
        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).text = getString(R.string.category_dialog_box_photo_btn)
        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).text = getString(R.string.category_dialog_box_data_btn)
        dialog.findViewById<TextView>(R.id.category_dialog_box_title).text = getString(R.string.category_dialog_box_title_photo)
        dialog.findViewById<TextView>(R.id.category_dialog_box_input).visibility = View.GONE

        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).setOnClickListener {
            verifyPermissionsForCamera()
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).setOnClickListener {
            verifyPermissionForExternalData()
            dialog.dismiss()
        }
        dialog.show()
    }
}
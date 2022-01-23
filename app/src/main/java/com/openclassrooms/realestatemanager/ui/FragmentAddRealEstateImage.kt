package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddRealEstateImageBinding
import com.openclassrooms.realestatemanager.utils.PermissionHelper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FragmentAddRealEstateImage : Fragment() {

    private val REQUEST_CODE_TAKE_PHOTO = 200
    private val REQUEST_CODE_EXTERNAL_STORAGE = 400
    private val REQUEST_CODE_EXTERNAL_STORAGE_HORIZONTAL = 600
    private var _binding: FragmentAddRealEstateImageBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var currentPhotoPath: String
    private var listOfPictureUri = ArrayList<Uri>()
    private val mapOfPictureUri = HashMap<String, List<Uri>>()
    private var linearLayoutId: Int? = null
    private lateinit var mAdapter: FragmentAddRealEstateAdapter
    private lateinit var mRecyclerView: RecyclerView
    private var currentCategory: String? = null
    private var linearLayoutPhoto: LinearLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRealEstateImageBinding.inflate(inflater, container, false)
        val view = mBinding.root
        this.configureListeners()
        return view
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

    private fun configureListeners() {
        mBinding.fragmentAddRealEstateImageAddMedia.setOnClickListener {
            showphotodialog()
        }

        mBinding.fragmentAddRealEstateImageAddCategory.setOnClickListener {
            showCategoryDialog()
        }
    }

    private fun addImageInCategory() {
        mapOfPictureUri[currentCategory!!] = listOfPictureUri
        listOfPictureUri.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                listOfPictureUri.add(imageUri)
                val imageView = ImageView(requireContext())
                imageView.setImageURI(imageUri)
//                this.addView(imageView, 500, 500)
                linearLayoutPhoto?.addView(imageView, 500, 500)
            }
        } catch (exception: Exception) {
            exception.stackTrace
        }
        if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    val uri: Uri = data.clipData!!.getItemAt(i).uri
                    listOfPictureUri.add(uri)
                    val imageView = ImageView(requireContext())
                    imageView.setImageURI(uri)
//                    this.addView(imageView, 500, 500)
                    linearLayoutPhoto?.addView(imageView, 500, 500)
                }
            } else if (data?.data != null) {
                val uri: Uri = data.data!!
                listOfPictureUri.add(uri)
//                val imageView = ImageView(requireContext())
//                imageView.setImageURI(uri)
//                this.addView(imageView, 500, 500)
            }
        }

        if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE_HORIZONTAL && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    val uri: Uri = data.clipData!!.getItemAt(i).uri
                    listOfPictureUri.add(uri)
                    val imageView = ImageView(requireContext())
                    imageView.setImageURI(uri)
//                    this.addLinearLayout(imageView, 500, 500)
                    linearLayoutPhoto?.addView(imageView, 500, 500)
                }
            } else if (data?.data != null) {
                val uri: Uri = data.data!!
                listOfPictureUri.add(uri)
                val imageView = ImageView(requireContext())
                imageView.setImageURI(uri)
//                if (linearLayoutId == linearLayoutPhoto?.id) {
                    linearLayoutPhoto?.addView(imageView, 500, 500)
//                }
//                this.addLinearLayout(imageView, 500, 500)
            }
        }
        mAdapter = FragmentAddRealEstateAdapter(listOfPictureUri)
        mRecyclerView.adapter = mAdapter
    }

    private fun addView(imageView: ImageView, width: Int, height: Int) {
        val params = LinearLayout.LayoutParams(width, height)
        imageView.layoutParams = params
        if (linearLayoutId != null) run {
            mAdapter = FragmentAddRealEstateAdapter(listOfPictureUri)
            mRecyclerView.adapter = mAdapter
        }
    }

    private fun addLinearLayout() {
        addRecyclerview()
        mAdapter = FragmentAddRealEstateAdapter(listOfPictureUri)
        mRecyclerView.adapter = mAdapter
        val titleLinearLayout = addLayout()
        val imageLinearLayout = addLayout()
        imageLinearLayout.tag = currentCategory
        titleLinearLayout.addView(addTextView())
        imageLinearLayout.addView(mRecyclerView)
        linearLayoutPhoto = imageLinearLayout
        mBinding.fragmentAddRealEstateLinearLayout.addView(titleLinearLayout)
        mBinding.fragmentAddRealEstateLinearLayout.addView(imageLinearLayout)
    }

    private fun addRecyclerview() {
        mRecyclerView = RecyclerView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 550)
        mRecyclerView.layoutParams = layoutParams
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun addTextView(): TextView {
        val categoryTitle = TextView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        categoryTitle.layoutParams = layoutParams
        categoryTitle.text = currentCategory
        return categoryTitle
    }

    private fun addLayout(): LinearLayout {
        val linearLayout = LinearLayout(requireContext())
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayoutId = System.currentTimeMillis().toInt()
        linearLayout.id = linearLayoutId as Int
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
            addImageInCategory()
            addLinearLayout()
        }
        dialog.show()
    }

    private fun showphotodialog() {
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
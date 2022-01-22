package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.FragmentAddRealEstateBinding
import com.openclassrooms.realestatemanager.utils.PermissionHelper
import com.openclassrooms.realestatemanager.utils.URIPathHelper
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

class FragmentAddRealEstate : Fragment() {

    private val REQUEST_CODE_TAKE_PHOTO = 200
    private val REQUEST_CODE_EXTERNAL_STORAGE = 400
    private var _binding: FragmentAddRealEstateBinding? = null
    private val mBinding get() = _binding!!
    private val listOfPictureUri = ArrayList<Uri>()
    private lateinit var adapter: FragmentAddRealEstateAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageUri: Uri
    private lateinit var currentPhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRealEstateBinding.inflate(inflater, container, false)
        val view = mBinding.root
        this.configureListener()
        this.configureRecyclerView()
        return view
    }

    private fun configureListener() {
        val permission = PermissionHelper(requireActivity())
        mBinding.createButton.setOnClickListener{
            this.getInformation()
        }
        mBinding.takePhotoBtn.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permission.askForPermissions(Manifest.permission.CAMERA)
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                this.capturePhoto()
            }
        }

        mBinding.imageDataBtn.setOnClickListener{
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permission.askForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                this.searchPhoto()
            }
        }
    }

    private fun configureRecyclerView() {
        recyclerView = mBinding.fragmentAddPictureRecyclerView
        adapter = FragmentAddRealEstateAdapter(listOfPictureUri)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun searchPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_EXTERNAL_STORAGE)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun capturePhoto() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PHOTO)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                listOfPictureUri.add(imageUri)
            }
        } catch (exception: Exception) {
            exception.stackTrace
        }
        if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount

                for (i in 0 until count) {
                    var uri: Uri = data.clipData!!.getItemAt(i).uri
                    listOfPictureUri.add(uri)
                }
            } else if (data?.data != null) {
                val uri: Uri = data.data!!
                listOfPictureUri.add(uri)
            }
        }
        adapter = FragmentAddRealEstateAdapter(listOfPictureUri)
        recyclerView.adapter = adapter
    }

    private fun getInformation() {
        val replyIntent = Intent()
        if (TextUtils.isEmpty(mBinding.address.text)) {
            requireActivity().setResult(Activity.RESULT_CANCELED, replyIntent)
        } else {
            val city = mBinding.address.text.toString()
            val price = mBinding.price.text.toString()
            val type = mBinding.property.text.toString()


            replyIntent.putExtra("city", city)
            replyIntent.putExtra("price", price)
            replyIntent.putExtra("type", type)
            requireActivity().setResult(Activity.RESULT_OK, replyIntent)
        }
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
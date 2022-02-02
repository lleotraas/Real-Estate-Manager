package com.openclassrooms.realestatemanager.ui.add


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddImageBinding
import kotlinx.coroutines.launch

class FragmentAddImage : Fragment() {

    private var _binding: FragmentAddImageBinding? = null
    private val mBinding get() = _binding!!
    private var listOfPictureUri = ArrayList<String>()
    private var listOfCategory = ArrayList<String>()
    private var city: String? = null
    private var type: String? = null
    private var price: String? = null
    private var state: String? = null
    private var staticMap: ByteArray? = null

    private lateinit var internalStoragePhotoAdapter: InternalStoragePhotoAdapter
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private var cameraPermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddImageBinding.inflate(inflater, container, false)
        val view = mBinding.root
        val args = arguments
        city = args?.get("city") as String?
        type = args?.get("type") as String?
        price = args?.get("price") as String?
        state = args?.get("state") as String?
        staticMap = args?.get("static_map") as ByteArray?

        internalStoragePhotoAdapter = InternalStoragePhotoAdapter {
            lifecycleScope.launch {
                internalStoragePhotoAdapter.onPhotoClick = {

//                    deletePhotoFromPhotosSelection(it)
                    setupInternalStorageRecyclerView()
                    loadPhotosSelectionIntoRecyclerView()
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_deleted), Toast.LENGTH_SHORT).show()
                }
            }
        }
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
            cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: cameraPermissionGranted
        }

        updateOrRequestPermission()
        setupInternalStorageRecyclerView()
        this.configureListeners()
        return view
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    listOfPictureUri.add(fileUri.toString())
                    internalStoragePhotoAdapter.submitList(listOfPictureUri)
                    setupInternalStorageRecyclerView()
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun configureListeners() {
        mBinding.fragmentAddImageCreateButton.setOnClickListener {
            this.getImages()
        }
        mBinding.fragmentAddRealEstateImageTakePhoto.setOnClickListener {
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(1080,1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
        }
    }

    private fun getImages() {
        val replyIntent = Intent()
        replyIntent.putExtra("price", price)
        replyIntent.putExtra("type", type)
        replyIntent.putExtra("city", city)
        replyIntent.putExtra("state", state)
        replyIntent.putExtra("static_map", staticMap)
//        replyIntent.putExtra("categories", listOfCategory)
        replyIntent.putStringArrayListExtra("photos", listOfPictureUri)
        requireActivity().setResult(RESULT_OK, replyIntent)
        requireActivity().finish()
    }

    private fun updateOrRequestPermission() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29
        cameraPermissionGranted = hasCameraPermission

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!cameraPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun setupInternalStorageRecyclerView() = mBinding.fragmentAddImageYourPhotoRv.apply {
        adapter = internalStoragePhotoAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun loadPhotosSelectionIntoRecyclerView() {
        internalStoragePhotoAdapter.submitList(listOfPictureUri)
        mBinding.fragmentAddImageYourPhotoRv.adapter = internalStoragePhotoAdapter
    }

//    private fun showPhotoDialog() {
//        val dialog = MaterialDialog(requireContext())
//            .customView(R.layout.category_dialog_box)
//        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).text = getString(R.string.category_dialog_box_photo_btn)
//        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).text = getString(R.string.category_dialog_box_data_btn)
//        dialog.findViewById<TextView>(R.id.category_dialog_box_title).text = getString(R.string.category_dialog_box_title_photo)
//        dialog.findViewById<EditText>(R.id.category_dialog_box_spinner).setOnClickListener{
//            dialog.findViewById<EditText>(R.id.category_dialog_box_spinner).setText(setCategory())
//        }
//
//        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
}
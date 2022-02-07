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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentAddImageBinding
import com.openclassrooms.realestatemanager.dependency.RealEstateApplication
import com.openclassrooms.realestatemanager.model.RealEstate
import kotlinx.coroutines.launch

class FragmentAddImage : Fragment() {

    private var currentRealEstate: RealEstate? = null
    private var _binding: FragmentAddImageBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: AddViewModel by viewModels {
        RealEstateViewModelFactory(
            (requireActivity().application as RealEstateApplication).realEstateRepository,
            (requireActivity().application as RealEstateApplication).realEstateImageRepository)
    }
    private var listOfPictureUri = ArrayList<String>()
    private var address: String? = null

    private lateinit var addImagedAdapter: AddImagedAdapter
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
        address = args?.get("address") as String?


        addImagedAdapter = AddImagedAdapter {
            lifecycleScope.launch {
                addImagedAdapter.onPhotoClick = {
                    deletePhotoFromPhotosSelection(it)
                    setupImageSelectedRecyclerView()
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
        mViewModel.getRealEstateByAddress(address!!).observe(viewLifecycleOwner) { realEstate ->
            currentRealEstate = realEstate
        }
        updateOrRequestPermission()
        setupImageSelectedRecyclerView()
        if (savedInstanceState != null) {
            listOfPictureUri = savedInstanceState.getStringArrayList(BUNDLE_STATE_LIST_OF_PHOTO) as ArrayList<String>
            loadPhotosSelectionIntoRecyclerView()
        }
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
                    addImagedAdapter.submitList(listOfPictureUri)
                    setupImageSelectedRecyclerView()
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
        requireActivity().setResult(RESULT_OK, replyIntent)
        //TODO see when there isn't list of image is empty.
        currentRealEstate!!.picture = listOfPictureUri
        mViewModel.update(currentRealEstate!!)
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

    private fun setupImageSelectedRecyclerView() = mBinding.fragmentAddImageYourPhotoRv.apply {
        adapter = addImagedAdapter
        layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
    }

    private fun loadPhotosSelectionIntoRecyclerView() {
        addImagedAdapter.submitList(listOfPictureUri)
        mBinding.fragmentAddImageYourPhotoRv.adapter = addImagedAdapter
    }

    private fun deletePhotoFromPhotosSelection(photo: String) {
        listOfPictureUri.remove(photo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(BUNDLE_STATE_LIST_OF_PHOTO, listOfPictureUri)
    }

    companion object {
        const val BUNDLE_STATE_LIST_OF_PHOTO = "BUNDLE_STATE_LIST_OF_PHOTO"
    }
}
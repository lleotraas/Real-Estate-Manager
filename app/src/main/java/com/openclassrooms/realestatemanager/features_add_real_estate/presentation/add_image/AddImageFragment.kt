package com.openclassrooms.realestatemanager.features_add_real_estate.presentation.add_image


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddPhotoBinding
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstate
import com.openclassrooms.realestatemanager.features_real_estate.domain.model.RealEstatePhoto
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.PlaceholderContent
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.ADDRESS
import com.openclassrooms.realestatemanager.features_real_estate.data.utils.UtilsKt.Companion.ID
import com.openclassrooms.realestatemanager.features_real_estate.presentation.ItemDetailHostActivity
import com.openclassrooms.realestatemanager.features_real_estate.presentation.RealEstateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddImageFragment : Fragment() {

    private var currentRealEstate: RealEstate? = null
    private var _binding: FragmentAddPhotoBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: RealEstateViewModel by viewModels()
    private var listOfRealEstatePhoto = ArrayList<RealEstatePhoto>()
    private var address: String? = null
    private var id: Long? = null

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
        _binding = FragmentAddPhotoBinding.inflate(inflater, container, false)
        val view = mBinding.root
        val args: MutableMap<String, PlaceholderContent.PlaceholderItem> = PlaceholderContent.ITEM_MAP
        if(args.containsKey(ID)) {
            id = args[ID].toString().toLong()
        }
        if (args.containsKey(ADDRESS)) {
            address = args[ADDRESS].toString()
        }

        addImagedAdapter = AddImagedAdapter {
            lifecycleScope.launch {
                addImagedAdapter.onPhotoClickDelete = { realEstatePhoto ->
                    deletePhotoFromPhotosSelection(realEstatePhoto)
                    deletePhotoFromDatabase(realEstatePhoto.id)
                    setupImageSelectedRecyclerView()
                    loadPhotosSelectionIntoRecyclerView()
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_photo_deleted), Toast.LENGTH_SHORT).show()
                }
            }
        }
        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
            cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: cameraPermissionGranted
        }
        if (address != null) {
            lifecycleScope.launch {
                Log.e(javaClass.simpleName, "onCreateView: address:$address", )
                mViewModel.getRealEstateByAddress(address!!).collect { realEstate ->
                    Log.e(javaClass.simpleName, "onCreateView: address:${realEstate.address} id:${realEstate.id}")
                    currentRealEstate = realEstate
                    id = realEstate.id
                }
            }
        }
        if (savedInstanceState != null) {
            id = savedInstanceState.getLong(BUNDLE_STATE_ID)
        }
        if (id != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.getRealEstateById(id!!).collect { realEstate ->
                        if (currentRealEstate == null) {
                            currentRealEstate = realEstate
                        }
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.getAllRealEstatePhoto(id!!).collect { realEstatePhotos ->
                        if (listOfRealEstatePhoto.isEmpty()) {
                            listOfRealEstatePhoto.addAll(realEstatePhotos)
                            setupImageSelectedRecyclerView()
                            loadPhotosSelectionIntoRecyclerView()

                        }

                    }
            }
            loadPhotosSelectionIntoRecyclerView()
            }
        }
        this.updateOrRequestPermission()
        this.setupImageSelectedRecyclerView()
        this.configureListeners()
        this.configureSupportNavigateUp()
        return view
    }

    private fun configureSupportNavigateUp() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_add_photo_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.menu_save_property -> {
                        updateRealEstate()
                        updateListOfRealEstatePhoto()
                    }
                    else -> {
                        PlaceholderContent.ITEM_MAP[ID] = PlaceholderContent.PlaceholderItem(ID, id.toString(), "")
                        findNavController().navigate(R.id.navigate_from_add_image_to_add_information)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
        if (!isTablet) {
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            (activity as ItemDetailHostActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private val startForImagePickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    val realEstatePhoto = RealEstatePhoto(
                        0,
                        currentRealEstate!!.id,
                        fileUri.toString(),
                        ""
                    )
                    lifecycleScope.launch {
                        val id = mViewModel.insertPhoto(realEstatePhoto)
                        realEstatePhoto.id = id
                        listOfRealEstatePhoto.add(realEstatePhoto)
                        loadPhotosSelectionIntoRecyclerView()
                    }
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
        mBinding.fragmentAddRealEstateImageTakePhoto.setOnClickListener {
                ImagePicker.with(this)
                    .compress(1024)
                    .maxResultSize(1080,1080)
                    .createIntent { intent ->
                        startForImagePickerResult.launch(intent)
                    }
        }
    }

    private fun updateRealEstate() {
        val replyIntent = Intent()
        requireActivity().setResult(RESULT_OK, replyIntent)
        currentRealEstate!!.picture = if(listOfRealEstatePhoto.isNotEmpty()) listOfRealEstatePhoto[0].photo else ""
        currentRealEstate!!.pictureListSize = listOfRealEstatePhoto.size
        lifecycleScope.launch {
            mViewModel.updateRealEstate(currentRealEstate!!)
        }
        findNavController().navigate(R.id.navigate_from_add_image_to_list)
    }

    private fun updateListOfRealEstatePhoto() {
        lifecycleScope.launch {
            for (realEstatePhoto in listOfRealEstatePhoto) {
                if (realEstatePhoto.category.isNotEmpty()) {
                    mViewModel.updateRealEstatePhoto(realEstatePhoto)
                }
            }
        }
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
        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
        layoutManager = if (isTablet) {
            StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        } else {
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        }
    }

    private fun loadPhotosSelectionIntoRecyclerView() {
        addImagedAdapter.submitList(listOfRealEstatePhoto)
        mBinding.fragmentAddImageYourPhotoRv.adapter = addImagedAdapter
    }

    private fun deletePhotoFromPhotosSelection(realEstatePhoto: RealEstatePhoto) {
        listOfRealEstatePhoto.remove(realEstatePhoto)
    }

    private fun deletePhotoFromDatabase(id: Long) {
        lifecycleScope.launch { mViewModel.deleteRealEstatePhoto(id) }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.fragment_add_photo_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.menu_save_property) {
//            this.updateRealEstate()
//            this.updateListOfRealEstatePhoto()
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(BUNDLE_STATE_ID, currentRealEstate!!.id)
    }

    companion object {
        const val BUNDLE_STATE_ID = "id"
    }
}
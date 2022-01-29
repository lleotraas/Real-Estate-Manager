package com.openclassrooms.realestatemanager.ui


import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.getItemSelector
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentAddRealEstateImageBinding
import com.openclassrooms.realestatemanager.model.InternalStoragePhoto
import com.openclassrooms.realestatemanager.model.SharedStoragePhoto
import com.openclassrooms.realestatemanager.utils.PermissionHelper
import com.openclassrooms.realestatemanager.utils.sdk29AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FragmentAddRealEstateImage : Fragment() {

    private var _binding: FragmentAddRealEstateImageBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var currentPhotoPath: String
    private var listOfPictureUri = ArrayList<String>()
    private var listOfCategory = ArrayList<String>()
    private var linearLayoutPhoto: LinearLayoutCompat? = null
    private var city: String? = null
    private var type: String? = null
    private var price: String? = null
    private var state: String? = null

    private lateinit var internalStoragePhotoAdapter: InternalStoragePhotoAdapter
    private lateinit var externalStoragePhotoAdapter: SharedPhotoAdapter
    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var contentObserver: ContentObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRealEstateImageBinding.inflate(inflater, container, false)
        val view = mBinding.root
//        linearLayoutPhoto = mBinding.fragmentAddRealEstateLinearLayoutHorizontal
//        linearLayoutPhoto?.addView(this.addImageAddPhoto())
//        val args = arguments
//        city = args?.get("city") as String?
//        type = args?.get("type") as String?
//        price = args?.get("price") as String?
//        state = args?.get("state") as String?
        internalStoragePhotoAdapter = InternalStoragePhotoAdapter {
            lifecycleScope.launch {
                val isDeletionSuccessful = deletePhotoFromInternalStorage(it.name)
                if (isDeletionSuccessful) {
                    loadPhotosFromInternalStorageIntoRecyclerView()
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_deleted), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_deletion_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }

        externalStoragePhotoAdapter = SharedPhotoAdapter {
            lifecycleScope.launch {
                deletePhotoFromExternalStorage(it.contentUri)
            }
        }
        setupExternalStorageRecyclerView()
        initContentObserver()

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if (readPermissionGranted) {
                loadPhotosFromExternalStorageIntoRecyclerView()
            } else {
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_read_file_without_permission), Toast.LENGTH_SHORT).show()
            }
        }
        updateOrRequestPermission()

        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_deleted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_deletion_failed), Toast.LENGTH_SHORT).show()
            }
        }

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            lifecycleScope.launch {
                val isPrivate = mBinding.switchPrivate.isChecked
                val isSavedSuccessfully = when {
                    isPrivate -> savePhotoToInternalStorage(UUID.randomUUID().toString(), it!!)
                    writePermissionGranted -> savePhotoToExternalStorage(UUID.randomUUID().toString(), it!!)
                    else -> false
                }
                if (isPrivate) {
                    loadPhotosFromInternalStorageIntoRecyclerView()
                }
                if (isSavedSuccessfully) {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_saved), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), requireContext().resources.getString(R.string.fragment_add_real_estate_image_photo_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
        mBinding.fragmentAddRealEstateImageTakePhoto.setOnClickListener {
            takePhoto.launch()
        }
        setupInternalStorageRecyclerView()
        loadPhotosFromInternalStorageIntoRecyclerView()
        loadPhotosFromExternalStorageIntoRecyclerView()
//        this.configureListeners()
        return view
    }

    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if (readPermissionGranted) {
                    loadPhotosFromExternalStorageIntoRecyclerView()
                }
            }
        }
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    private suspend fun loadPhotosFromExternalStorage(): List<SharedStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )

            val photos = mutableListOf<SharedStoragePhoto>()
            requireContext().contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(SharedStoragePhoto(id, displayName, width, height, contentUri))
                }
                photos.toList()
            } ?: listOf()
        }
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
        replyIntent.putExtra("categories", listOfCategory)
        replyIntent.putStringArrayListExtra("photos", listOfPictureUri)
        requireActivity().setResult(Activity.RESULT_OK, replyIntent)
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

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private suspend fun savePhotoToExternalStorage(displayName: String, bmp: Bitmap): Boolean {
      return withContext(Dispatchers.IO) {
          val imageCollection = sdk29AndUp {
              MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
          } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

          val contentValues = ContentValues().apply {
              put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
              put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
              put(MediaStore.Images.Media.WIDTH, bmp.width)
              put(MediaStore.Images.Media.HEIGHT, bmp.height)
          }
          try {
              requireContext().contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                  requireContext().contentResolver.openOutputStream(uri).use { outputStream ->
                      if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                          throw IOException("Couldn't save bitmap")
                      }
                  }
              } ?: throw IOException("Couldn't create MediaStore entry")
              true
          } catch (exception: IOException) {
              exception.printStackTrace()
              false
          }
      }
    }

    private fun setupInternalStorageRecyclerView() = mBinding.rvPrivatePhotos.apply {
        adapter = internalStoragePhotoAdapter
        layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
    }

    private fun setupExternalStorageRecyclerView() = mBinding.rvPublicPhotos.apply {
        adapter = externalStoragePhotoAdapter
        layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
    }

    private fun loadPhotosFromInternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = loadPhotosFromInternalStorage()
            internalStoragePhotoAdapter.submitList(photos)
        }
    }

    private suspend fun deletePhotoFromExternalStorage(photoUri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                requireContext().contentResolver.delete(photoUri, null, null)
            } catch (exception: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(requireContext().contentResolver, listOf(photoUri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = exception as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }

    private fun loadPhotosFromExternalStorageIntoRecyclerView() {
        lifecycleScope.launch {
            val photos = loadPhotosFromExternalStorage()
            externalStoragePhotoAdapter.submitList(photos)
        }
    }

    private suspend fun deletePhotoFromInternalStorage(filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                requireActivity().deleteFile(filename)
            } catch (exception: Exception) {
                exception.printStackTrace()
                false
            }
        }
    }

    private suspend fun loadPhotosFromInternalStorage(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bmp)
            } ?: listOf()
        }
    }

    private suspend fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                requireActivity().openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
                true
            } catch (exception: IOException) {
                exception.printStackTrace()
                false
            }
        }
    }

    private fun showPhotoDialog() {
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.category_dialog_box)
        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).text = getString(R.string.category_dialog_box_photo_btn)
        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).text = getString(R.string.category_dialog_box_data_btn)
        dialog.findViewById<TextView>(R.id.category_dialog_box_title).text = getString(R.string.category_dialog_box_title_photo)
        dialog.findViewById<EditText>(R.id.category_dialog_box_spinner).setOnClickListener{
            dialog.findViewById<EditText>(R.id.category_dialog_box_spinner).setText(setCategory())
        }

        dialog.findViewById<Button>(R.id.category_dialog_box_positive_btn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.category_dialog_box_negative_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setCategory(): String {
        var item: MaterialDialog? = null
        MaterialDialog(requireContext()).show {
            item = listItemsSingleChoice(R.array.rooms_array)
            listOfCategory.add(item!!.getItemSelector().toString())
        }
        return item?.getItemSelector().toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().contentResolver.unregisterContentObserver(contentObserver)
    }
}
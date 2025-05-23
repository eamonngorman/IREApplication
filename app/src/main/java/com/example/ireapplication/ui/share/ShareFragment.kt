package com.example.ireapplication.ui.share

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.ireapplication.R
import com.example.ireapplication.databinding.FragmentShareBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ShareFragment : Fragment() {
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShareViewModel by viewModels()

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: ExecutorService? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, getString(R.string.error_camera_permission), Toast.LENGTH_SHORT).show()
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            openGallery()
        } else {
            Toast.makeText(context, getString(R.string.error_storage_permission), Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.setCapturedImageUri(it)
            showFramedImage(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setupButtons()
        setupObservers()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupObservers() {
        viewModel.capturedImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                // Hide camera preview and show captured image
                binding.viewFinder.visibility = View.GONE
                binding.overlayImage.visibility = View.VISIBLE
                
                // Switch controls
                binding.cameraControls.visibility = View.GONE
                binding.imageControls.visibility = View.VISIBLE
            }
        }
    }

    private fun setupButtons() {
        binding.apply {
            captureButton.setOnClickListener { takePhoto() }
            switchCameraButton.setOnClickListener { switchCamera() }
            galleryButton.setOnClickListener { checkStoragePermissionAndOpenGallery() }
            shareButton.setOnClickListener { shareImage() }
            backButton.setOnClickListener { returnToCamera() }
        }
    }

    private fun checkStoragePermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                    // Show explanation if needed
                    Toast.makeText(context, getString(R.string.error_storage_permission), Toast.LENGTH_LONG).show()
                    requestStoragePermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                }
                else -> {
                    requestStoragePermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                }
            }
        } else {
            // For Android 12 and below
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // Show explanation if needed
                    Toast.makeText(context, getString(R.string.error_storage_permission), Toast.LENGTH_LONG).show()
                    requestStoragePermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
                else -> {
                    requestStoragePermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview use case
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            // Image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()

                // Select front or back camera
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.error_camera_start), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { uri ->
                        viewModel.setCapturedImageUri(uri)
                        showFramedImage(uri)
                        Toast.makeText(context, getString(R.string.msg_photo_captured), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(context, getString(R.string.error_photo_capture), Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }

    private fun returnToCamera() {
        // Show camera preview and hide captured image
        binding.viewFinder.visibility = View.VISIBLE
        binding.overlayImage.visibility = View.GONE
        
        // Switch controls
        binding.cameraControls.visibility = View.VISIBLE
        binding.imageControls.visibility = View.GONE
        
        // Clear the captured image URI
        viewModel.setCapturedImageUri(null)
    }

    private fun shareImage() {
        viewModel.capturedImageUri.value?.let { uri ->
            // Process image with frame
            val processedImageUri = processImageWithBanner(uri)
            processedImageUri?.let { processedUri ->
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, processedUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.title_share_image)))
            }
        } ?: run {
            Toast.makeText(context, getString(R.string.error_no_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageWithBanner(imageUri: android.net.Uri): android.net.Uri? {
        try {
            // Load original image with correct orientation
            val originalBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            
            // Get image orientation from MediaStore
            val cursor = requireContext().contentResolver.query(
                imageUri,
                arrayOf(MediaStore.Images.Media.ORIENTATION),
                null,
                null,
                null
            )
            
            var orientation = 0
            cursor?.use {
                if (it.moveToFirst()) {
                    orientation = it.getInt(0)
                }
            }
            
            // Create matrix for rotation
            val matrix = Matrix()
            when (orientation) {
                90 -> matrix.postRotate(90f)
                180 -> matrix.postRotate(180f)
                270 -> matrix.postRotate(270f)
            }
            
            // Create rotated bitmap
            val rotatedBitmap = Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                matrix,
                true
            )

            // Load frame
            val frameBitmap = BitmapFactory.decodeResource(resources, R.drawable.ireframe)
            
            // Scale frame to match image dimensions
            val scaledFrame = Bitmap.createScaledBitmap(
                frameBitmap,
                rotatedBitmap.width,
                rotatedBitmap.height,
                true
            )
            
            // Create new bitmap for the final image
            val combinedBitmap = Bitmap.createBitmap(
                rotatedBitmap.width,
                rotatedBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            
            val canvas = Canvas(combinedBitmap)
            
            // Draw original image
            canvas.drawBitmap(rotatedBitmap, 0f, 0f, null)
            
            // Draw frame on top
            canvas.drawBitmap(scaledFrame, 0f, 0f, null)
            
            // Save combined image
            val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                .format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }
            
            val outputUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            
            outputUri?.let { uri ->
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            }
            
            // Clean up
            originalBitmap.recycle()
            rotatedBitmap.recycle()
            frameBitmap.recycle()
            scaledFrame.recycle()
            combinedBitmap.recycle()
            
            return outputUri
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun showFramedImage(imageUri: android.net.Uri) {
        try {
            // Load original image with correct orientation
            val originalBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            
            // Get image orientation from MediaStore
            val cursor = requireContext().contentResolver.query(
                imageUri,
                arrayOf(MediaStore.Images.Media.ORIENTATION),
                null,
                null,
                null
            )
            
            var orientation = 0
            cursor?.use {
                if (it.moveToFirst()) {
                    orientation = it.getInt(0)
                }
            }
            
            // Create matrix for rotation
            val matrix = Matrix()
            when (orientation) {
                90 -> matrix.postRotate(90f)
                180 -> matrix.postRotate(180f)
                270 -> matrix.postRotate(270f)
            }
            
            // Create rotated bitmap
            val rotatedBitmap = Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                matrix,
                true
            )

            // Load frame
            val frameBitmap = BitmapFactory.decodeResource(resources, R.drawable.ireframe)
            
            // Scale frame to match image dimensions
            val scaledFrame = Bitmap.createScaledBitmap(
                frameBitmap,
                rotatedBitmap.width,
                rotatedBitmap.height,
                true
            )
            
            // Create new bitmap for the preview
            val combinedBitmap = Bitmap.createBitmap(
                rotatedBitmap.width,
                rotatedBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            
            val canvas = Canvas(combinedBitmap)
            
            // Draw original image
            canvas.drawBitmap(rotatedBitmap, 0f, 0f, null)
            
            // Draw frame on top
            canvas.drawBitmap(scaledFrame, 0f, 0f, null)

            // Show the framed preview
            binding.overlayImage.setImageBitmap(combinedBitmap)
            
            // Clean up
            originalBitmap.recycle()
            rotatedBitmap.recycle()
            frameBitmap.recycle()
            scaledFrame.recycle()
            
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
            // Fall back to showing original image
            Glide.with(this)
                .load(imageUri)
                .into(binding.overlayImage)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor?.shutdown()
        _binding = null
    }
} 
package com.example.ireapplication.ui.share

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
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
import com.example.ireapplication.databinding.FragmentShareBinding
import dagger.hilt.android.AndroidEntryPoint
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
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
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setupButtons()
        setupObservers()
        setupSlider()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupObservers() {
        viewModel.capturedImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                // Hide camera preview and show captured image
                binding.viewFinder.visibility = View.GONE
                binding.overlayImage.visibility = View.VISIBLE
                binding.ireOverlayText.visibility = View.VISIBLE
                
                // Show edit controls and hide camera controls
                binding.cameraControls.visibility = View.GONE
                binding.editControls.visibility = View.VISIBLE
                
                // Load the image using Glide
                Glide.with(this)
                    .load(it)
                    .into(binding.overlayImage)
            }
        }
    }

    private fun setupSlider() {
        binding.verticalPositionSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                updateOverlayPosition()
            }
        }

        binding.horizontalPositionSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                updateOverlayPosition()
            }
        }
    }

    private fun updateOverlayPosition() {
        val verticalValue = binding.verticalPositionSlider.value
        val horizontalValue = binding.horizontalPositionSlider.value
        
        // Calculate positions based on slider values (0 = start/top, 1 = end/bottom)
        val topMargin = (verticalValue * binding.overlayImage.height).toInt()
        val startMargin = (horizontalValue * binding.overlayImage.width).toInt()
        
        val params = binding.ireOverlayText.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = topMargin
        params.marginStart = startMargin
        binding.ireOverlayText.layoutParams = params
    }

    private fun setupButtons() {
        binding.apply {
            captureButton.setOnClickListener { takePhoto() }
            switchCameraButton.setOnClickListener { switchCamera() }
            galleryButton.setOnClickListener { /* TODO: Implement gallery selection */ }
            shareButton.setOnClickListener { /* TODO: Implement share functionality */ }
        }
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
                Toast.makeText(context, "Failed to start camera", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
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

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor?.shutdown()
        _binding = null
    }
} 
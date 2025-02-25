package com.example.ireapplication.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.example.ireapplication.databinding.FragmentScanQrBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.ireapplication.util.ErrorHandler

@AndroidEntryPoint
class ScanQrFragment : Fragment() {
    private var _binding: FragmentScanQrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScanQrViewModel by viewModels()

    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService? = null
    private var barcodeScanner: BarcodeScanner? = null
    private var imageAnalysis: ImageAnalysis? = null

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
        _binding = FragmentScanQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupScanner()
            setupObservers()
            setupClickListeners()

            if (allPermissionsGranted()) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

            cameraExecutor = Executors.newSingleThreadExecutor()
            
            ErrorHandler.logDebug("QR Scanner initialized successfully")
        } catch (e: Exception) {
            ErrorHandler.handleError(
                requireContext(),
                e,
                "Failed to initialize QR scanner",
                true
            )
        }
    }

    private fun setupScanner() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)
    }

    private fun setupObservers() {
        viewModel.scanResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ScanResult.ValidExhibit -> {
                    // Play success sound/vibration
                    findNavController().navigate(
                        ScanQrFragmentDirections.actionScanQrToExhibitDetail(result.exhibitId)
                    )
                    viewModel.resetScanResult()
                }
                is ScanResult.Invalid -> {
                    // Play error sound/vibration
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetScanResult()
                }
                null -> { /* Ignore */ }
            }
        }

        viewModel.isFlashlightOn.observe(viewLifecycleOwner) { isOn ->
            camera?.cameraControl?.enableTorch(isOn)
            binding.flashlightButton.setImageResource(
                if (isOn) com.google.android.material.R.drawable.ic_m3_chip_close
                else com.google.android.material.R.drawable.ic_m3_chip_check
            )
        }
    }

    private fun setupClickListeners() {
        binding.flashlightButton.setOnClickListener {
            viewModel.toggleFlashlight()
        }
    }

    private fun startCamera() {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    // Preview use case
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(binding.previewView.surfaceProvider)

                    // Image analysis use case
                    imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor!!) { imageProxy ->
                                processImage(imageProxy)
                            }
                        }

                    try {
                        cameraProvider.unbindAll()

                        // Select back camera
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        // Bind use cases to camera
                        camera = cameraProvider.bindToLifecycle(
                            viewLifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to start camera", Toast.LENGTH_SHORT).show()
                    }
                    ErrorHandler.logDebug("Camera started successfully")
                } catch (e: Exception) {
                    ErrorHandler.handleCameraError(requireContext(), e)
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } catch (e: Exception) {
            ErrorHandler.handleCameraError(requireContext(), e)
        }
    }

    private fun processImage(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                
                barcodeScanner?.process(image)
                    ?.addOnSuccessListener { barcodes ->
                        // Take the first valid barcode
                        barcodes.firstOrNull()?.rawValue?.let { value ->
                            ErrorHandler.logDebug("QR Code scanned: $value")
                            viewModel.onQrCodeScanned(value)
                        }
                    }
                    ?.addOnFailureListener { e ->
                        ErrorHandler.handleQRScanError(requireContext(), e)
                    }
                    ?.addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
                ErrorHandler.logDebug("No image available for processing")
            }
        } catch (e: Exception) {
            imageProxy.close()
            ErrorHandler.handleQRScanError(requireContext(), e)
        }
    }

    private fun handleScannedContent(content: String) {
        // TODO: Navigate to appropriate screen based on QR content
        // For example, if the QR code contains an exhibit ID:
        // findNavController().navigate(
        //     ScanQrFragmentDirections.actionScanQrToExhibitDetail(content)
        // )
        Toast.makeText(context, "Scanned: $content", Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor?.shutdown()
        barcodeScanner?.close()
        _binding = null
    }
} 
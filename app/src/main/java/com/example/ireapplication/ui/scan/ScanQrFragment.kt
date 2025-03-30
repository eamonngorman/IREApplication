package com.example.ireapplication.ui.scan

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import com.example.ireapplication.util.ErrorHandler
import com.example.ireapplication.R
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class ScanQrFragment : Fragment() {
    private var _binding: FragmentScanQrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScanQrViewModel by viewModels()

    private var camera: Camera? = null
    private var cameraExecutor: ExecutorService? = null
    private var barcodeScanner: BarcodeScanner? = null
    private var imageAnalysis: ImageAnalysis? = null
    
    // Add a processing flag to prevent multiple simultaneous scans
    private val isProcessingQrCode = AtomicBoolean(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, getString(R.string.error_camera_permission), Toast.LENGTH_SHORT).show()
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
            ErrorHandler.logDebug("Starting QR Scanner initialization")
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
                    ErrorHandler.logDebug("NAVIGATION: Valid exhibit found: ID=${result.exhibitId}, attempting navigation...")
                    // Play success sound/vibration
                    try {
                        // Navigate to exhibit detail fragment
                        val directions = ScanQrFragmentDirections.actionScanQrToExhibitDetail(result.exhibitId)
                        ErrorHandler.logDebug("NAVIGATION: Created directions with exhibit ID: ${result.exhibitId}")
                        
                        // Debug current navigation controller state
                        val navController = findNavController()
                        ErrorHandler.logDebug("NAVIGATION: Current destination: ${navController.currentDestination?.label}")
                        
                        // Attempt navigation
                        ErrorHandler.logDebug("NAVIGATION: Executing navigation action...")
                        // Try direct navigation if the directions fail
                        try {
                            navController.navigate(directions)
                            ErrorHandler.logDebug("NAVIGATION: Navigation to exhibit detail successful via directions")
                        } catch (e: Exception) {
                            ErrorHandler.logDebug("NAVIGATION: Directions failed, trying direct navigation: ${e.message}")
                            try {
                                val bundle = Bundle().apply {
                                    putInt("exhibitId", result.exhibitId)
                                }
                                navController.navigate(R.id.exhibitDetailFragment, bundle)
                                ErrorHandler.logDebug("NAVIGATION: Direct navigation successful")
                            } catch (e2: Exception) {
                                ErrorHandler.logDebug("NAVIGATION: Direct navigation also failed: ${e2.message}")
                                throw e2
                            }
                        }
                    } catch (e: Exception) {
                        ErrorHandler.logDebug("NAVIGATION ERROR: ${e.javaClass.simpleName}: ${e.message}")
                        ErrorHandler.logDebug("NAVIGATION ERROR stacktrace: ${e.stackTraceToString()}")
                        Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.resetScanResult()
                    // Reset processing flag after navigation
                    isProcessingQrCode.set(false)
                }
                is ScanResult.GenericContent -> {
                    ErrorHandler.logDebug("Generic QR content found: ${result.content}")
                    // Show QR content and controls
                    showQrContentResult(result.content)
                }
                is ScanResult.Invalid -> {
                    ErrorHandler.logDebug("Invalid QR: ${result.message}")
                    // Play error sound/vibration
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetScanResult()
                    // Reset processing flag after showing error message
                    isProcessingQrCode.set(false)
                }
                null -> { 
                    // Reset UI to scanning state
                    resetScanningUI()
                }
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
        
        binding.scanAgainButton.setOnClickListener {
            resetScanningUI()
            viewModel.resetScanResult()
            isProcessingQrCode.set(false)
        }
        
        binding.copyButton.setOnClickListener {
            val content = binding.qrContentText.text.toString()
            if (content.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("QR Code Content", content)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, getString(R.string.qr_copied_to_clipboard), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showQrContentResult(content: String) {
        // Show result UI
        binding.scanContainer.visibility = View.GONE
        binding.resultContainer.visibility = View.VISIBLE
        binding.qrContentText.text = content
        
        // Determine if content is a URL
        if (content.startsWith("http://") || content.startsWith("https://")) {
            binding.contentTypeText.text = getString(R.string.qr_content_type_url)
            binding.openLinkButton.visibility = View.VISIBLE
            binding.openLinkButton.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(content))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, getString(R.string.qr_unable_to_open_url), Toast.LENGTH_SHORT).show()
                    ErrorHandler.logDebug("Failed to open URL: ${e.message}")
                }
            }
        } else {
            binding.contentTypeText.text = getString(R.string.qr_content_type_text)
            binding.openLinkButton.visibility = View.GONE
        }
    }
    
    private fun resetScanningUI() {
        binding.scanContainer.visibility = View.VISIBLE
        binding.resultContainer.visibility = View.GONE
        binding.qrContentText.text = ""
    }

    private fun startCamera() {
        try {
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = 
                ProcessCameraProvider.getInstance(requireContext())

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

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
                        ErrorHandler.handleError(
                            requireContext(),
                            e,
                            getString(R.string.error_camera_start),
                            true
                        )
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
        // Skip processing if already handling a QR code
        if (isProcessingQrCode.get()) {
            imageProxy.close()
            return
        }
        
        try {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                
                barcodeScanner?.process(image)
                    ?.addOnSuccessListener { barcodes ->
                        // Take the first valid barcode
                        val barcode = barcodes.firstOrNull()
                        if (barcode != null) {
                            val value = barcode.rawValue
                            if (value != null) {
                                // Only process if we're not already handling a QR code
                                if (isProcessingQrCode.compareAndSet(false, true)) {
                                    ErrorHandler.logDebug("QR Code scanned: $value")
                                    viewModel.onQrCodeScanned(value)
                                    
                                    // Add a delay before allowing another scan
                                    cameraExecutor?.execute {
                                        try {
                                            Thread.sleep(1500) // 1.5 second delay
                                        } catch (e: InterruptedException) {
                                            // Ignore
                                        } finally {
                                            isProcessingQrCode.set(false)
                                        }
                                    }
                                }
                            } else {
                                ErrorHandler.logDebug("Barcode detected but value is null")
                            }
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
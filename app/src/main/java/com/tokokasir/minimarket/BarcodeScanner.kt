package com.tokokasir.minimarket

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.barcode.BarcodeScanner


@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerView(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val scanner = remember { com.google.mlkit.vision.barcode.BarcodeScanning.getClient() }
//    Menampilkan Preview Kamera
    AndroidView(
        factory = { ctx ->
            val  previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

//                Pengaturan detektor bercode ML Kit
                val barcodeAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

//                 logika pendeteksi barcode
                    .also { barcodeAnalyzer ->
                        var isProcessing = false

                        barcodeAnalyzer.setAnalyzer(executor) { imageProxy ->
                            val mediaImage = imageProxy.image

                            if (mediaImage != null && !isProcessing) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                                isProcessing = true

                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        if (barcodes.isNotEmpty()) {
                                            val barcode = barcodes[0]
                                            barcode.rawValue?.let { code ->
                                                onBarcodeDetected(code)
                                            }
                                        } else {
                                            isProcessing = false
                                        }
                                    }
                                    .addOnFailureListener {
                                        // JIKA ERROR, BUKA LAGI KUNCINYA
                                        isProcessing = false
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        barcodeAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, executor)
            previewView
            },
        modifier = modifier
    )
}
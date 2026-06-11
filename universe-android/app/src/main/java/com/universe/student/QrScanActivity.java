package com.universe.student;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class QrScanActivity extends Activity {

    public static final String EXTRA_QR_PAYLOAD = "qrPayload";
    public static final String EXTRA_INSTRUCTION = "instruction";
    private static final int REQUEST_CAMERA_PERMISSION = 2001;

    private final AtomicBoolean decoding = new AtomicBoolean();
    private final AtomicBoolean completed = new AtomicBoolean();
    private final MultiFormatReader barcodeReader = new MultiFormatReader();

    private TextureView textureView;
    private HandlerThread cameraThread;
    private Handler cameraHandler;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private Surface previewSurface;
    private Size captureSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureReader();
        setContentView(createScannerView());
        if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraThread();
            startWhenReady();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void configureReader() {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS,
                Collections.singletonList(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        barcodeReader.setHints(hints);
    }

    private View createScannerView() {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        textureView = new TextureView(this);
        root.addView(textureView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        TextView instruction = new TextView(this);
        String instructionText = getIntent().getStringExtra(EXTRA_INSTRUCTION);
        instruction.setText(instructionText == null || instructionText.isBlank()
                ? "Đưa mã QR điểm danh vào giữa khung hình"
                : instructionText);
        instruction.setTextColor(Color.WHITE);
        instruction.setTextSize(17);
        instruction.setGravity(Gravity.CENTER);
        instruction.setBackgroundColor(0x99070B1A);
        instruction.setPadding(dp(20), dp(18), dp(20), dp(18));
        FrameLayout.LayoutParams instructionParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP);
        root.addView(instruction, instructionParams);

        Button cancel = new Button(this);
        cancel.setText("Hủy quét");
        cancel.setTextColor(getColor(R.color.white));
        cancel.setAllCaps(false);
        cancel.setBackground(getDrawable(R.drawable.button_primary));
        cancel.setOnClickListener(view -> finish());
        FrameLayout.LayoutParams cancelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        cancelParams.bottomMargin = dp(28);
        root.addView(cancel, cancelParams);
        return root;
    }

    private void startWhenReady() {
        if (textureView.isAvailable()) {
            openCamera(textureView.getSurfaceTexture());
            return;
        }
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(
                    SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    private void startCameraThread() {
        if (cameraThread != null) {
            return;
        }
        cameraThread = new HandlerThread("universe-qr-camera");
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

    private void openCamera(SurfaceTexture texture) {
        if (cameraDevice != null || isFinishing()) {
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = findBackCamera(manager);
            CameraCharacteristics characteristics =
                    manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap configurationMap =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (configurationMap == null) {
                fail("Camera không cung cấp cấu hình preview.");
                return;
            }
            captureSize = chooseSize(
                    configurationMap.getOutputSizes(ImageFormat.YUV_420_888));
            Size previewSize = chooseSize(
                    configurationMap.getOutputSizes(SurfaceTexture.class));
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewSurface = new Surface(texture);
            manager.openCamera(cameraId, cameraStateCallback, cameraHandler);
        } catch (SecurityException ex) {
            fail("Ứng dụng chưa có quyền camera.");
        } catch (CameraAccessException | IllegalStateException ex) {
            fail("Không thể mở camera: " + ex.getMessage());
        }
    }

    private final CameraDevice.StateCallback cameraStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    cameraDevice = camera;
                    createCaptureSession();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                    fail("Camera gặp lỗi " + error + ".");
                }
            };

    private void createCaptureSession() {
        if (cameraDevice == null || previewSurface == null || captureSize == null) {
            return;
        }
        imageReader = ImageReader.newInstance(
                captureSize.getWidth(),
                captureSize.getHeight(),
                ImageFormat.YUV_420_888,
                2);
        imageReader.setOnImageAvailableListener(this::decodeLatestImage, cameraHandler);
        try {
            cameraDevice.createCaptureSession(
                    Arrays.asList(previewSurface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            captureSession = session;
                            startPreview();
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            fail("Không thể khởi tạo camera quét QR.");
                        }
                    },
                    cameraHandler);
        } catch (CameraAccessException ex) {
            fail("Không thể tạo phiên camera.");
        }
    }

    private void startPreview() {
        try {
            CaptureRequest.Builder request =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            request.addTarget(previewSurface);
            request.addTarget(imageReader.getSurface());
            request.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureSession.setRepeatingRequest(request.build(), null, cameraHandler);
        } catch (CameraAccessException ex) {
            fail("Không thể bắt đầu preview camera.");
        }
    }

    private void decodeLatestImage(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image == null) {
            return;
        }
        if (!decoding.compareAndSet(false, true)) {
            image.close();
            return;
        }
        try {
            Image.Plane plane = image.getPlanes()[0];
            ByteBuffer buffer = plane.getBuffer();
            int rowStride = plane.getRowStride();
            int pixelStride = plane.getPixelStride();
            int width = image.getWidth();
            int height = image.getHeight();
            byte[] luminance = packLuminance(
                    buffer, rowStride, pixelStride, width, height);
            Result result = decode(luminance, width, width, height);
            if (result == null) {
                byte[] rotated = rotateClockwise(luminance, width, width, height);
                result = decode(rotated, height, height, width);
            }
            if (result != null) {
                complete(result.getText());
            }
        } finally {
            image.close();
            barcodeReader.reset();
            decoding.set(false);
        }
    }

    private byte[] packLuminance(ByteBuffer buffer, int rowStride, int pixelStride,
                                 int width, int height) {
        byte[] packed = new byte[width * height];
        ByteBuffer source = buffer.duplicate();
        for (int y = 0; y < height; y++) {
            int rowStart = y * rowStride;
            for (int x = 0; x < width; x++) {
                packed[y * width + x] = source.get(rowStart + x * pixelStride);
            }
        }
        return packed;
    }

    private Result decode(byte[] data, int dataWidth, int width, int height) {
        try {
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    data, dataWidth, height, 0, 0, width, height, false);
            return barcodeReader.decodeWithState(
                    new BinaryBitmap(new HybridBinarizer(source)));
        } catch (NotFoundException | IllegalArgumentException ignored) {
            return null;
        }
    }

    private byte[] rotateClockwise(byte[] source, int rowStride, int width, int height) {
        byte[] rotated = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotated[x * height + (height - y - 1)] = source[y * rowStride + x];
            }
        }
        return rotated;
    }

    private void complete(String payload) {
        if (!completed.compareAndSet(false, true)) {
            return;
        }
        runOnUiThread(() -> {
            Intent result = new Intent();
            result.putExtra(EXTRA_QR_PAYLOAD, payload);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    private String findBackCamera(CameraManager manager) throws CameraAccessException {
        String fallback = null;
        for (String id : manager.getCameraIdList()) {
            if (fallback == null) {
                fallback = id;
            }
            Integer facing = manager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.LENS_FACING);
            if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        if (fallback == null) {
            throw new IllegalStateException("Thiết bị không có camera.");
        }
        return fallback;
    }

    private Size chooseSize(Size[] sizes) {
        if (sizes == null || sizes.length == 0) {
            throw new IllegalStateException("Camera không có kích thước hỗ trợ.");
        }
        Size best = sizes[0];
        long targetArea = 1280L * 720L;
        long bestDifference = Math.abs(area(best) - targetArea);
        for (Size size : sizes) {
            long difference = Math.abs(area(size) - targetArea);
            if (difference < bestDifference) {
                best = size;
                bestDifference = difference;
            }
        }
        return best;
    }

    private long area(Size size) {
        return (long) size.getWidth() * size.getHeight();
    }

    private void fail(String message) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Không thể quét QR")
                .setMessage(message)
                .setPositiveButton("Đóng", (dialog, which) -> finish())
                .setCancelable(false)
                .show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return;
        }
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCameraThread();
            startWhenReady();
        } else {
            fail("Cần quyền camera để quét mã QR điểm danh.");
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                && cameraDevice == null) {
            startCameraThread();
            startWhenReady();
        }
    }

    private void closeCamera() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        if (previewSurface != null) {
            previewSurface.release();
            previewSurface = null;
        }
        if (cameraThread != null) {
            cameraThread.quitSafely();
            cameraThread = null;
            cameraHandler = null;
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}

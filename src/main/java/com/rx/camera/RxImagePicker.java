package com.rx.camera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;


import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class RxImagePicker {

    private Context context = null;

    //Default selection mode for dialog
    private String selectionMode = RxConstants.CAMERA_AND_GALLERY;

    private Disposable cameraSubscription = null;
    private Disposable gallerySubscription = null;
    private ImageView imageView = null;
    private OnOperationResult onOperationResult = null;

    private String choosenOption = RxConstants.CAMERA; //Default Capture using camera

    public RxImagePicker(Context context) {
        this.context = context;
    }

    public String getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(String selectionMode) {
        this.selectionMode = selectionMode;
    }

    /*public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }*/

    // To return result to subscribers place
    public OnOperationResult getOnOperationResult() {
        return onOperationResult;
    }

    public void setOnOperationResult(OnOperationResult onOperationResult) {
        this.onOperationResult = onOperationResult;
    }

    public void chooseImage() {
        switch (selectionMode) {
            case RxConstants.CAMERA_AND_GALLERY:
                final CharSequence[] options = {RxConstants.GALLERY, RxConstants.CAMERA, RxConstants.CANCEL};
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(RxConstants.DIALOG_TITLE);
                dialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(RxConstants.GALLERY)) {
                            choosenOption = RxConstants.GALLERY;
                            openGallery();
                        } else if (options[which].equals(RxConstants.CAMERA)) {
                            choosenOption = RxConstants.CAMERA;
                            openCamera();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;

            case RxConstants.CAMERA:
                choosenOption = RxConstants.CAMERA;
                openCamera();
                break;

            case RxConstants.GALLERY:
                choosenOption = RxConstants.GALLERY;
                openGallery();
                break;

            default:
                selectionMode = RxConstants.CAMERA_AND_GALLERY;
                chooseImage();
        }
    }

    private void openCamera() {
        subscribeForCamera();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((AppCompatActivity) context).startActivityForResult(intent, RxConstants.CAMERA_REQ_CODE);
    }

    private void subscribeForCamera() {
//        here we get subscription object so we can unsubscribe anytime
        cameraSubscription = RxEventBus.getEventBus().getObservables()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        if (o != null) processImage(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d("MyImagePicker", "onComplete");
                    }
                });
    }

    private void openGallery() {
        subscribeForGallery();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((AppCompatActivity) context).startActivityForResult(Intent.createChooser(intent, "Select file")
                , RxConstants.GALLERY_REQ_CODE);
    }

    private void subscribeForGallery() {
        gallerySubscription = RxEventBus.getEventBus().getObservables()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        processImage(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d("MyImagePicker", "onComplete");
                    }
                });
    }

    private void processImage(Object o) {
        if (o instanceof RxPojo) {
            RxPojo pojo = (RxPojo) o;
            if (pojo.getResultCode() == AppCompatActivity.RESULT_OK) {
                if (choosenOption.equals(RxConstants.CAMERA)) {
                    processCameraResult(pojo);
                } else if (choosenOption.equals(RxConstants.GALLERY)) {
                    processGalleryResult(pojo);
                }
            }
        }
        if (choosenOption.equalsIgnoreCase(RxConstants.CAMERA)) {
            unSubscribeCamera();
        } else {
            unSubscribeGallery();
        }
    }

    private void processCameraResult(RxPojo pojo) {
       /* Bitmap bitmap = (Bitmap) pojo.getIntent().getExtras().get("data");
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }*/
        if (onOperationResult != null) {
            onOperationResult.onOperationResult(pojo);
        }
    }

    private void processGalleryResult(RxPojo pojo) {

        Bitmap bitmap = null;
        if (pojo.getIntent() != null) {
            /*try {
                bitmap = MediaStore.Images.Media.
                        getBitmap(context.getApplicationContext().getContentResolver(), pojo.getIntent().getData());
                bitmap = cropAndScale(bitmap, 300);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }*/

            if (onOperationResult != null) {
                onOperationResult.onOperationResult(pojo);
            }
        }
    }

    private void unSubscribeCamera() {
        if (cameraSubscription != null && !cameraSubscription.isDisposed()) {
            cameraSubscription.dispose();
        }
    }

    private void unSubscribeGallery() {
        if (gallerySubscription != null && !gallerySubscription.isDisposed()) {
            gallerySubscription.dispose();
        }
    }

    public Bitmap cropAndScale(Bitmap source, int scale) {
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight() : source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight() : source.getWidth();
        int x = source.getHeight() >= source.getWidth() ? 0 : (longer - factor) / 2;
        int y = source.getHeight() <= source.getWidth() ? 0 : (longer - factor) / 2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }
}

package com.ferbook.providers;

import android.content.Context;

import com.ferbook.utils.CompressorBitmapImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save (Context context, File file) {

        //comprimir la imagen
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(),500,500);

        StorageReference storage = FirebaseStorage.getInstance().getReference().child(new Date()+".jpg");

        mStorage = storage;
        return storage.putBytes(imageByte);
    }

    public StorageReference getStorage () {
        return mStorage;
    }
}

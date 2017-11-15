package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Firebase photo provider
 *
 * @author pedro
 */
public class FirebasePhotoProvider extends PhotoProvider {
    StorageReference stRef = FirebaseStorage.getInstance().getReference();

    /**
     * converts an array of bytes into a string, each byte is converted with its hexadecimal
     * representation
     *
     * @param bytes the bytes to convert
     * @return the string of the resulting conversion
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b: bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadPOIBitmap(Bitmap bitmap, String poiName, final UploadPhotoListener listener) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // fill the outputStream with the bitmap data and convert it into a byte array
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        // create a hash of the data, it will be the name of the photo
        String photoName = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            photoName = bytesToHexString(hash) + ".jpg";
        } catch (NoSuchAlgorithmException e) {
            listener.onFailure();
            return;
        }

        // get the photo reference which is /Points of interest/#poiName/#photoName
        StorageReference poisRef = stRef.child("Points of interest");
        StorageReference poiRef = poisRef.child(poiName);
        StorageReference photoRef = poiRef.child(photoName);

        // create an uploadTask which takes care of the upload
        UploadTask uploadTask = photoRef.putBytes(data);

        // add listeners to uploadTask to keep track of the status of the upload
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // inform the listener that the upload failed
                listener.onFailure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // inform the listener that the upload succeeded
                listener.onSuccess();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                long totalBytes = taskSnapshot.getTotalByteCount();
                long bytesTransfered = taskSnapshot.getBytesTransferred();

                // inform the listener that the progress has been updated
                if (totalBytes == 0) {
                    // if there is no bytes then the upload is "done"
                    listener.updateProgress(100.0);
                } else {
                    double progress = (100.0 * bytesTransfered) / taskSnapshot.getTotalByteCount();
                    listener.updateProgress(progress);
                }

            }
        });
    }
}

package ch.epfl.sweng.fiktion.providers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Firebase photo provider
 *
 * @author pedro
 */
public class FirebasePhotoProvider extends PhotoProvider {
    private StorageReference stRef;
    private DatabaseReference dbRef;
    final private long MAXIMUM_SIZE = 10 * 1024 * 1024; // 10MB

    public FirebasePhotoProvider() {
        stRef = FirebaseStorage.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public FirebasePhotoProvider(StorageReference stRef, DatabaseReference dbRef) {
        this.stRef = stRef;
        this.dbRef = dbRef;
    }

    /**
     * converts an array of bytes into a string, each byte is converted with its hexadecimal
     * representation
     *
     * @param bytes the bytes to convert
     * @return the string of the resulting conversion
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadPOIBitmap(Bitmap bitmap, final String poiName, final UploadPhotoListener listener) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // fill the outputStream with the bitmap data and convert it into a byte array
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        // if the number of bytes exceeds MAXIMUM_SIZE, abort the upload
        if (data.length > MAXIMUM_SIZE) {
            listener.onFailure();
            return;
        }

        // create a hash of the data, it will be the name of the photo
        String photoName = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            photoName = bytesToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            listener.onFailure();
            return;
        }

        // get the photo reference which is /Points of interest/#poiName/#photoName
        StorageReference poisRef = stRef.child("Points of interest");
        StorageReference poiRef = poisRef.child(poiName);
        StorageReference photoRef = poiRef.child(photoName + ".jpg");

        // create an uploadTask which takes care of the upload
        UploadTask uploadTask = photoRef.putBytes(data);

        // add listeners to uploadTask to keep track of the status of the upload
        final String finalPhotoName = photoName;
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // inform the listener that the upload failed
                listener.onFailure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // store the photo name in the database so that we can retrieve it
                dbRef.child("Photo references").child(poiName).child(finalPhotoName).setValue(true);

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void downloadPOIBitmaps(final String poiName, final DownloadBitmapListener listener) {
        // first, get the reference of the poi and listen for its photo references
        DatabaseReference poiRef = dbRef.child("Photo references").child(poiName);
        poiRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // For every photo name, download the associated photo from firebase
                String photoName = dataSnapshot.getKey() + ".jpg";
                StorageReference photoRef = stRef.child("Points of interest").child(poiName).child(photoName);
                photoRef.getBytes(MAXIMUM_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // convert the bytes into a bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bitmap != null) {
                            // "send" the new bitmap to the listener
                            listener.onNewPhoto(bitmap);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }
}

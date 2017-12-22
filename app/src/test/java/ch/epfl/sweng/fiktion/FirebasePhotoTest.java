package ch.epfl.sweng.fiktion;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.OutputStream;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.providers.FirebasePhotoProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static ch.epfl.sweng.fiktion.providers.PhotoProvider.ALL_PHOTOS;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by pedro on 17/11/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FirebasePhotoTest {

    @Mock
    private StorageReference stRef;

    @Mock
    private DatabaseReference dbRef;

    @Captor
    private ArgumentCaptor<OnSuccessListener> successListener;

    @Captor
    private ArgumentCaptor<OnFailureListener> failureListener;

    private FirebasePhotoProvider photoProvider;

    enum Result {
        SUCCESS, FAILURE, NEWPHOTO, NOTHING
    }

    private Result result;

    private void setResult(Result result) {
        this.result = result;
    }

    private double progress = 0.0;

    private void setProgress(double progress) {
        this.progress = progress;
    }

    @Before
    public void setup() {
        photoProvider = new FirebasePhotoProvider(stRef, dbRef);
        result = Result.NOTHING;
        when(stRef.child(anyString())).thenReturn(stRef);
        when(dbRef.child(anyString())).thenReturn(dbRef);
    }

    @Test
    public void uploadTest() {
        UploadTask uploadTask = mock(UploadTask.class);
        when(stRef.putBytes(any(byte[].class))).thenReturn(uploadTask);
        final StorageTask<UploadTask.TaskSnapshot> storageTask = mock(StorageTask.class);
        when(uploadTask.addOnFailureListener(failureListener.capture())).thenReturn(storageTask);
        when(storageTask.addOnSuccessListener(successListener.capture())).thenReturn(storageTask);
        ArgumentCaptor<OnProgressListener> progressListener = ArgumentCaptor.forClass(OnProgressListener.class);
        when(storageTask.addOnProgressListener(progressListener.capture())).thenReturn(storageTask);
        when(dbRef.setValue(anyString())).thenReturn(null);

        Bitmap bitmap = mock(Bitmap.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7};
                ((OutputStream) invocation.getArgument(2)).write(bytes);
                return null;
            }
        }).when(bitmap).compress(any(Bitmap.CompressFormat.class), anyInt(), any(OutputStream.class));

        ArgumentCaptor<ValueEventListener> vel = ArgumentCaptor.forClass(ValueEventListener.class);
        doNothing().when(dbRef).addListenerForSingleValueEvent(vel.capture());

        DataSnapshot velSnapshot = mock(DataSnapshot.class);
        when(velSnapshot.getChildrenCount()).thenReturn((long) 2);
        when(stRef.delete()).thenReturn(null);

        PhotoProvider.UploadPOIPhotoListener listener = new PhotoProvider.UploadPOIPhotoListener() {
            @Override
            public void onSuccess(String photoName) {
                setResult(Result.SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(Result.FAILURE);
            }

            @Override
            public void updateProgress(double progress) {
                setProgress(progress);
            }
        };

        photoProvider.uploadPOIBitmap(bitmap, "poiTest", listener);
        UploadTask.TaskSnapshot snapshot = mock(UploadTask.TaskSnapshot.class);
        failureListener.getValue().onFailure(new Exception());
        assertThat(result, is(Result.FAILURE));
        successListener.getValue().onSuccess(snapshot);
        vel.getValue().onDataChange(velSnapshot);
        assertThat(result, is(Result.SUCCESS));
        vel.getValue().onCancelled(null);
        assertThat(result, is(Result.FAILURE));

        when(snapshot.getBytesTransferred()).thenReturn((long) 2);
        when(snapshot.getTotalByteCount()).thenReturn((long) 4);
        progressListener.getValue().onProgress(snapshot);
        assertThat(progress, is(50.0));
        when(snapshot.getTotalByteCount()).thenReturn((long) 0);
        progressListener.getValue().onProgress(snapshot);
        assertThat(progress, is(100.0));
    }

    @Test
    public void getPOIPhotoNamesTest() {
        when(dbRef.orderByKey()).thenReturn(dbRef);
        when(dbRef.limitToFirst(anyInt())).thenReturn(dbRef);

        ArgumentCaptor<ChildEventListener> cel = ArgumentCaptor.forClass(ChildEventListener.class);

        when(dbRef.addChildEventListener(cel.capture())).thenReturn(null);

        final Set<String> photoNames = new TreeSet<>();
        final Mutable<Boolean> isFailure = new Mutable<>(false);
        PhotoProvider.GetPhotoNamesListener listener = new PhotoProvider.GetPhotoNamesListener() {
            @Override
            public void onNewValue(String photoName) {
                photoNames.add(photoName);
            }

            @Override
            public void onFailure() {
                isFailure.set(true);
            }
        };

        photoProvider.getPOIPhotoNames("poi", ALL_PHOTOS, listener);
        photoProvider.getPOIPhotoNames("poi", 3, listener);
        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.getValue()).thenReturn("42");
        cel.getValue().onChildAdded(snapshot, "");
        assertTrue(photoNames.contains("42.jpg"));
        when(snapshot.getValue()).thenReturn("360");
        cel.getValue().onChildAdded(snapshot, "");
        assertTrue(photoNames.contains("360.jpg"));
        assertThat(photoNames.size(), is(2));
        assertFalse(isFailure.get());

        cel.getValue().onChildChanged(snapshot, "");
        cel.getValue().onChildRemoved(snapshot);
        cel.getValue().onChildMoved(snapshot, "");
        assertFalse(isFailure.get());
        cel.getValue().onCancelled(null);
        assertTrue(isFailure.get());

    }

    @Test
    public void downloadPOIBitmapTest() {
        final Task<byte[]> task = mock(Task.class);
        when(stRef.getBytes(anyLong())).thenReturn(task);
        when(task.addOnSuccessListener(successListener.capture())).thenReturn(task);
        when(task.addOnFailureListener(failureListener.capture())).thenReturn(task);

        PhotoProvider.DownloadBitmapListener listener = new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onNewValue(Bitmap b) {
                setResult(Result.NEWPHOTO);
            }

            @Override
            public void onFailure() {
                setResult(Result.FAILURE);
            }
        };

        photoProvider.downloadPOIBitmap("poi", "photo", listener);
        failureListener.getValue().onFailure(new Exception());
        assertThat(result, is(Result.FAILURE));
    }
}

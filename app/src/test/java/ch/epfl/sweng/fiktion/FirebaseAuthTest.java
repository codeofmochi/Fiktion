
package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseAuthProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, FirebaseAuth.class, GeoFire.class})
public class FirebaseAuthTest {

    private FirebaseAuthProvider auth;
    private String email = "test@epfl.ch";
    private String password = "testing";
    @Mock
    FirebaseAuth fbAuth;
    @Mock
    FirebaseDatabase fbDatabase;
    @Mock
    FirebaseUser fbUser;
    @Mock
    Task<AuthResult> taskAuthSucceedResult;
    @Mock
    Task<Void> taskVoidSucceedResult;
    @Mock
    AuthResult result;
    @Mock
    OnCompleteListener<AuthResult> onCompleteAuthListener;
    @Mock
    OnCompleteListener<Void> onCompleteVoidListener;
    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> testOnCompleteAuthListener;
    @Captor
    private ArgumentCaptor<OnCompleteListener<Void>> testOnCompleteVoidListener;


    @Mock
    GeoFire geofire;

    @Before
    public void setUp() throws Exception{
        mockStatic(FirebaseAuth.class);
        setSucceedTask();

        Mockito.when(FirebaseAuth.getInstance()).thenReturn(fbAuth);

        auth = new FirebaseAuthProvider();

    }

    private void setSucceedTask(){
        Mockito.when(taskAuthSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthSucceedResult);
        Mockito.when(taskVoidSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskVoidSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskVoidSucceedResult.addOnCompleteListener(testOnCompleteVoidListener.capture())).
                thenReturn(taskVoidSucceedResult);
    }

    @Test
    public void succeedSendPasswordResetEmail(){
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbAuth.sendPasswordResetEmail(email)).thenReturn(taskVoidSucceedResult);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.sendPasswordResetEmail(email));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }

    @Test
    public void succeedCreateUser(){
        Mockito.when(fbAuth.createUserWithEmailAndPassword(email,password)).thenReturn(taskAuthSucceedResult);
        auth.createUserWithEmailAndPassword(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.createUserWithEmailAndPassword(email,password));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthSucceedResult);
    }

    @Test
    public void succeedSignIn(){
        setSucceedTask();
        Mockito.when(fbAuth.signInWithEmailAndPassword(email,password)).thenReturn(taskAuthSucceedResult);

        auth.signIn(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.signInWithEmailAndPassword(email,password));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthSucceedResult);


    }
    @Test
    public void testAuthSignOut(){
        Mockito.doNothing().when(fbAuth).signOut();
        auth.signOut();
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);
        Assert.assertNull(auth.getCurrentUser());
    }


    @Test
    public void getCurrentUser() {
        String name = "default";
        String email = "test@test.ch";
        String id = "id";

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getDisplayName()).thenReturn(name);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbUser.getUid()).thenReturn(id);
        Mockito.when(fbUser.isEmailVerified()).thenReturn(false);

        Assert.assertEquals(auth.getCurrentUser().getEmail(), email);
        Assert.assertEquals(auth.getCurrentUser().getName(), name);
        Assert.assertEquals(auth.getCurrentUser().getID(), id);
        Assert.assertFalse(auth.getCurrentUser().isEmailVerified());
    }







}
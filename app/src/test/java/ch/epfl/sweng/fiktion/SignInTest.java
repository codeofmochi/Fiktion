package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.SignInActivity;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/** tests of tests
 * Created by Rodrigo on 02.11.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, FirebaseAuth.class, GeoFire.class, FirebaseAuthProvider.class})
public class SignInTest {

    SignInActivity siAct;

    @Mock
    FirebaseAuth fbAuth;
    @Mock
    FirebaseAuthProvider fbAuthProv;
    @Mock
    FirebaseDatabase fbDatabase;
    @Mock
    DatabaseReference dbRef;
    @Mock
    GeoFire gf;
    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> testOnCompleteAuthListener;
    @Mock
    FirebaseUser fbUser;
    @Mock
    Task<AuthResult> taskAuthSucceedResult;
    @Mock
    Task<AuthResult> taskAuthFailResult;
    @Captor
    private ArgumentCaptor<AuthProvider.AuthListener> testAuthListener;


    @Before
    public void setUp() throws Exception {
        mockStatic(FirebaseAuth.class);
        mockStatic(FirebaseDatabase.class);
        mockStatic(GeoFire.class);
        setTasks();
        Mockito.when(FirebaseAuth.getInstance()).thenReturn(fbAuth);
        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(fbDatabase);
        Mockito.when(fbDatabase.getReference()).thenReturn(dbRef);
        whenNew(GeoFire.class).withAnyArguments().thenReturn(gf);
        whenNew(FirebaseAuthProvider.class).withAnyArguments().thenReturn(fbAuthProv);
        Providers.auth = new FirebaseAuthProvider();
        siAct = new SignInActivity();

    }

    private void setTasks() {
        Mockito.when(taskAuthSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthSucceedResult);
        Mockito.when(taskAuthFailResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthFailResult.isSuccessful()).thenReturn(false);
        Mockito.when(taskAuthFailResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthFailResult);
    }

    @Test
    public void testSignIn(){
        String valid_email = "test@test.ch";
        String valid_password = "testing";
        Mockito.when(fbAuth
                .signInWithEmailAndPassword(valid_email,valid_password))
                .thenReturn(taskAuthSucceedResult);
        Mockito.when(fbAuthProv.validateEmail(valid_email)).thenReturn("");
        Mockito.when(fbAuthProv.validatePassword(valid_password)).thenReturn("");
        siAct.signIn(valid_email,valid_password);
    }
}

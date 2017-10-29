
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

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseAuthProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, FirebaseAuth.class, GeoFire.class})
public class FirebaseAuthTest {

    private FirebaseAuthProvider auth;
    @Mock
    FirebaseAuth fbAuth;
    @Mock
    FirebaseDatabase fbDatabase;
    @Mock
    FirebaseUser fbUser;
    @Mock
    Task<AuthResult> taskWithResult;
    @Mock
    AuthResult result;
    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> testOnCompleteListener;


    @Mock
    GeoFire geofire;

    @Before
    public void setUp() throws Exception{
        mockStatic(FirebaseAuth.class);
        Mockito.when(FirebaseAuth.getInstance()).thenReturn(fbAuth);
        auth = new FirebaseAuthProvider();

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

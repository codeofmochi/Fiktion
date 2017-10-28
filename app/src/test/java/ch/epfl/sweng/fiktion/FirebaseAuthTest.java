/*
package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ch.epfl.sweng.fiktion.providers.Providers;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseDatabase.class, FirebaseAuth.class, GeoFire.class})
public class FirebaseAuthTest {
    @Mock
    private FirebaseAuth fbAuth;
    @Mock
    private FirebaseDatabase fbDatabase;
    @Mock
    private FirebaseUser fbUser;
    @Mock
    private GeoFire gf;
    @Mock
    FirebaseDatabase fb;
    @Mock
    DatabaseReference dbRef;

    @Before
    public void setUp(){
        mockStatic(FirebaseAuth.class);
        mockStatic(FirebaseDatabase.class);
        Mockito.when(FirebaseDatabase.getInstance()).thenReturn(fbDatabase);
        Mockito.when(FirebaseAuth.getInstance()).thenReturn(fbAuth);
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.doNothing().when(fbAuth).signOut();
    }

    @Test
    public void testAuthSignOut(){
        Providers.auth.signOut();
        Assert.assertNull(Providers.auth.getCurrentUser());
    }


}
*/
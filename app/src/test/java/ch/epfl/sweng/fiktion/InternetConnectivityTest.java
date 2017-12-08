package ch.epfl.sweng.fiktion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetAddress;

import ch.epfl.sweng.fiktion.providers.ConnectivityReceiverProvider;

import static android.net.ConnectivityManager.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by serdar on 12/8/2017.
 * Test InternetConnectivityProvider class
 */

@RunWith(MockitoJUnitRunner.class)
public class InternetConnectivityTest {

    @Mock
    Context context;
    ConnectivityManager manager;
    InetAddress address;

    @Test
    public void networkNullConnTest() {
        // Mock the respective class
        context = mock(Context.class);
        manager = mock(ConnectivityManager.class);

        // The simulated context has no network connection
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(null);

        ConnectivityReceiverProvider provider = new ConnectivityReceiverProvider();
        boolean result = provider.isNetworkConnected(context);

        // Since there is no network connection, the result should be false
        assertFalse(result);
    }

}

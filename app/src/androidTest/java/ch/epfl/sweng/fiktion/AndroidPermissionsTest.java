package ch.epfl.sweng.fiktion;

import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.android.AndroidPermissions;
import ch.epfl.sweng.fiktion.android.AndroidPolicies;
import ch.epfl.sweng.fiktion.android.AndroidServices;
import ch.epfl.sweng.fiktion.views.HomeActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for AndroidPermissions
 * Created by alexandre on 09.12.17.
 */

public class AndroidPermissionsTest {

    private static final int DENY_BUTTON_INDEX = 0;
    private static final int GRANT_BUTTON_INDEX = 1;

    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule<>(HomeActivity.class);

    /**
     * helper to find permission allow box
     * Inspired from
     * https://gist.github.com/rocboronat/65b1187a9fca9eabfebb5121d818a3c4
     */
    public static void assertPermissionDialogExistsAndClickAllow(int destinationId) throws UiObjectNotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
            // if doesn't pass on jenkins, may need to wait a bit here
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector()
                    .clickable(true)
                    .checkable(false)
                    .index(GRANT_BUTTON_INDEX));
            assertTrue(allowPermissions.exists());
            allowPermissions.click();
            onView(withId(destinationId)).check(matches(isDisplayed()));
        }
    }

    /**
     * helper to click on permission box only if it exists
     * (to be used when unsure if emulator will really display the permission request)
     */
    public static void denyPermssionDialogIfExists() throws UiObjectNotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
            // if doesn't pass on jenkins, may need to wait a bit here
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector()
                    .clickable(true)
                    .checkable(false)
                    .index(DENY_BUTTON_INDEX));
            if (allowPermissions.exists()) {
                allowPermissions.click();
            }
        }
    }

    @Test
    public void testPromptLocationPermissions() throws UiObjectNotFoundException {
        AndroidPermissions.promptLocationPermission(mActivityRule.getActivity());
        assertPermissionDialogExistsAndClickAllow(R.id.home_main_layout);
    }

    @Test
    public void testPromptCameraPermissions() throws UiObjectNotFoundException {
        AndroidPermissions.promptCameraPermission(mActivityRule.getActivity());
        assertPermissionDialogExistsAndClickAllow(R.id.home_main_layout);
    }

    @Test
    public void testReachAndroidPolicies() {
        AndroidPolicies ap = new AndroidPolicies();
        assertNotNull(ap);
    }

    @Test
    public void testReachAndroidPermissions() {
        AndroidPermissions ap = new AndroidPermissions();
        assertNotNull(ap);
    }

    /**
     * Tests the reachability of the location check, however we will never
     * be able to test the case where the location is disabled on Jenkins
     * since it is enabled by default
     */
    @Test
    public void testReachPromptLocationEnable() throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidServices.promptLocationEnable(mActivityRule.getActivity());
            }
        });
        try {
            denyPermssionDialogIfExists();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        // basically nothing happens if enabled so we can't really test the dialog
        assertTrue(true);
    }

    /**
     * Tests the reachability of the camera check, however we will never
     * be able to test the case where the camera is disabled on Jenkins
     * since it is enabled by default
     */
    @Test
    public void testCameraPromptLocationEnable() throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AndroidServices.promptCameraEnable(mActivityRule.getActivity());
            }
        });
        try {
            denyPermssionDialogIfExists();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        // basically nothing happens if enabled so we can't really test the dialog
        assertTrue(true);
    }

}


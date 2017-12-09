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
import ch.epfl.sweng.fiktion.views.HomeActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

/**
 * Tests for AndroidPermissions
 * Created by alexandre on 09.12.17.
 */

public class AndroidPermissionsTest {

    private static final int GRANT_BUTTON_INDEX = 1;

    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule<>(HomeActivity.class);

    /**
     * helper to find permission allow box
     */
    public void assertPermissionDialogExistsAndClickAllow(int destinationId) throws UiObjectNotFoundException {
        if (Build.VERSION.SDK_INT >= 23) {
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
}


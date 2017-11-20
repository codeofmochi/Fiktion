package ch.epfl.sweng.fiktion;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.SettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests for SettingsActivity
 * Created by dialexo on 15.11.17.
 */

public class SettingsActivityTest {

    @Rule
    public final IntentsTestRule<SettingsActivity> testRule =
            new IntentsTestRule<>(SettingsActivity.class);

    @Test
    public void settingsDisplay() {
        onView(withId(R.id.accountSettingsTitle)).check(matches(isDisplayed()));
    }

}

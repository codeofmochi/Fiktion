package ch.epfl.sweng.fiktion;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.ProfileActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test class for profile activity
 * Created by alexandre on 16.11.17.
 */

public class ProfileActivityTest {

    @Rule
    public final IntentsTestRule<ProfileActivity> testRule =
            new IntentsTestRule<>(ProfileActivity.class);

    @Test
    public void profileDisplay() {
        onView(withId(R.id.profileMain)).check(matches(isDisplayed()));
    }
}

package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.NearbyListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class NearbyListActivityTest {

    @Rule
    public final ActivityTestRule<NearbyListActivity> nearbyActivityTestRule =
            new ActivityTestRule<>(NearbyListActivity.class);

    @Test
    public void initializeNearbyListActivity(){
        onView(withText("No nearby places found.")).check(matches(isDisplayed()));
    }
}

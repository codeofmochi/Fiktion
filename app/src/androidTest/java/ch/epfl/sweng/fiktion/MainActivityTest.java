package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainActivityTest {


    @Rule
    public final ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void goToHomeOnLaunch() {
        onView(withId(R.id.placeText)).check(matches(isDisplayed()));
    }
}

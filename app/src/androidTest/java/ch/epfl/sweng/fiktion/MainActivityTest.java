package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MainActivityTest {


    @Rule
    public final ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void goToSignIn(){
        onView(withId(R.id.signInButton)).perform(click());

        onView(withId(R.id.User_Email)).check(matches(isDisplayed()));
    }

    @Test
    public void goToNearbyPos(){
        onView(withId(R.id.main_findNearPois)).perform(click());

        onView(withId(R.id._findNearPois)).check(matches(isDisplayed()));
    }

    @Test
    public void goToHome(){
        onView(withId(R.id.startHomeButton)).perform(click());
        onView(withId(R.id.placeText)).check(matches(isDisplayed()));
    }
}

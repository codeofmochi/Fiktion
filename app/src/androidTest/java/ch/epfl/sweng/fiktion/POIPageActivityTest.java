package ch.epfl.sweng.fiktion;


import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Justinas on 03/11/2017.
 */

public class POIPageActivityTest {

    @Rule
    public final ActivityTestRule<POIPageActivity> toastRule =
            new ActivityTestRule<>(POIPageActivity.class);

    @BeforeClass
    public static void setProviders() {
        Providers.auth = new LocalAuthProvider();
        Providers.database = new LocalDatabaseProvider();
    }

    @Test
    public void buttonTest() {
        onView(withId(R.id.addPictureButton)).perform(ViewActions.scrollTo()).perform(click());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Camera")).inRoot(withDecorView(not(is(toastRule.getActivity()
                .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Camera")).perform(click());
    }

}

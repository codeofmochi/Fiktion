package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.views.tests.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/** tests an incorrect instanciation of the user profile
 * Created by Rodrigo on 27.10.2017.
 */

public class IncorrectUserDetailsActivityStartTest {

    @Rule
    public final ActivityTestRule<UserDetailsActivity> userDetActivityRule =
            new ActivityTestRule<>(UserDetailsActivity.class);
    @BeforeClass
    public static void setAuth(){
        AuthSingleton.auth = new LocalAuthProvider();
        AuthSingleton.auth.signOut();
    }

    @After
    public void reset() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void incorrectStart(){
        onView(withId(R.id.trendingLayout)).check(matches(isDisplayed()));
    }
}

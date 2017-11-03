package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.ProfileSettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/** Tests an incorrect start of the user profile editing
 * Created by Rodrigo on 02.11.2017.
 */

public class IncorrectProfileSettingsActivityStart {
    @Rule
    public final ActivityTestRule<ProfileSettingsActivity> userDetActivityRule =
            new ActivityTestRule<>(ProfileSettingsActivity.class);
    @BeforeClass
    public static void setAuth(){
        Providers.auth = new LocalAuthProvider();
        Providers.auth.signOut();
    }


    @Test
    public void incorrectStart(){
        onView(withId(R.id.trendingLayout)).check(matches(isDisplayed()));
    }
}

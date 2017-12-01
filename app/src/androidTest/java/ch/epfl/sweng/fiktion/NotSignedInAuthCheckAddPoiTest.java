package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.AddPOIActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/** AuthenticationChecks tests
 * Created by Rodrigo on 30.11.2017.
 */

public class NotSignedInAuthCheckAddPoiTest {
    @Rule
    public final ActivityTestRule<AddPOIActivity> mActivityRule =
            new ActivityTestRule<>(AddPOIActivity.class);

    @BeforeClass
    public static void setConfig(){
        Config.TEST_MODE = true;
        AuthProvider.getInstance().signOut();

    }

    @Test
    public void notSignedInContribute(){
        onView(withId(R.id.SignInButton)).check(matches(isDisplayed()));
    }
}

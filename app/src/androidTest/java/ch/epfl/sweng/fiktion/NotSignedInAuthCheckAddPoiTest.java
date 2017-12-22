package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.HomeActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * AuthenticationChecks tests
 * Created by Rodrigo on 30.11.2017.
 */

public class NotSignedInAuthCheckAddPoiTest {


    private final ViewInteraction homeMainLayout = onView(withId(R.id.home_main_layout));
    private final ViewInteraction menuDrawer = onView(withId(R.id.menu_drawer));

    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule<>(HomeActivity.class);


    private static ViewAction swipeRightFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    @BeforeClass
    public static void setConfig() {
        Config.TEST_MODE = true;
        AuthProvider.destroyInstance();
        AuthProvider.getInstance().signOut();
    }

    @After
    public void resetAuth(){
        AuthProvider.destroyInstance();
    }

    @Test
    public void notSignedInContributeAndSIgnIn() {
        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());

        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.SignInButton)).check(matches(isDisplayed()));

        onView(withId(R.id.User_Email)).perform(typeText("default@email.ch"), closeSoftKeyboard());
        onView(withId(R.id.User_Password)).perform(typeText("testing"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

    }

    @Test
    public void notProceedWithSignIn(){
        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());

        onView(withText("Return"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.home_main_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void notProceedWithProfileLogIn(){
        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(3).perform(click());

        onView(withText("Return"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.home_main_layout)).check(matches(isDisplayed()));
    }

}

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

import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.HomeActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.IsNot.not;

public class HomeActivityTest {

    @Rule
    public final ActivityTestRule<HomeActivity> homeActivityActivityTestRule =
            new ActivityTestRule<>(HomeActivity.class);

    private final ViewInteraction homeMainLayout = onView(withId(R.id.home_main_layout));
    private final ViewInteraction menuDrawer = onView(withId(R.id.menu_drawer));

    private static ViewAction swipeRightFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    private static ViewAction swipeLeftFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER);
    }

    @BeforeClass
    public static void resetProviders() {
        Providers.auth = new LocalAuthProvider();
        Providers.database = new LocalDatabaseProvider();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void reset(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void menuDrawerOpensAndClosesOnSwipe() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        homeMainLayout.perform(swipeLeftFast());
        menuDrawer.check(matches(not(isDisplayed())));
    }
    @Test
    public void homeToHomeWhenHomeClicked() {
        closeSoftKeyboard();

        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(0).perform(click());
        menuDrawer.check(matches(not(isDisplayed())));
    }
    @Test
    public void backHomeWhenHomeClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(4).perform(click());
        closeSoftKeyboard();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.add_poi_scroll)).perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(0).perform(click());
        homeMainLayout.check(matches(isDisplayed()));
    }
    /*
    public void showMapWhenNearbyClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(1).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
    */
    // Will be modified when linked
    @Test
    public void CloseDrawerWhenProfileClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(2).perform(click());
        homeMainLayout.check(matches(isDisplayed()));
    }
    // Will be modified when linked
    @Test
    public void CloseDrawerWhenDiscoverClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(3).perform(click());
        homeMainLayout.check(matches(isDisplayed()));
    }

    @Test
    public void AddPoiToAddPoiWhenContributeClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(4).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.add_poi_scroll)).perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(4).perform(click());
        menuDrawer.check(matches(not(isDisplayed())));
    }
    // Will be modified when linked
    @Test
    public void CloseDrawerWhenSettingsClicked() {
        closeSoftKeyboard();
        homeMainLayout.perform(swipeRightFast());
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());
        homeMainLayout.check(matches(isDisplayed()));
    }
}

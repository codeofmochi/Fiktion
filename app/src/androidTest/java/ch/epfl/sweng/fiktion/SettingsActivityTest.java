package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import junit.framework.Assert;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.SettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

/**
 * Tests for SettingsActivity
 * Created by dialexo on 15.11.17.
 */

public class SettingsActivityTest {

    @Rule
    public final IntentsTestRule<SettingsActivity> testRule =
            new IntentsTestRule<>(SettingsActivity.class, true, false);

    @BeforeClass
    public static void setConfig() {
        Config.TEST_MODE = true;
    }

    @After
    public void resetProvider() {
        AuthProvider.destroyInstance();
        DatabaseProvider.destroyInstance();
        Config.settings = new Settings(Settings.DEFAULT_SEARCH_RADIUS);
    }

    @Test
    public void settingsDisplay() {
        Intent i = new Intent();
        testRule.launchActivity(i);

        onView(withId(R.id.accountSettingsTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void updateMaxSearchRadiusWhileConnected() {

        Intent i = new Intent();
        testRule.launchActivity(i);

        assertThat(Config.settings.getSearchRadius(), is(20));

        onView(withId(R.id.searchRadiusSlider)).perform(scrollTo()).perform(
                new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_RIGHT, Press.FINGER));
        onView(withId(R.id.searchRadiusNum)).check(matches(withText("200")));

        assertThat(Config.settings.getSearchRadius(), is(200));
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                assertThat(user.getSettings().getSearchRadius(), is(200));
            }

            @Override
            public void onModified(User user) {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {

            }
        });

    }



    @Test
    public void updateMinSearchRadiusWhileConnected() {
        Intent i = new Intent();
        testRule.launchActivity(i);

        assertThat(Config.settings.getSearchRadius(), is(20));

        onView(withId(R.id.searchRadiusSlider)).perform(scrollTo()).perform(
                new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER));
        onView(withId(R.id.searchRadiusNum)).check(matches(withText("1")));

        assertThat(Config.settings.getSearchRadius(), is(1));
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                assertThat(user.getSettings().getSearchRadius(), is(1));
            }

            @Override
            public void onModified(User user) {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Test
    public void updateSearchBarwhileDisconnected() {
        AuthProvider.getInstance().signOut();

        Intent i = new Intent();
        testRule.launchActivity(i);
        assertThat(Config.settings.getSearchRadius(), is(20));


        onView(withId(R.id.searchRadiusSlider)).perform(
                new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER));
        onView(withId(R.id.searchRadiusNum)).check(matches(withText("1")));

        assertThat(Config.settings.getSearchRadius(), is(1));
    }

}

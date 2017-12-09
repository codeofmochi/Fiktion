package ch.epfl.sweng.fiktion;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.ProfileActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test class for profile activity
 * Created by alexandre on 16.11.17.
 */

public class ProfileActivityTest {

    @Rule
    public final IntentsTestRule<ProfileActivity> testRule =
            new IntentsTestRule<>(ProfileActivity.class);

    @BeforeClass
    public static void setProviders() {
        Config.TEST_MODE = true;

        AuthProvider.getInstance().signIn("default@email.ch", "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        });
        AuthProvider.getInstance().sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
            }

            public void onModified(User user) {

            }

            @Override
            public void onFailure() {
            }
        });
    }

    @After
    public void resetProviders() {
        PhotoProvider.destroyInstance();
        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
    }

    @AfterClass
    public static void clean() {
        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
        PhotoProvider.destroyInstance();
    }

    @Test
    public void profileDisplay() {
        onView(withId(R.id.profileMain)).check(matches(isDisplayed()));
    }

    @Test
    public void clickPlacesButtonDisplaysUserPlaces() {
        onView(withId(R.id.placesLink)).perform(click());
        onView(withId(R.id.poiList)).check(matches(isDisplayed()));
    }

    @Test
    public void clickPicturesButtonDisplaysUserPictures() {
        onView(withId(R.id.picturesLink)).perform(click());
        onView(withId(R.id.user_pictures_main)).check(matches(isDisplayed()));
    }

    @Test
    public void clickFriendsButtonDisplayUserFriends() {
        onView(withId(R.id.friendsLink)).perform(click());
        onView(withId(R.id.friends_list)).check(matches(isDisplayed()));
    }

    @Test
    public void clickPointsButtonDisplayUserAchievements() {
        onView(withId(R.id.achievementsLink)).perform(click());
        onView(withId(R.id.user_achievements_main)).check(matches(isDisplayed()));
    }
}

package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Config;
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
 * This class is meant to test the behaviour of the poi page activity when user is not signed in ( nor has verified account)
 * Created by Rodrigo on 03.12.2017.
 */

public class NotSignedInPoiPageActivity {

    @Rule
    public final IntentsTestRule<POIPageActivity> mActivityRule =
            new IntentsTestRule<>(POIPageActivity.class, true, false);

    @BeforeClass
    public static void setConfig() {
        AuthProvider.getInstance().signOut();
        Config.TEST_MODE = true;
        DatabaseProvider.getInstance().addPoi(new PointOfInterest("poiTest", new Position(3, 4), new TreeSet<String>(), "", 0, "", ""), new DatabaseProvider.AddPoiListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onAlreadyExists() {
            }

            @Override
            public void onFailure() {
            }
        });
    }

    @After
    public void resetProviders() {
        AuthProvider.destroyInstance();
        AuthProvider.getInstance().signOut();
    }

    @Test
    public void clickVote() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        mActivityRule.launchActivity(i);
        onView(withId(R.id.upvoteButton)).perform(click());
        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Return"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
    }

    @Test
    public void clickFavourites() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        mActivityRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Favourite"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Favourite"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void clickWishlist() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        mActivityRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Wishlist"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Wishlist"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void clickEdit() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        mActivityRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Edit"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Edit"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }


    @Test
    public void clickAddPicture() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        mActivityRule.launchActivity(i);
        onView(withId(R.id.addPictureButton)).perform(click());
        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Return"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
    }

    @Test
    public void clickAddReview() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        mActivityRule.launchActivity(i);

        onView(withId(R.id.addReviewButton)).perform(ViewActions.scrollTo()).perform(click());
        onView(withText("Sign In"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withText("Return"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
    }

}

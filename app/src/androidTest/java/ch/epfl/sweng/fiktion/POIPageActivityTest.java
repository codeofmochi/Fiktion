package ch.epfl.sweng.fiktion;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;
import android.widget.LinearLayout;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.fiktion.providers.PhotoProvider.ALL_PHOTOS;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests for POIPageActivity
 * Created by Justinas on 03/11/2017.
 */

public class POIPageActivityTest {


    @Rule
    public final IntentsTestRule<POIPageActivity> toastRule =
            new IntentsTestRule<>(POIPageActivity.class, true, false);

    @BeforeClass
    public static void setProviders() {
        //providers.getInstance will return localProviders
        Config.TEST_MODE = true;
        AuthProvider.getInstance().sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
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
        AuthProvider.getInstance().signIn("default@email.ch", "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        });
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
            }

            @Override
            public void onDoesntExist() {
            }

            @Override
            public void onFailure() {
            }
        });


    }


    @Before
    public void beforeReset(){

        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
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
        AuthProvider.getInstance().signIn("default@email.ch", "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        });
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
            }

            @Override
            public void onDoesntExist() {
            }

            @Override
            public void onFailure() {
            }
        });
        AuthProvider.getInstance().sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {

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
    public void buttonTest() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.addPictureButton)).perform(ViewActions.scrollTo()).perform(click());

        onView(withText("Camera")).inRoot(withDecorView(not(is(toastRule.getActivity()
                .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        onView(withText("Cancel")).perform(click());
    }

    @Test
    public void cameraTest() {

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);

        onView(withId(R.id.addPictureButton)).perform(ViewActions.scrollTo()).perform(click());

        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);

        // Build a result to return from the Camera app
        // this tells Espresso to respond with this instead of camera
        intending(toPackage("com.android.camera2")).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
        onView(withText("Camera")).check(matches(isDisplayed()));
        onView(withText("Camera")).perform(click());

        final List<Bitmap> bitmaps = new ArrayList<>();

        PhotoProvider.getInstance().downloadPOIBitmaps("poiTest", ALL_PHOTOS, new PhotoProvider.DownloadBitmapListener() {
            @Override
            public void onNewPhoto(Bitmap b) {
                bitmaps.add(b);
            }

            @Override
            public void onFailure() {

            }
        });

        assertThat(bitmaps.size(), is(1));
    }

    @Test
    public void loadPhotosTest() {
        Bitmap b = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);
        PhotoProvider.getInstance().uploadPOIBitmap(b, "poiTest", new PhotoProvider.UploadPhotoListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void updateProgress(double progress) {
            }
        });

        b = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher_round);
        PhotoProvider.getInstance().uploadPOIBitmap(b, "poiTest", new PhotoProvider.UploadPhotoListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void updateProgress(double progress) {
            }
        });

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.imageLayout)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertThat(((LinearLayout) view).getChildCount(), is(2));
            }
        });
    }

    private User user;

    private void setUser(User user) {
        this.user = user;
    }


    @Test
    public void voteTest() {
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                setUser(user);
            }

            @Override
            public void onDoesntExist() {
            }

            @Override
            public void onFailure() {
            }
        });

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);

        ViewInteraction upvoteButton = onView(withId(R.id.upvoteButton));
        upvoteButton.perform(click());
        upvoteButton.check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertTrue(view.isEnabled());
            }
        });
        assertTrue(user.getUpvoted().contains("poiTest"));
        upvoteButton.perform(click());
        upvoteButton.check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertTrue(view.isEnabled());
            }
        });
        assertFalse(user.getUpvoted().contains("poiTest"));
    }



    // edit a POI

    private static ViewAction swipeUpCenterTopFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.TOP_CENTER, Press.FINGER);
    }
/*
    @Test
    public void testFavourite(){
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                setUser(user);
            }

            @Override
            public void onDoesntExist() {
            }

            @Override
            public void onFailure() {
            }
        });

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);

        onView(withId(R.id.moreMenu)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Favourite")).perform(click());

        assertTrue(user.getFavourites().contains("poiTest"));

    }

    @Test
    public void testWishlist(){
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                setUser(user);
            }

            @Override
            public void onDoesntExist() {
            }

            @Override
            public void onFailure() {
            }
        });

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);

        onView(withId(R.id.moreMenu)).perform(click());
        onView(withId(R.id.moreMenu)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Wishlist")).perform(click());
        assertTrue(user.getWishlist().contains("poiTest"));

    }
*/
    @Test
    public void testModifyExistingPoi() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);

        // click edit button
        onView(withId(R.id.moreMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Edit")).perform(click());
        onView(withId(R.id.add_poi_scroll)).check(matches(isDisplayed()));


        ViewInteraction addPoiFiction = onView(withId(R.id.add_poi_fiction));
        ViewInteraction addPoiFictionButton = onView(withId(R.id.add_poi_fiction_button));
        ViewInteraction addPoiLatitude = onView(withId(R.id.add_poi_latitude));
        ViewInteraction addPoiLongitude = onView(withId(R.id.add_poi_longitude));

        closeSoftKeyboard();
        addPoiFiction.perform(typeText("fiction"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiLatitude.perform(clearText());
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(clearText());
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        onView(withId(R.id.add_poi_city)).perform(typeText("city"));
        closeSoftKeyboard();
        onView(withId(R.id.add_poi_country)).perform(typeText("country"));
        closeSoftKeyboard();
        onView(withId(R.id.add_poi_scroll)).perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        onView(withId(R.id.add_poi_finish)).perform(click());
        onView(withId(R.id.menu_scroll)).perform(swipeUpCenterTopFast());
        onView(withId(R.id.title)).check(matches(withText("poiTest")));
        onView(withId(R.id.featured)).check(matches(withText("Featured in fiction")));
        onView(withId(R.id.cityCountry)).check(matches(withText("city, country")));
    }

    @Test
    public void writeComment() {

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        i.putExtra("USER_NAME", "default");
        toastRule.launchActivity(i);

        onView(withId(R.id.addReviewButton)).perform(ViewActions.scrollTo()).perform(click());
        onView(withId(R.id.comment)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadCommentButton)).perform(click());
        onView(withId(R.id.comment)).check(matches(hasErrorText("You can't add an empty comment")));

        onView(withId(R.id.comment)).perform(typeText("this is a test"));
        closeSoftKeyboard();
        onView(withId(R.id.uploadCommentButton)).perform(click());
        onView(withId(R.id.nearbyTitle)).perform(ViewActions.scrollTo());
        onView(withText("this is a test")).check(matches(isDisplayed()));
    }

    @Test
    public void fullscreenTest() {
        Bitmap b = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);
        PhotoProvider.getInstance().uploadPOIBitmap(b, "poiTest", new PhotoProvider.UploadPhotoListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void updateProgress(double progress) {
            }
        });

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);

        onView(withId(R.id.imgSlider)).perform(ViewActions.scrollTo());
        onView(withParent(withId(R.id.imgSlider))).perform(click());
        onView(withId(R.id.fullScreen)).check(matches(isDisplayed()));
    }

}

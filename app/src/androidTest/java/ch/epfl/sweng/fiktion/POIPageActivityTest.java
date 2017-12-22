package ch.epfl.sweng.fiktion;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
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

import junit.framework.Assert;

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
import ch.epfl.sweng.fiktion.providers.CurrentLocationProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
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

    private static PointOfInterest poiTest = new PointOfInterest("poiTest", new Position(3, 4), new TreeSet<String>(), "", 0, "", "");

    private static DatabaseProvider.AddPOIListener emptyAddPOIListener = new DatabaseProvider.AddPOIListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onAlreadyExists() {
        }

        @Override
        public void onFailure() {
        }
    };

    @BeforeClass
    public static void setProviders() {
        //providers.getInstance will return localProviders
        Config.TEST_MODE = true;
        DatabaseProvider.getInstance().addPOI(poiTest, emptyAddPOIListener);

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


    @Before
    public void beforeReset() {

        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
        CurrentLocationProvider.destroyInstance();
        DatabaseProvider.getInstance().addPOI(new PointOfInterest("poiTest", new Position(3, 4), new TreeSet<String>(), "", 0, "", ""), new DatabaseProvider.AddPOIListener() {
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
        i.putExtra("POI_NAME", poiTest.name());
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
        i.putExtra("POI_NAME", poiTest.name());
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

        PhotoProvider.getInstance().getPOIPhotoNames(poiTest.name(), ALL_PHOTOS, new PhotoProvider.GetPhotoNamesListener() {
            @Override
            public void onNewValue(String photoName) {
                PhotoProvider.getInstance().downloadPOIBitmap(poiTest.name(), photoName, new PhotoProvider.DownloadBitmapListener() {
                    @Override
                    public void onNewValue(Bitmap b) {
                        bitmaps.add(b);
                    }

                    @Override
                    public void onFailure() {
                    }

                });
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
        PhotoProvider.getInstance().uploadPOIBitmap(b, poiTest.name(), new PhotoProvider.UploadUserPhotoListener() {
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
        PhotoProvider.getInstance().uploadPOIBitmap(b, poiTest.name(), new PhotoProvider.UploadUserPhotoListener() {
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
        i.putExtra("POI_NAME", poiTest.name());
        toastRule.launchActivity(i);
        onView(withId(R.id.imageLayout)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertThat(((LinearLayout) view).getChildCount(), is(2));
            }
        });

        assertTrue(toastRule.getActivity().deleteFile(poiTest.name() + "0"));
        assertTrue(toastRule.getActivity().deleteFile(poiTest.name() + "1"));
    }

    private User user;

    private void setUser(User newUser) {
        user = newUser;
    }


    @Test
    public void voteTest() {
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User user) {
                setUser(user);
            }

            @Override
            public void onModifiedValue(User user) {

            }

            @Override
            public void onDoesntExist() {
            }

            @Override
            public void onFailure() {
            }
        });

        Intent i = new Intent();
        i.putExtra("POI_NAME", poiTest.name());
        toastRule.launchActivity(i);

        ViewInteraction upvoteButton = onView(withId(R.id.upvoteButton));
        ViewInteraction upvotes = onView(withId(R.id.upvotes));

        upvoteButton.perform(click());
        upvoteButton.check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertTrue(view.isEnabled());
            }
        });
        assertTrue(user.getUpvoted().contains(poiTest.name()));
        upvotes.check(matches(withText("1 upvotes")));

        upvoteButton.perform(click());
        upvoteButton.check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertTrue(view.isEnabled());
            }
        });
        assertFalse(user.getUpvoted().contains(poiTest.name()));
        upvotes.check(matches(withText("0 upvotes")));
    }


    // edit a POI

    private static ViewAction swipeUpCenterTopFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.TOP_CENTER, Press.FINGER);
    }

    @Test
    public void testModifyExistingPoi() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", poiTest.name());
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
        onView(withId(R.id.title)).check(matches(withText(poiTest.name())));
        onView(withId(R.id.featured)).check(matches(withText("Featured in fiction")));
        onView(withId(R.id.cityCountry)).check(matches(withText("city, country")));
    }

    @Test
    public void writeComment() {

        Intent i = new Intent();
        i.putExtra("POI_NAME", poiTest.name());
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
        PhotoProvider.getInstance().uploadPOIBitmap(b, "poiTest", new PhotoProvider.UploadUserPhotoListener() {
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

    @Test
    public void addFavouriteSuccessTest() {

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Favourite")).perform(click());
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User user) {
                assertThat(user.getFavourites().contains("poiTest"), is(true));
            }

            @Override
            public void onModifiedValue(User user) {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void addFavouriteDoesntExistsTest() {

        ((LocalAuthProvider) AuthProvider.getInstance()).currUser = new User("FAVOURITE", "MODIFYUSERD");
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Favourite")).perform(click());
    }

    @Test
    public void addFavouriteFailureTest() {

        ((LocalAuthProvider) AuthProvider.getInstance()).currUser = new User("FAVOURITE", "MODIFYUSERF");
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Favourite")).perform(click());
    }

    @Test
    public void addWishlistSuccessTest() {

        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Wishlist")).perform(click());
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onNewValue(User user) {
                assertThat(user.getWishlist().contains("poiTest"), is(true));
            }

            @Override
            public void onModifiedValue(User user) {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void addWishlistDoesntExistsTest() {

        ((LocalAuthProvider) AuthProvider.getInstance()).currUser = new User("WISHLIST", "MODIFYUSERD");
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Wishlist")).perform(click());
    }

    @Test
    public void addWishlistFailureTest() {

        ((LocalAuthProvider) AuthProvider.getInstance()).currUser = new User("WISHLIST", "MODIFYUSERF");
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Wishlist")).perform(click());
    }

    @Test
    public void addToVisited() {
        PointOfInterest poiClose = new PointOfInterest("poiClose", new Position(6.56, 46.5167), new TreeSet<String>(), "", 0, "", "");
        DatabaseProvider.getInstance().addPOI(poiClose, emptyAddPOIListener);
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiClose");
        toastRule.launchActivity(i);

        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }

            @Override
            public void onNewValue(User value) {
                assertThat(value.getVisited().contains("poiClose"), is(true));
            }

            @Override
            public void onModifiedValue(User value) {
                Assert.fail();
            }
        });
    }

}

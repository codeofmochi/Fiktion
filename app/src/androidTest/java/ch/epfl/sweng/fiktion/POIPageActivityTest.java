package ch.epfl.sweng.fiktion;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;
import android.widget.LinearLayout;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalPhotoProvider;
import ch.epfl.sweng.fiktion.providers.PhotoProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.fiktion.providers.AuthSingleton.auth;
import static ch.epfl.sweng.fiktion.providers.DatabaseSingleton.database;
import static ch.epfl.sweng.fiktion.providers.PhotoProvider.ALL_PHOTOS;
import static ch.epfl.sweng.fiktion.providers.PhotoSingleton.photoProvider;
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
        Config.TEST_MODE = true;
        auth = new LocalAuthProvider();
        database = new LocalDatabaseProvider();
        photoProvider = new LocalPhotoProvider();
        DatabaseSingleton.database.addPoi(new PointOfInterest("poiTest", new Position(3, 4), new TreeSet<String>(), "", 0, "", ""), new DatabaseProvider.AddPoiListener() {
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

    @Test
    public void buttonTest() {
        Intent i = new Intent();
        i.putExtra("POI_NAME", "poiTest");
        toastRule.launchActivity(i);
        onView(withId(R.id.addPictureButton)).perform(ViewActions.scrollTo()).perform(click());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));

        onView(withText("Camera")).perform(click());

        final List<Bitmap> bitmaps = new ArrayList<>();

        photoProvider.downloadPOIBitmaps("poiTest", ALL_PHOTOS, new PhotoProvider.DownloadBitmapListener() {
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
        photoProvider = new LocalPhotoProvider();
        Bitmap b = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);
        photoProvider.uploadPOIBitmap(b, "poiTest", new PhotoProvider.UploadPhotoListener() {
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
        photoProvider.uploadPOIBitmap(b, "poiTest", new PhotoProvider.UploadPhotoListener() {
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

}

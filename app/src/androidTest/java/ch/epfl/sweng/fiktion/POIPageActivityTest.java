package ch.epfl.sweng.fiktion;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.AuthSingleton;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests for POIPageActivity
 * Created by Justinas on 03/11/2017.
 */

public class POIPageActivityTest {

    @Rule
    public final IntentsTestRule<POIPageActivity> toastRule =
            new IntentsTestRule<POIPageActivity>(POIPageActivity.class);


    @BeforeClass
    public static void setProviders() {
        AuthSingleton.auth = new LocalAuthProvider();
        DatabaseSingleton.database = new LocalDatabaseProvider();
    }

    @Test
    public void buttonTest() {
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

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (onView(withId(R.id.image1)) == null) {
            assert true;
        }

    }



}

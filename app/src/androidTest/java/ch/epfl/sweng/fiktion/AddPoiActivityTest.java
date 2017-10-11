package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by pedro on 11/10/17.
 */

@RunWith(AndroidJUnit4.class)
public class AddPoiActivityTest {
    final static String poiTestName1 = "poiTest1";
    final static String poiTestName2 = "poiTest2";

    @Rule
    public final ActivityTestRule<AddPOIActivity> mActivityRule =
            new ActivityTestRule<>(AddPOIActivity.class);

    private static void removePoiTestName(String poiTestName) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference poiRef = db.child("Points of interest").child(poiTestName);
        poiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    poiRef.removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @BeforeClass public static void setup() {
        removePoiTestName(poiTestName1);
        removePoiTestName(poiTestName2);
    }

    @Test
    public void testCanAddPOI() {
        onView(withId(R.id.poiName)).perform(typeText(poiTestName1));
        onView(withId(R.id.addPOIButton)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.addConfirm)).check(matches(withText(poiTestName1 + " added")));
    }

    @Test
    public void testDoesntAddTwice() {
        onView(withId(R.id.poiName)).perform(typeText(poiTestName2));
        onView(withId(R.id.addPOIButton)).perform(click());
        onView(withId(R.id.poiName)).perform(typeText(poiTestName2));
        onView(withId(R.id.addPOIButton)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.addConfirm)).check(matches(withText(poiTestName2 + " already exists")));
    }

    @Test
    public void addingFailsOnEmptyString() {
        onView(withId(R.id.poiName)).perform(typeText(""));
        onView(withId(R.id.addPOIButton)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.addConfirm)).check(matches(withText("Write the name of your Point of interest")));
    }

    @AfterClass public static void cleanup() {
        removePoiTestName(poiTestName1);
        removePoiTestName(poiTestName2);
    }
}

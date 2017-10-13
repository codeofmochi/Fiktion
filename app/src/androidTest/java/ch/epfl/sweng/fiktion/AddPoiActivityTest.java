package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;
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
    private ViewInteraction poiNameView =  onView(withId(R.id.poiName));
    private ViewInteraction addPoiButtonView = onView(withId(R.id.addPOIButton));
    private ViewInteraction confirmTextView = onView(withId(R.id.addConfirmationText));

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
        poiNameView.perform(typeText(poiTestName1));
        addPoiButtonView.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        confirmTextView.check(matches(withText(poiTestName1 + " added")));
    }

    @Test
    public void testDoesntAddTwice() {
        poiNameView.perform(typeText(poiTestName2));
        addPoiButtonView.perform(click());
        poiNameView.perform(typeText(poiTestName2));
        addPoiButtonView.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        confirmTextView.check(matches(withText(poiTestName2 + " already exists")));
    }

    @Test
    public void addingFailsOnEmptyString() {
        poiNameView.perform(typeText(""));
        addPoiButtonView.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        confirmTextView.check(matches(withText("Please write the name of your Point of interest")));
    }

    @Test
    public void addingFailsWithDot() {
        poiNameView.perform(typeText("."));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void addingFailsWithDollar() {
        poiNameView.perform(typeText("$"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void addingFailsWithHash() {
        poiNameView.perform(typeText("#"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void addingFailsWithOpenBracket() {
        poiNameView.perform(typeText("["));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void addingFailsWithCloseBracket() {
        poiNameView.perform(typeText("]"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void addingFailsWithSlash() {
        poiNameView.perform(typeText("/"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Those characters are not accepted: . $ # [ ] /")));
    }

    @AfterClass public static void cleanup() {
        removePoiTestName(poiTestName1);
        removePoiTestName(poiTestName2);
    }
}

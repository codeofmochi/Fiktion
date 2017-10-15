package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by pedro on 11/10/17.
 */

public class AddPoiActivityTest {
    private final ViewInteraction poiNameView = onView(withId(R.id.poiName));
    private final ViewInteraction addPoiButtonView = onView(withId(R.id.addPOIButton));
    private final ViewInteraction confirmTextView = onView(withId(R.id.addConfirmationText));
    private final String unacceptedCharactersWarning = "Those characters are not accepted: . $ # [ ] /";

    @Rule
    public final ActivityTestRule<AddPOIActivity> mActivityRule =
            new ActivityTestRule<>(AddPOIActivity.class);

    @BeforeClass
    public static void setDatabase() {
        DatabaseActivity.database = new LocalDatabaseProvider();
    }

    @Test
    public void testCanAddPOI() {
        String poiTestName1 = "poiTest1";
        poiNameView.perform(typeText(poiTestName1));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(poiTestName1 + " added")));
    }

    @Test
    public void addingFailsOnSecondAdd() {
        String poiTestName2 = "poiTest2";
        poiNameView.perform(typeText(poiTestName2));
        addPoiButtonView.perform(click());
        poiNameView.perform(typeText(poiTestName2));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(poiTestName2 + " already exists")));
    }

    @Test
    public void addingFailsOnEmptyString() {
        poiNameView.perform(typeText(""));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText("Please write the name of your Point of interest")));
    }

    @Test
    public void addingFailsWithDot() {
        poiNameView.perform(typeText("."));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(unacceptedCharactersWarning)));
    }

    @Test
    public void addingFailsWithDollar() {
        poiNameView.perform(typeText("$"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(unacceptedCharactersWarning)));
    }

    @Test
    public void addingFailsWithHash() {
        poiNameView.perform(typeText("#"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(unacceptedCharactersWarning)));
    }

    @Test
    public void addingFailsWithOpenBracket() {
        poiNameView.perform(typeText("["));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(unacceptedCharactersWarning)));
    }

    @Test
    public void addingFailsWithCloseBracket() {
        poiNameView.perform(typeText("]"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(unacceptedCharactersWarning)));
    }

    @Test
    public void addingFailsWithSlash() {
        poiNameView.perform(typeText("/"));
        addPoiButtonView.perform(click());
        confirmTextView.check(matches(withText(unacceptedCharactersWarning)));
    }
}

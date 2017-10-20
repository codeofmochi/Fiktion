package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.widget.Button;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.views.AddPOIActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.fiktion.providers.Providers.database;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by pedro on 20/10/17.
 */

public class AddPOIActivityTest {
    @Rule
    public final ActivityTestRule<AddPOIActivity> mActivityRule =
            new ActivityTestRule<>(AddPOIActivity.class);

    private static DatabaseProvider.AddPoiListener emptyAddPoiListener = new DatabaseProvider.AddPoiListener() {
        @Override
        public void onSuccess() {}

        @Override
        public void onAlreadyExists() {}

        @Override
        public void onFailure() {}
    };

    @BeforeClass
    public static void setup() {
        database = new LocalDatabaseProvider();
        database.addPoi(new PointOfInterest("p1", new Position(0,1)),emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p2", new Position(1,2)),emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p3", new Position(2,3)),emptyAddPoiListener);
    }

    private ViewInteraction addPoiFinish = onView(withId(R.id.add_poi_finish));
    private ViewInteraction addPoiName = onView(withId(R.id.add_poi_name));

    public void doesToastMatch(String s) {
        onView(withText(s)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void failsOnNoTextTest() {
        addPoiFinish.perform(click());
        doesToastMatch("You can't enter an empty fiction name");
    }

    @Test
    public void failsWithDotTest() {
        addPoiName.perform(typeText("."));
        addPoiFinish.perform(click());
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithDollarTest() {
        addPoiName.perform(typeText("$"));
        addPoiFinish.perform(click());
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithHashTest() {
        addPoiName.perform(typeText("#"));
        addPoiFinish.perform(click());
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithOpenBracketTest() {
        addPoiName.perform(typeText("["));
        addPoiFinish.perform(click());
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithCloseBracketTest() {
        addPoiName.perform(typeText("]"));
        addPoiFinish.perform(click());
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithSlashTest() {
        addPoiName.perform(typeText("/"));
        addPoiFinish.perform(click());
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }
}

package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.views.AddPOIActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
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
    private ViewInteraction addPoiLatitude = onView(withId(R.id.add_poi_latitude));
    private ViewInteraction addPoiLongitude = onView(withId(R.id.add_poi_longitude));
    private ViewInteraction addPoiFiction= onView(withId(R.id.add_poi_fiction));
    private ViewInteraction addPoiFictionButton = onView(withId(R.id.add_poi_fiction_button));
    private ViewInteraction addPoiFictionList = onView(withId(R.id.add_poi_fiction_list));

    private void doesToastMatch(String s) {
        onView(withText(s)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void waitASecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void failsOnNoTextTest() {
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("You can't enter an empty point of interest name");
    }

    @Test
    public void failsWithDotTest() {
        addPoiName.perform(typeText("."));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithDollarTest() {
        addPoiName.perform(typeText("$"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithHashTest() {
        addPoiName.perform(typeText("#"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithOpenBracketTest() {
        addPoiName.perform(typeText("["));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithCloseBracketTest() {
        addPoiName.perform(typeText("]"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWithSlashTest() {
        addPoiName.perform(typeText("/"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void failsWIthEmptyLatitudeOrLongitudeTest() {
        addPoiName.perform(typeText("poiTest1"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter valid coordinates");
        addPoiLatitude.perform(typeText("15"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter valid coordinates");
        addPoiLongitude.perform(typeText("30"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter valid coordinates");
    }

    @Test
    public void failsWithOutOfBoundsCoordinatesTest() {
        addPoiName.perform(typeText("poiTest2"));
        addPoiLatitude.perform(typeText("45"));
        addPoiLongitude.perform(typeText("220"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter coordinates in range -90 to 90 for latitude and -180 to 180 for longitude");
        addPoiLatitude.perform(typeText("100"));
        addPoiLongitude.perform(typeText("60"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter coordinates in range -90 to 90 for latitude and -180 to 180 for longitude");
        addPoiLatitude.perform(typeText("-120"));
        addPoiLongitude.perform(typeText("60"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter coordinates in range -90 to 90 for latitude and -180 to 180 for longitude");
        addPoiLatitude.perform(typeText("30"));
        addPoiLongitude.perform(typeText("-200"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter coordinates in range -90 to 90 for latitude and -180 to 180 for longitude");
    }

    @Test
    public void failsOnWrongCoordinateInputTest() {
        addPoiName.perform(typeText("poiTest4"));
        addPoiLatitude.perform(typeText("32#2"));
        addPoiLongitude.perform(typeText("1:2"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter valid coordinates");
        addPoiLatitude.perform(typeText("45.3"));
        addPoiLongitude.perform(typeText("56:2"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter valid coordinates");
        addPoiLatitude.perform(typeText("16#2"));
        addPoiLongitude.perform(typeText("02"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("Please enter valid coordinates");
    }

    @Test
    public void succeedsOnCorrectInputsTest() {
        addPoiName.perform(typeText("poiTest4"));
        addPoiLatitude.perform(typeText("45"));
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The Point of Interest poiTest4 was added !");
    }

    @Test
    public void failsOnAddingTwiceTest() {
        addPoiName.perform(typeText("poiTest5"));
        addPoiLatitude.perform(typeText("45"));
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The Point of Interest poiTest5 was added !");
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The Point of Interest poiTest5 already exists !");
    }

    @Test
    public void addingFictionFailsOnWrongInputTest() {
        addPoiFiction.perform(typeText("["));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
        addPoiFiction.perform(typeText("hello$test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
        addPoiFiction.perform(typeText("hello[test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
        addPoiFiction.perform(typeText("hello]test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
        addPoiFiction.perform(typeText("hello.test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
        addPoiFiction.perform(typeText("hello/test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        waitASecond();
        doesToastMatch("Those characters are not accepted: . $ # [ ] /");
    }

    @Test
    public void addingFictionWorksTest() {
        addPoiFiction.perform(typeText("hello test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFictionList.check(matches(withText("hello test")));
        addPoiFiction.perform(typeText("my fiction"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFictionList.check(matches(withText("hello test, my fiction")));
    }
}

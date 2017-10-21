package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
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
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
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
    public static void setup() {
        database = new LocalDatabaseProvider();
        database.addPoi(new PointOfInterest("p1", new Position(0, 1)), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p2", new Position(1, 2)), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p3", new Position(2, 3)), emptyAddPoiListener);
    }

    private ViewInteraction addPoiFinish = onView(withId(R.id.add_poi_finish));
    private ViewInteraction addPoiName = onView(withId(R.id.add_poi_name));
    private ViewInteraction addPoiLatitude = onView(withId(R.id.add_poi_latitude));
    private ViewInteraction addPoiLongitude = onView(withId(R.id.add_poi_longitude));
    private ViewInteraction addPoiFiction = onView(withId(R.id.add_poi_fiction));
    private ViewInteraction addPoiFictionButton = onView(withId(R.id.add_poi_fiction_button));
    private ViewInteraction addPoiFictionList = onView(withId(R.id.add_poi_fiction_list));
    private ViewInteraction addPoiScroll = onView(withId(R.id.add_poi_scroll));

    private void doesToastMatch(String s) {
        onView(withText(s)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private void waitASecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static ViewAction swipeUpCenterTopFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.TOP_CENTER, Press.FINGER);
    }

    private static ViewAction swipeDownCenterBottomFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.BOTTOM_CENTER, Press.FINGER);
    }

    @Test
    public void failsOnNoTextTest() {
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("You can't enter an empty point of interest name")));
    }

    @Test
    public void failsWithDotTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("."));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWithDollarTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("$"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWithHashTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("#"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWithOpenBracketTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("["));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWithCloseBracketTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("]"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWithSlashTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("/"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWIthEmptyLatitudeOrLongitudeTest() {
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText("You can't enter an empty latitude")));
        addPoiLongitude.check(matches(hasErrorText("You can't enter an empty longitude")));
        addPoiLatitude.perform(typeText("15"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("You can't enter an empty longitude")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(typeText("30"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText("You can't enter an empty latitude")));
    }

    @Test
    public void failsWithOutOfBoundsCoordinatesTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("poiTest2"));
        closeSoftKeyboard();
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("220"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("The longitude must be in range [-180;180]")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("100"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("60"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText("The latitude must be in range [-90;90]")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("-120"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("60"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText("The latitude must be in range [-90;90]")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("30"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("-200"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("The longitude must be in range [-180;180]")));
    }

    @Test
    public void failsOnWrongCoordinateInputTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("poiTest3"));
        closeSoftKeyboard();
        addPoiLatitude.perform(typeText("32#2"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("1:2"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("You need to enter a number")));
        addPoiLatitude.check(matches(hasErrorText("You need to enter a number")));
        addPoiLatitude.perform(typeText("45.3"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("56!2"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("You need to enter a number")));
        addPoiLatitude.check(matches(hasErrorText("You need to enter a number")));
    }

    @Test
    public void succeedsOnCorrectInputsTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("poiTest4"));
        closeSoftKeyboard();
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The Point of Interest poiTest4 was added !");
    }

    @Test
    public void failsOnAddingTwiceTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("poiTest5"));
        closeSoftKeyboard();
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The Point of Interest poiTest5 was added !");
        addPoiScroll.perform(swipeDownCenterBottomFast());
        addPoiName.perform(typeText("poiTest5"));
        closeSoftKeyboard();
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The Point of Interest poiTest5 already exists !");
    }

    @Test
    public void addingFictionFailsOnWrongInputTest() {
        closeSoftKeyboard();
        addPoiFiction.perform(typeText("["));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiFiction.perform(clearText());
        addPoiFiction.perform(typeText("hello$test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiFiction.perform(clearText());
        addPoiFiction.perform(typeText("hello[test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiFiction.perform(clearText());
        addPoiFiction.perform(typeText("hello]test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiFiction.perform(clearText());
        addPoiFiction.perform(typeText("hello.test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiFiction.perform(clearText());
        addPoiFiction.perform(typeText("hello/test"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void addingFictionWorksTest() {
        closeSoftKeyboard();
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

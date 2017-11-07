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

import java.util.ArrayList;

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

@SuppressWarnings("DefaultFileTemplate")
public class AddPOIActivityTest {
    @Rule
    public final ActivityTestRule<AddPOIActivity> mActivityRule =
            new ActivityTestRule<>(AddPOIActivity.class);

    private final static DatabaseProvider.AddPoiListener emptyAddPoiListener = new DatabaseProvider.AddPoiListener() {
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
        database.addPoi(new PointOfInterest("p1", new Position(0, 1), new ArrayList<String>(), "", 0, "", ""), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p2", new Position(1, 2), new ArrayList<String>(), "", 0, "", ""), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p3", new Position(2, 3), new ArrayList<String>(), "", 0, "", ""), emptyAddPoiListener);
    }

    private final ViewInteraction addPoiFinish = onView(withId(R.id.add_poi_finish));
    private final ViewInteraction addPoiName = onView(withId(R.id.add_poi_name));
    private final ViewInteraction addPoiLatitude = onView(withId(R.id.add_poi_latitude));
    private final ViewInteraction addPoiLongitude = onView(withId(R.id.add_poi_longitude));
    private final ViewInteraction addPoiFiction = onView(withId(R.id.add_poi_fiction));
    private final ViewInteraction addPoiFictionButton = onView(withId(R.id.add_poi_fiction_button));
    private final ViewInteraction addPoiFictionList = onView(withId(R.id.add_poi_fiction_list));
    private final ViewInteraction addPoiScroll = onView(withId(R.id.add_poi_scroll));
    private final ViewInteraction addWikiButton = onView(withId(R.id.position_wiki));
    private final ViewInteraction wikiURL = onView(withId(R.id.wikipedia_url));
    private final ViewInteraction wikiGetButton = onView(withId(R.id.get_coordinates));

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
    public void failsWithWrongInputNameTest() {
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("You can't enter an empty point of interest name")));
        addPoiName.perform(clearText());

        addPoiName.perform(typeText("."));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiName.perform(clearText());

        addPoiName.perform(typeText("$"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiName.perform(clearText());

        addPoiName.perform(typeText("#"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiName.perform(clearText());

        addPoiName.perform(typeText("["));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiName.perform(clearText());

        addPoiName.perform(typeText("]"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
        addPoiName.perform(clearText());

        addPoiName.perform(typeText("/"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Those characters are not accepted: . $ # [ ] /")));
    }

    @Test
    public void failsWithEmptyLatitudeOrLongitudeTest() {
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
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("45.3"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("56!2"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("You need to enter a number")));
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

    /* Wikipedia tests */

    @Test
    public void getCoordFromWikipediaAkihabara() {
        closeSoftKeyboard();
        addWikiButton.perform(click());
        wikiURL.perform(typeText("wikipedia.org/wiki/Akihabara"));
        wikiGetButton.perform(click());
        waitASecond();
        addPoiLatitude.check(matches(withText("35.69836")));
        addPoiLongitude.check(matches(withText("139.77313")));
    }

    @Test
    public void notWikipediaURL() {
        closeSoftKeyboard();
        addWikiButton.perform(click());
        wikiURL.perform(typeText("notwiki.net"));
        wikiGetButton.perform(click());
        waitASecond();
        wikiURL.check(matches(hasErrorText("Link must be from wikipedia.org")));
    }

    @Test
    public void wrongFormatURL() {
        closeSoftKeyboard();
        addWikiButton.perform(click());
        wikiURL.perform(typeText("wikipedia.org"));
        wikiGetButton.perform(click());
        waitASecond();
        wikiURL.check(matches(hasErrorText("Wrong link format : must follow wikipedia.org/wiki/Article")));
    }

    @Test
    public void invalidArticle() {
        closeSoftKeyboard();
        addWikiButton.perform(click());
        wikiURL.perform(typeText("wikipedia.org/wiki/notavalidarticle"));
        wikiGetButton.perform(click());
        waitASecond();
        doesToastMatch("No article found");
    }

    @Test
    public void noCoordArticle() {
        closeSoftKeyboard();
        addWikiButton.perform(click());
        wikiURL.perform(typeText("wikipedia.org/wiki/Main_Page"));
        wikiGetButton.perform(click());
        waitASecond();
        doesToastMatch("No coordinates found in article");
    }
}

package ch.epfl.sweng.fiktion;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.InputDevice;
import android.view.View;
import android.widget.EditText;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.GoogleMapsLocationProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.AddPOIActivity;
import ch.epfl.sweng.fiktion.views.GetLocationFromMapActivity;
import ch.epfl.sweng.fiktion.views.POIPageActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
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
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


/**
 * Created by pedro on 20/10/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class AddPOIActivityTest {

    private DatabaseProvider database = DatabaseProvider.getInstance();

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
        Config.TEST_MODE = true;
    }
    @Before
    public void setUp(){
        database.addPoi(new PointOfInterest("p1", new Position(0, 1), new TreeSet<String>(), "", 0, "", ""), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p2", new Position(1, 2), new TreeSet<String>(), "", 0, "", ""), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p3", new Position(2, 3), new TreeSet<String>(), "", 0, "", ""), emptyAddPoiListener);
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
    private final ViewInteraction addPoiCity = onView(withId(R.id.add_poi_city));
    private final ViewInteraction addPoiCountry = onView(withId(R.id.add_poi_country));

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

    @Test
    public void failsWithWrongInputNameTest() {
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiName.check(matches(hasErrorText("Name cannot be empty")));
        addPoiName.perform(clearText());
    }

    @Test
    public void failsWithWrongCityTest() {
        ViewInteraction field = onView(withId(R.id.add_poi_city));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        field.check(matches(hasErrorText("City cannot be empty")));
        field.perform(clearText());
    }

    @Test
    public void failsWithWrongCountryTest() {
        ViewInteraction field = onView(withId(R.id.add_poi_country));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        field.check(matches(hasErrorText("Country cannot be empty")));
        field.perform(clearText());
    }

    @Test
    public void failsWithEmptyLatitudeOrLongitudeTest() {
        String emptyLat = "Latitude cannot be empty";
        String emptyLon = "Longitude cannot be empty";
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText(emptyLat)));
        addPoiLongitude.check(matches(hasErrorText(emptyLon)));
        addPoiLatitude.perform(typeText("15"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText(emptyLon)));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(typeText("30"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText(emptyLat)));
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
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("The longitude must be in range [-180;180]")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("100"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("60"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText("The latitude must be in range [-90;90]")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("-120"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("60"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLatitude.check(matches(hasErrorText("The latitude must be in range [-90;90]")));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("30"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("-200"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText("The longitude must be in range [-180;180]")));
    }

    @Test
    public void failsOnWrongCoordinateInputTest() {
        String numFormat = "Please provide a valid number";
        closeSoftKeyboard();
        addPoiName.perform(typeText("poiTest3"));
        closeSoftKeyboard();
        addPoiLatitude.perform(typeText("32#2"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("1:2"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText(numFormat)));
        addPoiLatitude.check(matches(hasErrorText(numFormat)));
        addPoiLatitude.perform(clearText());
        addPoiLongitude.perform(clearText());
        addPoiLatitude.perform(typeText("45.3"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("56!2"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        addPoiLongitude.check(matches(hasErrorText(numFormat)));
    }

    @Test
    public void succeedsOnCorrectInputsTest() {
        closeSoftKeyboard();
        addPoiName.perform(typeText("poiTest4"));
        closeSoftKeyboard();
        addPoiFiction.perform(typeText("fiction"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiCity.perform(typeText("city"));
        closeSoftKeyboard();
        addPoiCountry.perform(typeText("country"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        onView(withId(R.id.menu_scroll)).perform(swipeUpCenterTopFast());
        onView(withId(R.id.title)).check(matches(withText("poiTest4")));
    }

    @Test
    public void failsOnAddingTwiceTest() {
        database.addPoi(
                new PointOfInterest("poiTest5", new Position(0, 0), new TreeSet<String>(), "", 0, "", ""),
                new DatabaseProvider.AddPoiListener() {
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
        addPoiName.perform(typeText("poiTest5"));
        closeSoftKeyboard();
        addPoiFiction.perform(typeText("fiction"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiCity.perform(typeText("city"));
        closeSoftKeyboard();
        addPoiCountry.perform(typeText("country"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        waitASecond();
        doesToastMatch("The place named poiTest5 already exists !");
    }

    @Test
    public void addingFictionFailsOnWrongInputTest() {
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiFiction.check(matches(hasErrorText("Fiction name cannot be empty")));
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

    private ViewAssertion isBetween(final double from, final double to) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                String sVal = ((EditText) view).getText().toString();
                double val = Double.parseDouble(sVal);
                assertTrue(from <= val && val <= to);
            }
        };
    }

    // https://stackoverflow.com/questions/38737127/espresso-how-to-get-current-activity-to-test-fragments
    private Activity getActivityInstance() {
        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                currentActivity[0] = it.next();
            }
        });

        return currentActivity[0];
    }

    public ViewAction clickQuarter() {
        return new GeneralClickAction(Tap.SINGLE, new CoordinatesProvider() {
            @Override
            public float[] calculateCoordinates(View view) {
                float[] newPos = {view.getWidth() / 2, view.getHeight() / 4};
                return newPos;
            }
        }, Press.FINGER, InputDevice.SOURCE_ANY, 0);
    }

    @Test
    public void getLocationFromMapNewCoordsTest() {
        onView(withId(R.id.add_poi_map)).perform(click());

        GoogleMapsLocationProvider gmaps = ((GetLocationFromMapActivity) getActivityInstance()).gmaps;
        // busy wait until GPS is ready
        long t = System.currentTimeMillis();
        long end = t + 5000;
        while (System.currentTimeMillis() < end && !gmaps.hasLocation()) ;

        if (gmaps.hasLocation()) {
            onView(withId(R.id.mapForLocation)).perform(clickQuarter());
            waitASecond();
            onView(withId(R.id.getNewLocationButton)).perform(click());
            addPoiLatitude.check(isBetween(-90, 90));
            addPoiLongitude.check(isBetween(-180, 180));
        }
    }

    @Test
    public void getLocationFromMapSelfCoordsTest() {
        onView(withId(R.id.add_poi_map)).perform(click());

        GoogleMapsLocationProvider gmaps = ((GetLocationFromMapActivity) getActivityInstance()).gmaps;
        // busy wait until GPS is ready
        long t = System.currentTimeMillis();
        long end = t + 5000;
        while (System.currentTimeMillis() < end && !gmaps.hasLocation()) ;
        if (gmaps.hasLocation()) {
            onView(withId(R.id.selfLocationButton)).perform(click());
            addPoiLatitude.check(isBetween(-90, 90));
            addPoiLongitude.check(isBetween(-180, 180));
        }
    }

    // edit a POI

    @Test
    public void testModifyExistingPoi() {
        // start activity of existing POI
        Intent i = new Intent(mActivityRule.getActivity(), POIPageActivity.class);
        i.putExtra("POI_NAME", "p1");
        mActivityRule.getActivity().startActivity(i);
        // click edit button
        onView(withId(R.id.moreMenu)).perform(click());
        onView(withText("Edit")).perform(click());
        onView(withId(R.id.add_poi_scroll)).check(matches(isDisplayed()));

        closeSoftKeyboard();
        addPoiFiction.perform(typeText("fiction"));
        closeSoftKeyboard();
        addPoiFictionButton.perform(click());
        addPoiLatitude.perform(typeText("45"));
        closeSoftKeyboard();
        addPoiLongitude.perform(typeText("90"));
        closeSoftKeyboard();
        addPoiCity.perform(typeText("city"));
        closeSoftKeyboard();
        addPoiCountry.perform(typeText("country"));
        closeSoftKeyboard();
        addPoiScroll.perform(swipeUpCenterTopFast());
        closeSoftKeyboard();
        addPoiFinish.perform(click());
        onView(withId(R.id.menu_scroll)).perform(swipeUpCenterTopFast());
        onView(withId(R.id.title)).check(matches(withText("p1")));
        onView(withId(R.id.featured)).check(matches(withText("Featured in fiction")));
        onView(withId(R.id.cityCountry)).check(matches(withText("city, country")));
    }
}

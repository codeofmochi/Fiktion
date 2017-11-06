package ch.epfl.sweng.fiktion;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.views.FindNearestPoisActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.fiktion.providers.Providers.database;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * Created by pedro on 20/10/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class FindAndDisplayNearestPoisActivityTest {
    @Rule
    public final ActivityTestRule<FindNearestPoisActivity> mActivityRule =
            new ActivityTestRule<>(FindNearestPoisActivity.class);

    private static final DatabaseProvider.AddPoiListener emptyAddPoiListener = new DatabaseProvider.AddPoiListener() {
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
        database.addPoi(new PointOfInterest("p1", new Position(0.05, 0.05)), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p2", new Position(0.3, 0.3)), emptyAddPoiListener);
        database.addPoi(new PointOfInterest("p3", new Position(0.6, 0.6)), emptyAddPoiListener);
    }

    private ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    private ViewAssertion countMatches(final int count) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (view instanceof ListView)
                    assertThat(((ListView) view).getChildCount(), is(count));
                else
                    throw noViewFoundException;
            }
        };
    }

    @Test
    public void setTo10Shows1Test() {
        onView(withId(R.id.searchRadius)).perform(setProgress(10));
        onView(withId(R.id._findNearPois)).perform(click());
        onView(withId(R.id.displayResultPois)).check(countMatches(1));
    }

    @Test
    public void setTo10Shows2Test() {
        onView(withId(R.id.searchRadius)).perform(setProgress(50));
        onView(withId(R.id._findNearPois)).perform(click());
        onView(withId(R.id.displayResultPois)).check(countMatches(2));
    }

    @Test
    public void setTo10Shows3Test() {
        onView(withId(R.id.searchRadius)).perform(setProgress(100));
        onView(withId(R.id._findNearPois)).perform(click());
        onView(withId(R.id.displayResultPois)).check(countMatches(3));
    }
}

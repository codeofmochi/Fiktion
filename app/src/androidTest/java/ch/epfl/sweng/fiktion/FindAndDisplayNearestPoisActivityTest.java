package ch.epfl.sweng.fiktion;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.doNothing;

/**
 * Created by pedro on 16/10/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FindAndDisplayNearestPoisActivityTest {
    private final PointOfInterest poi1 = new PointOfInterest("poi1", new Position(0.2, 0.2));
    private final PointOfInterest poi2 = new PointOfInterest("poi2", new Position(0.3, 0.4));
    private final PointOfInterest poi3 = new PointOfInterest("poi3", new Position(1.0, 1.0));

    @Rule
    public final ActivityTestRule<FindNearestPoisActivity> mActivityRule =
            new ActivityTestRule<>(FindNearestPoisActivity.class);

    @Mock
    TextView textView;

    @Before
    public void setDatabase() {
        DatabaseActivity.database = new LocalDatabaseProvider();
        PointOfInterest poi1 = new PointOfInterest("poi1", new Position(0.2, 0.2));
        PointOfInterest poi2 = new PointOfInterest("poi2", new Position(0.3, 0.4));
        PointOfInterest poi3 = new PointOfInterest("poi3", new Position(1.0, 1.0));
        doNothing().when(textView).setText("poi1 added");
        DatabaseActivity.database.addPoi(poi1, textView);
        DatabaseActivity.database.addPoi(poi2, textView);
        DatabaseActivity.database.addPoi(poi3, textView);
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

    private ViewAssertion listViewAssertion(Matcher match) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {

            }
        };
    }

    @Test
    public void zeroRangeTest() {
        onView(withId(R.id.searchRadius)).perform(setProgress(0));
        onView(withId(R.id.findNearPois)).perform(click());
    }

}

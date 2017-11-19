package ch.epfl.sweng.fiktion;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.test.rule.ActivityTestRule;
import android.text.Spannable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.views.TextSearchActivity;
import ch.epfl.sweng.fiktion.views.utils.POIDisplayer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for TextSearchActivity
 * Created by dialexo on 19.11.17.
 */

public class TextSearchActivityTest {

    @Rule
    public ActivityTestRule<TextSearchActivity> testRule =
            new ActivityTestRule<>(TextSearchActivity.class);

    @Test
    public void displayTextSearchActivity() {
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
    }

    @Test
    public void emptySearchText() {
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.searchText)).check(matches(hasErrorText("Search field is empty")));
    }

    @Test
    public void searchKeepsText() {
        onView(withId(R.id.searchText)).perform(typeText("poi"));
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.searchText)).check(matches(withText("poi")));
    }

    // tests for POIDisplayer

    @Test
    public void createFictionsString() {
        Set<String> f = new TreeSet<>();
        f.add("f1");
        f.add("f2");
        f.add("f3");
        Spannable s = POIDisplayer.makeFictionsString(f, 3, testRule.getActivity());
        String t = s.toString();
        assertThat(t, is("Featured in f1, f2, f3"));
        s = POIDisplayer.makeFictionsString(f, 2, testRule.getActivity());
        t = s.toString();
        assertThat(t, is("Featured in f1, f2"));
    }

    @Test
    public void testScaleBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
        Bitmap b = POIDisplayer.scaleBitmap(bitmap, 100);
        assertThat(b.getWidth(), is(200));
        assertThat(b.getHeight(), is(100));
    }

    @Test
    public void testCropToSquareBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
        Bitmap b = POIDisplayer.cropBitmapToSquare(bitmap);
        assertThat(b.getWidth(), is(200));
        assertThat(b.getHeight(), is(200));
    }

    @Test
    public void testCreatePoiCard() {
        Set<String> f = new TreeSet<>();
        Activity ctx = testRule.getActivity();
        f.add("fiction");
        PointOfInterest p = new PointOfInterest(
                "name",
                new Position(0, 0),
                f,
                "description",
                0,
                "country",
                "city"
        );
        final View v = POIDisplayer.createPoiCard(p, ctx);
        final LinearLayout results = (LinearLayout) ctx.findViewById(R.id.resultsList);
        // we have to add the view via the creators thread
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    results.addView(v);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        LinearLayout child = (LinearLayout) results.getChildAt(0);
        LinearLayout texts = (LinearLayout) child.getChildAt(1);
        TextView title = (TextView) texts.getChildAt(0);
        TextView cityCountry = (TextView) texts.getChildAt(1);
        TextView featured = (TextView) texts.getChildAt(2);
        TextView upvotes = (TextView) texts.getChildAt(3);
        assertThat(title.getText().toString(), is("name"));
        assertThat(cityCountry.getText().toString(), is("city, country"));
        assertThat(featured.getText().toString(), is("Featured in fiction"));
        assertThat(upvotes.getText().toString(), is("0 upvotes"));
    }
}

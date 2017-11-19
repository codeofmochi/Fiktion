package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;
import android.text.Spannable;

import org.junit.Rule;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

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
}

package ch.epfl.sweng.fiktion;

import android.support.annotation.NonNull;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

/**
 * Created by rodri on 12.10.2017.
 */

public class UserDetailsTest {

    private final static String TAG = "UserDetailsTest";
    private static FirebaseAuth userDetAuth;;
    private static FirebaseUser currUser;
    private static FirebaseAuth.AuthStateListener userDetAtuhListener;
    @Rule
    public ActivityTestRule<UserDetailsActivity> userDetActivityRule =
            new ActivityTestRule<>(UserDetailsActivity.class);

    @After
    public void returnToSignIn(){
        onView(withId(R.id.detail_signout)).perform(click());
    }
    @Before
    public void setUp(){
        defaultSignIn();
    }
    private void defaultSignIn(){
        onView(withId(R.id.User_Email)).perform(typeText("test@test.ch"));
        onView(withId(R.id.User_Password)).perform(typeText("testing"));
        int count = 1000000000;
        for(int i=0;i<=count;i++){}
        onView(withId(R.id.SignInButton)).perform(click());
        int counter = 1000000000;
        for(int i=0;i<=counter;i++){}
    }

    @Test
    public void correctSignOut() {
        onView(withId(R.id.detail_signout)).perform(click());
        onView(withId(R.id.User_Email)).check(matches(isDisplayed()));
        defaultSignIn();
    }

    @Test
    public void changeUserNameCorrect() {
        defaultSignIn();
        onView(withId(R.id.detail_new_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.detail_nickname_button)).perform(click());
        onView(withId(R.id.detail_new_name)).check(matches(isDisplayed()));

        //return to sign in state
        onView(withId(R.id.detail_signout)).perform(click());
    }


}

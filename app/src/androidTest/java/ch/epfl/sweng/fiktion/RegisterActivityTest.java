package ch.epfl.sweng.fiktion;

/**
 * Created by rodri on 11.10.2017.
 */


import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.security.Provider;

import ch.epfl.sweng.fiktion.Providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.RegisterActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegisterActivityTest {

    private final String TAG = "RegActivTest";
    private final String new_email = "new@email.com";
    private final String new_password = "123456";
    private final String exist_email = "test@test.ch";
    private final String exist_password = "testing";

    private RegisterActivity regActivity;

    @Rule
    public ActivityTestRule<RegisterActivity> regActivityRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Before
    public void setUp() {
        //define authenticator as our local and not the firebase one
        Providers.auth = new LocalAuthProvider();
        //define context
        regActivity = regActivityRule.getActivity();
    }

    @After
    public void end() {
        //we need to sign out everytime in case it fails
        Providers.auth.signOut();
        regActivity.finish();
    }

    //TODO: Implement tests that mcck data access to firebase
    /*
    @Test
    public void newAccountTest() throws InterruptedException {
        regAuth.signOut();
        //we type valid credentials and click on the register button
        onView(withId(R.id.register_email)).perform(typeText(new_email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText(new_password), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_click)).perform(click());

        Thread.sleep(2000);

        FirebaseUser user;
        user = regAuth.getCurrentUser();
        if (user != null) {
            user.delete();
        }
    }

    @Test
    public void existingAccountTest() {
        //we type valid but existing credentials and click on the register button
        onView(withId(R.id.register_email)).perform(typeText(exist_email), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText(exist_password), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account)
        onView(withId(R.id.register_title));
    }
*/
    @Test
    public void emptyCredentialsTest() {
        //we only click and expect that we stay in the same activity and errors appear
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account)
        onView(withId(R.id.register_title));
        onView(withId(R.id.register_email)).check(matches(hasErrorText(regActivity.getString(R.string.invalid_email_error))));
    }

    @Test
    public void passwordConfirmationFailed() {
        //we only click and expect that we stay in the same activity and errors appear
        onView(withId(R.id.register_email)).perform(typeText(new_email), closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText(new_password), closeSoftKeyboard());
        onView(withId(R.id.register_confirm_password)).perform(typeText("different"), closeSoftKeyboard());
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account)
        //check that there is an error in register_confirmation_password box
        onView(withId(R.id.register_confirm_password)).check(matches(hasErrorText("Both fields must be equal")));
    }

    @Test
    public void invalidEmailTest() {
        //we type invalid and click on the register button
        onView(withId(R.id.register_email)).perform(typeText("invalidEmail"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText("validpassword"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account) and email error displays
        onView(withId(R.id.register_title));
        onView(withId(R.id.register_email)).check(matches(hasErrorText("Requires a valid email")));
    }

    @Test
    public void invalidPasswordTest() {
        //we type invalid password and click on the register button

        onView(withId(R.id.register_email)).perform(typeText("v@e"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_password)).perform(typeText("12345"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.register_click)).perform(click());

        onView(withId(R.id.register_password)).check(matches(hasErrorText(regActivity.getString(R.string.invalid_password_error))));

        //check that we stay in the same activity (we do not sign in to the new account) and password error displays
        onView(withId(R.id.register_title));
    }
}

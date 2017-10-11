package ch.epfl.sweng.fiktion;

/**
 * Created by rodri on 11.10.2017.
 */


import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class RegisterActivityTest {

    private final String TAG = "RegActivTest";
    private final String new_email = "new@email.com";
    private final String new_password = "newuser";
    private final String exist_email = "test@test.ch";
    private final String exist_password = "testing";

    private FirebaseAuth regAuth;
    private RegisterActivity regActivity;

    @Rule
    public ActivityTestRule<RegisterActivity> regActivityRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Before
    public void setUp() {
        regAuth = FirebaseAuth.getInstance();
        regActivity = regActivityRule.getActivity();
    }

    @Test
    public void newAccountTest() throws InterruptedException {
        regAuth.signOut();

        //we type valid credentials and click on the register button
        onView(withId(R.id.register_email)).perform(typeText(new_email));
        onView(withId(R.id.register_password)).perform(typeText(new_password));
        onView(withId(R.id.register_click)).perform(click());


        Thread.sleep(2000);
        //After creating the account the activity finishes and starts SignInActivity with a logged user, therefore
        //we should see the user details activity and the new email on the field "Email"
        onView(withId(R.id.detail_user_email)).check(matches(withText(new_email)));

        FirebaseUser user;
        user = regAuth.getCurrentUser();
        if (user != null) {
            user.delete();
        } else {
            Log.d(TAG, "Creation failed");
        }
    }

    @Test
    public void existingAccountTest() {
        //we type valid but existing credentials and click on the register button
        onView(withId(R.id.register_email)).perform(typeText(exist_email));
        onView(withId(R.id.register_password)).perform(typeText(exist_password));
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account)
        onView(withId(R.id.register_title));
    }

    @Test
    public void emptyCredentialsTest() {
        //we only click and expect that we stay in the same activity and errors appear
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account)
        onView(withId(R.id.register_title));
        onView(withId(R.id.register_email)).check(matches(hasErrorText(regActivity.getString(R.string.invalid_email_error))));
        onView(withId(R.id.register_password)).check(matches(hasErrorText(regActivity.getString(R.string.required_password_error))));
    }

    @Test
    public void invalidEmailTest() {
        //we type valid but existing credentials and click on the register button
        onView(withId(R.id.register_email)).perform(typeText("invalidEmail"));
        onView(withId(R.id.register_password)).perform(typeText("validpassword"));
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account) and email error displays
        onView(withId(R.id.register_title));
        onView(withId(R.id.register_email)).check(matches(hasErrorText(regActivity.getString(R.string.invalid_email_error))));

    }

    @Test
    public void invalidPasswordTest() {
        //we type valid but existing credentials and click on the register button
        onView(withId(R.id.register_email)).perform(typeText("valid@Email"));
        onView(withId(R.id.register_password)).perform(typeText("not"));
        onView(withId(R.id.register_click)).perform(click());

        //check that we stay in the same activity (we do not sign in to the new account) and password error displays
        onView(withId(R.id.register_title));
        onView(withId(R.id.register_password)).check(matches(hasErrorText(regActivity.getString(R.string.invalid_password_error))));
    }
}

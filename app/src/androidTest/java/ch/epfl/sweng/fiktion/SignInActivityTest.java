package ch.epfl.sweng.fiktion;

/**
 * Created by rodri on 10.10.2017.
 */


import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.fiktion.SignInActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    private final String valid_email = "test@test.ch";
    private final String valid_password = "testing";
    private final String invalid_email = "invalid";
    private final String invalid_password = "1234";

    private FirebaseAuth mAuth;
    private SignInActivity mActivity;


    @Rule
    public ActivityTestRule<SignInActivity> sinActivityRule =
            new ActivityTestRule<SignInActivity>(SignInActivity.class);

    @Before
    public void setUp() {
        mActivity = sinActivityRule.getActivity();
        mAuth = FirebaseAuth.getInstance();

    }

    @Test
    public void valid_login() {
        mAuth.signOut();

        if (mAuth.getCurrentUser() == null) {
            //type valid credentials and click sign in
            onView(withId(R.id.User_Email)).perform(typeText(valid_email), closeSoftKeyboard());
            onView(withId(R.id.User_Password)).perform(typeText(valid_password), closeSoftKeyboard());
            onView(withId(R.id.SignInButton)).perform(click());

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //this view is in other activity but no need to tell Espresso
            //check that user id is correctly update in user details activity
            onView(withId(R.id.detail_user_email)).check(matches(withText(valid_email)));
            onView(withId(R.id.detail_signout)).perform(click());
        }

    }


    @Test
    public void invalid_login() {
        mAuth.signOut();
        //type invalid credentials and click sign in
        onView(withId(R.id.User_Email)).perform(typeText(invalid_email), closeSoftKeyboard());
        onView(withId(R.id.User_Password)).perform(typeText(invalid_password), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.User_Email)).check(matches(hasErrorText(mActivity.getString(R.string.invalid_email_error))));
        onView(withId(R.id.User_Password)).check(matches(hasErrorText(mActivity.getString(R.string.invalid_password_error))));
    }

    @Test
    public void emptyPassword_login() {
        mAuth.signOut();
        //type invalid credentials and click sign in
        onView(withId(R.id.User_Email)).perform(typeText(invalid_email), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

        //wait
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.User_Email)).check(matches(hasErrorText(mActivity.getString(R.string.invalid_email_error))));
        onView(withId(R.id.User_Password)).check(matches(hasErrorText(mActivity.getString(R.string.required_password_error))));
    }

    @Test
    public void startRegistration(){
        //click on sign up button
        onView(withId(R.id.RegisterButton)).perform(click());
        //check if we can see Registration Activity's title
        onView(withId(R.id.register_title));
    }
}
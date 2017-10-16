package ch.epfl.sweng.fiktion;

/**
 * Created by rodri on 10.10.2017.
 */


import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignInActivityTest {

    private final String valid_email = "test@test.ch";
    private final String valid_password = "testing";
    private final String wrong_password = "validbutwrong";
    private final String invalid_email = "invalid";
    private final String invalid_password = "1234";

    private SignInActivity mActivity;


    @Rule
    public ActivityTestRule<SignInActivity> sinActivityRule =
            new ActivityTestRule<SignInActivity>(SignInActivity.class);

    @Before
    public void before() {
        mActivity = sinActivityRule.getActivity();
    }

//TODO: Implement tests that mock data access to firebase
/*
    //valid login test needs to wait for response of the firebase, ask assistants
    @Test
    public void valid_login() {

        onView(withId(R.id.User_Email)).perform(typeText(valid_email), closeSoftKeyboard());
        onView(withId(R.id.User_Password)).perform(typeText(valid_password), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

        //this view is in other activity but no need to tell Espresso
        //check that user id is correctly update in user details activity
        onView(withId(R.id.detail_user_email));

        //we need to check out to reset the app state
        onView(withId(R.id.detail_signout)).perform(click());

    }
*/

    @Test
    public void invalid_login() {
        //type invalid credentials and click sign in

        onView(withId(R.id.User_Email)).perform(typeText(invalid_email), closeSoftKeyboard());
        onView(withId(R.id.User_Password)).perform(typeText(invalid_password), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

        onView(withId(R.id.User_Email)).check(matches(hasErrorText(mActivity.getString(R.string.invalid_email_error))));
        onView(withId(R.id.User_Password)).check(matches(hasErrorText(mActivity.getString(R.string.invalid_password_error))));
    }

    @Test
    public void emptyPassword_login() {
        mActivity = sinActivityRule.getActivity();

        //type invalid credentials and click sign in
        onView(withId(R.id.User_Email)).perform(typeText(invalid_email), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

        onView(withId(R.id.User_Email)).check(matches(hasErrorText(mActivity.getString(R.string.invalid_email_error))));
        onView(withId(R.id.User_Password)).check(matches(hasErrorText(mActivity.getString(R.string.required_password_error))));
    }

    @Test
    public void startRegistration() {
        //click on sign up button
        onView(withId(R.id.RegisterButton)).perform(click());
        //check if we can see Registration Activity's title
        onView(withId(R.id.register_title)).check(matches(isDisplayed()));
    }

    @Test
    public void valid_wrong_login() {
        //type valid credentials and click sign in
        onView(withId(R.id.User_Email)).perform(typeText(valid_email), closeSoftKeyboard());
        onView(withId(R.id.User_Password)).perform(typeText(wrong_password), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());

        //check login failed and we have not advanced to other activities after 2 seconds
        onView(withId(R.id.User_Email));
    }
}
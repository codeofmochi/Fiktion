package ch.epfl.sweng.fiktion;

/**
 * Created by rodri on 10.10.2017.
 */


import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SignInActivityTest {

    private final String valid_email = "test@test.ch";
    private final String valid_password = "testing";
    private FirebaseAuth mAuth;



    @Rule
    public ActivityTestRule<SignInActivity> sinActivityRule =
            new ActivityTestRule<SignInActivity>(SignInActivity.class);

    @Test
    public void valid_signIn(){
        //only try to sign in if the user is signed out
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            //type valid credentials and click sign in
            onView(withId(R.id.User_Email)).perform(typeText(valid_email), closeSoftKeyboard());
            onView(withId(R.id.User_Password)).perform(typeText(valid_password), closeSoftKeyboard());
            onView(withId(R.id.SignInButton)).perform(click());

            Looper.prepare();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    //this view is in other activity but no need to tell Espresso
                    onView(withId(R.id.detail_user_email)).check(matches(withText(valid_email)));
                    //test we can successfully logout right after sign in
                    onView(withId(R.id.detail_signout)).perform(click());
                }
            }, 10000);
        } else{
            mAuth.signOut();
            valid_signIn();
        }
    }





}

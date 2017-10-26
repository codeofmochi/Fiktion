package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.ProfileSettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * This class tests if the profile editing happens correctly
 * Created by Rodrigo on 25.10.2017.
 */

public class ProfileSettingsActivityTest {

    private User user;
    private final User defaultUser = new User("", "default@test.ch", "id", true);

    @Rule
    public final ActivityTestRule<ProfileSettingsActivity> editProfileActivityRule =
            new ActivityTestRule<>(ProfileSettingsActivity.class);

    @BeforeClass
    public static void setAuth() {
        Providers.auth = new LocalAuthProvider();
    }

    @Before
    public void setVariables() {
        user = Providers.auth.getCurrentUser();
    }

    @After
    public void resetAuth() {
        Providers.auth = new LocalAuthProvider();
        //wait until all toasts disappear
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void changeUserInfos_newValues() {
        //TODO check that toasts appear
        //change name
        String newName = "new name";
        onView(withId(R.id.update_new_name)).perform(typeText(newName), closeSoftKeyboard());
        onView(withId(R.id.update_confirm_name)).perform(click());


        assertThat(Providers.auth.getCurrentUser().getName(), is(newName));
        //change email
        String newEmail = "new@email.ch";
        onView(withId(R.id.update_new_email)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.update_confirm_email)).perform(click());

        assertThat(Providers.auth.getCurrentUser().getEmail(), is(newEmail));

    }

    @Test
    public void changeUserInfos_sameValues() {
        //TODO check that toasts appear
        //change name
        String newName = user.getName();
        onView(withId(R.id.update_new_name)).perform(typeText(newName), closeSoftKeyboard());
        onView(withId(R.id.update_confirm_name)).perform(click());

        assertThat(Providers.auth.getCurrentUser().getName(), is(newName));
        //change email
        String newEmail = "new@email.ch";
        onView(withId(R.id.update_new_email)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.update_confirm_email)).perform(click());

        assertThat(Providers.auth.getCurrentUser().getEmail(), is(newEmail));

    }

    @Test
    public void successDeleteAccount() {
        onView(withId(R.id.update_delete_account)).perform(click());
        //check that list of user that by default only has one user is now empty
        Providers.auth.signIn(defaultUser.getEmail(), "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.assertNull(Providers.auth.getCurrentUser());
            }
        });
    }

    @Test
    public void failDeleteAccount(){
        Providers.auth.signOut();
        onView(withId(R.id.update_delete_account)).perform(click());
        //check that list of user that by default only has one user is now empty
        Providers.auth.signIn(defaultUser.getEmail(), "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertThat(Providers.auth.getCurrentUser().getEmail(), is(defaultUser.getEmail()));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void failNoUserSignedInDeleteAccount() {
        Providers.auth.signOut();
        //we try to delete the same account with no user currently connected -> failure, toast should appear
        onView(withId(R.id.update_delete_account)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        onView(withText("No user currently signed in"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void successSendEmailVerification(){
        onView(withId(R.id.update_email_verification)).perform(click());
        //should send an email verification since the user is already connected (default user)

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("User's email is verified"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void noUserSignedInSendEmailVerification(){
        Providers.auth.signOut();
        onView(withId(R.id.update_email_verification)).perform(click());
        //should send an email verification since the user is already connected (default user)

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Sign in state changed, signing out"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void successResetPassword(){

        onView(withId(R.id.update_reset_password)).perform(click());
        //should send an email verification since the user is already connected (default user)

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Password reset email sent"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void failResetPassword(){
        Providers.auth.signOut();
        onView(withId(R.id.update_reset_password)).perform(click());
        //should send an email verification since the user is already connected (default user)

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("No User currently signed in"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow()
                        .getDecorView())))).check(matches(isDisplayed()));
    }


}

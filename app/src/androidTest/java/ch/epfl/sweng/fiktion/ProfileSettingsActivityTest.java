package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.SettingsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * This class tests if the profile editing happens correctly
 * Created by Rodrigo on 25.10.2017.
 */

public class ProfileSettingsActivityTest {

    private User user;
    private final User defaultUser = new User("default", "defaultID");

    @Rule
    public final ActivityTestRule<SettingsActivity> editProfileActivityRule =
            new ActivityTestRule<>(SettingsActivity.class);

    @BeforeClass
    public static void setAuth() {
        Config.TEST_MODE = true;
    }

    @Before
    public void setVariables() {
        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User currUser) {
                user = currUser;
            }

            @Override
            public void onDoesntExist() {
                user = null;
            }

            @Override
            public void onFailure() {
                user = null;
            }
        });
    }

    @After
    public void resetAuth() {
        AuthProvider.destroyInstance();
    }

    @Test
    public void changeUserInfos_newValues() {
        //change name
        final String newName = "new name";
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());
        //change email
        final String newEmail = "new@email.ch";
        onView(withId(R.id.emailEdit)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        assertThat(user.getName(), is("new name"));

        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                assertThat(AuthProvider.getInstance().getEmail(), is(newEmail));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void changeUserEmail_invalid() {
        String newEmail = "invalid";
        onView(withId(R.id.emailEdit)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                //assert that we can only write 15 characters
                assertThat(AuthProvider.getInstance().getEmail(), is("default@email.ch"));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }


            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void changeUserName_invalid() {
        String newName = "";
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());
        newName = "thishasmorethan15characters";
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                //assert that we can only write 15 characters
                assertThat(user.getName(), is("thishasmorethan"));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }


            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void changeUserInfos_sameValues() {
        //change name
        final String newName = user.getName();
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());


        //change email
        final String newEmail = AuthProvider.getInstance().getEmail();
        onView(withId(R.id.emailEdit)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        onView(withId(R.id.usernameEdit)).check(matches(hasErrorText("Please type a new and valid username")));
        onView(withId(R.id.emailEdit)).check(matches(hasErrorText("Please type a new and valid email")));


        AuthProvider.getInstance().getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                assertThat(user.getName(), is(newName));
                assertThat(AuthProvider.getInstance().getEmail(), is(newEmail));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }


            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void successDeleteAccount() {
        onView(withId(R.id.deleteAccountButton)).perform(click());
        //check that list of user that by default only has one user is now empty
        onView(withText("Delete"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.home_main_layout)).check(matches(isDisplayed()));

    }

    @Test
    public void cancelDeleteAccount() {
        onView(withId(R.id.deleteAccountButton)).perform(click());
        //check that list of user that by default only has one user is now empty
        onView(withText("Cancel"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.accountSettingsTitle)).check(matches(isDisplayed()));

    }


    @Test
    public void failNoUserSignedInDeleteAccount() {
        AuthProvider.getInstance().signOut();
        //we try to delete the same account with no user currently connected -> failure, toast should appear
        onView(withId(R.id.deleteAccountButton)).perform(click());
        onView(withText("Delete"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.accountLoginButton)).check(matches(isDisplayed()));

    }

    @Test
    public void NotSignedInSendEmailVerification() {
        AuthProvider.getInstance().signOut();
        editProfileActivityRule.launchActivity(new Intent());
        onView(withId(R.id.accountLoginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void SuccessSendEmailVerification() {
        onView(withId(R.id.verifiedButton)).perform(click());
        onView(withId(R.id.verifiedButton)).check(matches(isDisplayed()));

    }


    @Test
    public void successResetPassword() {
        onView(withId(R.id.passwordReset)).perform(click());
        //should send an email verification since the user is already connected (default user)
    }

    @Test
    public void failResetPassword() {
        AuthProvider.getInstance().signOut();
        onView(withId(R.id.passwordReset)).perform(click());
        //should send an email verification since the user is already connected (default user)
        onView(withId(R.id.accountLoginButton)).check(matches(isDisplayed()));
    }

    @Test
    public void failSaveInfos() {
       AuthProvider.getInstance().signOut();
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());
    }

    @Test
    public void testRedirectLogin() {
        AuthProvider.getInstance().signOut();
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());
        onView(withId(R.id.accountLoginButton)).perform(click());
        onView(withId(R.id.User_Email)).check(matches(isDisplayed()));
    }

    @Test
    public void testActivityForResult() {
        AuthProvider.getInstance().signOut();
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());
        onView(withId(R.id.accountLoginButton)).perform(click());
        onView(withId(R.id.User_Email)).perform(typeText("default@email.ch"), closeSoftKeyboard());
        onView(withId(R.id.User_Password)).perform(typeText("testing"), closeSoftKeyboard());
        onView(withId(R.id.SignInButton)).perform(click());
        onView(withId(R.id.accountSettings)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignOut() {
        onView(withId(R.id.signOutButton)).perform(click());
        onView(withId(R.id.home_main_layout)).check(matches(isDisplayed()));
    }
}

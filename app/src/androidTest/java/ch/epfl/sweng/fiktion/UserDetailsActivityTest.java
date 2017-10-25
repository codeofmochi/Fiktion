package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**This activity will test the UI part of the User Details Activity
 * Created by Rodrigo on 22.10.2017.
 */


public class UserDetailsActivityTest {

    private User user;
    @Rule
    public final ActivityTestRule<UserDetailsActivity> userDetActivityRule =
            new ActivityTestRule<>(UserDetailsActivity.class);

    @BeforeClass
    public static void setAuth(){
        Providers.auth = new LocalAuthProvider();
    }

    @Before
    public void setVariables(){
        user = Providers.auth.getCurrentUser();
    }

    @After
    public void resetAuth(){
        Providers.auth = new LocalAuthProvider();
    }

    @Test
    public void seeDefaultUserInformations(){
        onView(withId(R.id.detail_user_email)).check(matches(withText(user.getEmail())));
        onView(withId(R.id.detail_user_name)).check(matches(withText(user.getName())));
    }
  
    //moving this to ProfileSettingsActivity
  /*
    @Test
    public void changeUserInfos_newValues(){
        //TODO check that toasts appear
        //change name
        String newName = "new name";
        onView(withId(R.id.detail_new_name)).perform(typeText(newName),closeSoftKeyboard());
        onView(withId(R.id.detail_confirm_name)).perform(click());

        onView(withId(R.id.detail_user_name)).check(matches(withText(newName)));

        //change email
        String newEmail = "new@email.ch";
        onView(withId(R.id.detail_new_email)).perform(typeText(newEmail),closeSoftKeyboard());
        onView(withId(R.id.detail_confirm_email)).perform(click());

        onView(withId(R.id.detail_user_email)).check(matches(withText(newEmail)));

    }
    @Test
    public void changeUserInfos_sameValues(){
        //TODO check that toasts appear
        //Try to change name with the same value
        String sameName = user.getName();
        String sameEmail = user.getEmail();

        onView(withId(R.id.detail_new_name)).perform(typeText(sameName),closeSoftKeyboard());
        onView(withId(R.id.detail_confirm_name)).perform(click());

        onView(withId(R.id.detail_user_name)).check(matches(withText(sameName)));

        //change email
        String newEmail = "new@email.ch";
        onView(withId(R.id.detail_new_email)).perform(typeText(sameEmail),closeSoftKeyboard());
        onView(withId(R.id.detail_confirm_email)).perform(click());

        onView(withId(R.id.detail_user_email)).check(matches(withText(sameEmail)));

    }

*/

}

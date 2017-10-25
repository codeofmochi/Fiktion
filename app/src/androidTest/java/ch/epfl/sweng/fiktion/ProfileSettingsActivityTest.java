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
import ch.epfl.sweng.fiktion.views.ProfileSettingsActivity;
import ch.epfl.sweng.fiktion.views.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

/**This class tests if the profile editing happens correctly
 * Created by Rodrigo on 25.10.2017.
 */

public class ProfileSettingsActivityTest {

    private User user;
    @Rule
    public final ActivityTestRule<ProfileSettingsActivity> editProfileActivityRule =
            new ActivityTestRule<>(ProfileSettingsActivity.class);

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
    public void changeUserInfos_newValues(){
        //TODO check that toasts appear
        //change name
        String newName = "new name";
        onView(withId(R.id.update_new_name)).perform(typeText(newName),closeSoftKeyboard());
        onView(withId(R.id.update_confirm_name)).perform(click());


        assertThat(Providers.auth.getCurrentUser().getName(), is(newName));
        //change email
        String newEmail = "new@email.ch";
        onView(withId(R.id.update_new_email)).perform(typeText(newEmail),closeSoftKeyboard());
        onView(withId(R.id.update_confirm_email)).perform(click());

        assertThat(Providers.auth.getCurrentUser().getEmail(), is(newEmail));

    }

    @Test
    public void changeUserInfos_sameValues() {
        //TODO check that toasts appear
        //change name
        String newName = user.getName();
        onView(withId(R.id.update_new_name)).perform(typeText(newName),closeSoftKeyboard());
        onView(withId(R.id.update_confirm_name)).perform(click());

        assertThat(Providers.auth.getCurrentUser().getName(), is(newName));
        //change email
        String newEmail = "new@email.ch";
        onView(withId(R.id.update_new_email)).perform(typeText(newEmail),closeSoftKeyboard());
        onView(withId(R.id.update_confirm_email)).perform(click());

        assertThat(Providers.auth.getCurrentUser().getEmail(), is(newEmail));

    }
}

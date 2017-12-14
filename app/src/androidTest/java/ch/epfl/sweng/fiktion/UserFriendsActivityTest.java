package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.ProfileActivity;
import ch.epfl.sweng.fiktion.views.UserFriendsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;


public class UserFriendsActivityTest {

    private TreeSet<String> friends = new TreeSet<>(Collections.singleton("defaultID"));
    private TreeSet<String> requests = new TreeSet<>(Collections.singleton("id1"));

    private final User userWithRequestAndFriend = new User("popular", "popularID", new TreeSet<String>(),
            new TreeSet<String>(), friends, requests, new LinkedList<String>(),
            true, new TreeSet<String>(), new Settings(30));

    DatabaseProvider.AddUserListener emptyAddUserListener = new DatabaseProvider.AddUserListener() {
        @Override
        public void onAlreadyExists() {

        }

        @Override
        public void onFailure() {

        }

        @Override
        public void onSuccess() {

        }
    };

    @Rule
    public final IntentsTestRule<UserFriendsActivity> testRule =
            new IntentsTestRule<>(UserFriendsActivity.class, true, false);

    @BeforeClass
    public static void setConfig(){
        Config.TEST_MODE = true;
    }

    @Before
    public void reset(){
        AuthProvider.getInstance().signIn("default@email.ch", "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        });
    }

    @After
    public void destroyInstances(){
        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
    }

    @Test
    public void anotherProfile(){
        Intent i = new Intent();
        i.putExtra(ProfileActivity.PROFILE_ACTION_KEY, ProfileActivity.PROFILE_ACTION_ANOTHER);
        testRule.launchActivity(i);
    }

    @Test
    public void stateNull(){
        Intent i = new Intent();
        testRule.launchActivity(i);
    }

    @Test
    public void handleRequests() {
        DatabaseProvider.getInstance().addUser(userWithRequestAndFriend, emptyAddUserListener);
        Intent i = new Intent();
        i.putExtra(ProfileActivity.USER_ID_KEY, "popularID");
        i.putExtra(ProfileActivity.PROFILE_ACTION_KEY, ProfileActivity.PROFILE_ACTION_ME);
        testRule.launchActivity(i);

        //TODO : click on V and X , accept or ignore request

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void handleFriend(){
        DatabaseProvider.getInstance().addUser(userWithRequestAndFriend, emptyAddUserListener);
        Intent i = new Intent();
        i.putExtra(ProfileActivity.USER_ID_KEY, "popularID");
        i.putExtra(ProfileActivity.PROFILE_ACTION_KEY, ProfileActivity.PROFILE_ACTION_ME);
        testRule.launchActivity(i);

        //TODO : click on X : remove friend
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

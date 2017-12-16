package ch.epfl.sweng.fiktion.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;

/**
 * Created by painguin on 16.12.17.
 */

public class LocalDatabaseFiller {

    public static void addBasicUsers() {
        if (Config.TEST_MODE) {
            User defaultUser = new User("default", "defaultID");
            final User user1 = new User("user1", "id1");

            // Initiating friendlists and friendRequests
            String[] frList = new String[]{"defaultID"};
            String[] rList = new String[]{"id1"};
            String[] fakeFList = new String[]{"idfake"};
            String[] fakeRList = new String[]{"idfake"};
            String[] favList = new String[]{"fav POI"};
            String[] whishList = new String[]{"wish POI"};
            String[] visitedList = new String[]{"vis POI"};

            // user has "fav POI" as favourite, "vis POI" in visited and "wish POI" in wishlist
            User userWVFav = new User("userWVFav", "idwvfav", new TreeSet<>(Arrays.asList(favList)), new TreeSet<>(Arrays.asList(whishList)),
                    new TreeSet<String>(), new TreeSet<String>(),
                    new LinkedList<>(Arrays.asList(visitedList)), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

            // user is friend with defaultUser and has user1 in his requests
            User userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
                    new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)),
                    new LinkedList<String>(), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

            // user with a friend that is not stored in the database
            User userFakeF = new User("userFakeF", "idfakef", new TreeSet<String>(), new TreeSet<String>(),
                    new TreeSet<>(Arrays.asList(fakeFList)), new TreeSet<String>(),
                    new LinkedList<String>(), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

            // user has request from fake friend
            User userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
                    new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)),
                    new LinkedList<String>(), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));
            List<User> initialList = Arrays.asList(defaultUser, user1, userFR, userFakeF, userFakeR, userWVFav);

            DatabaseProvider.AddUserListener listener = new DatabaseProvider.AddUserListener() {
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

            for (User u : initialList) {
                DatabaseProvider.getInstance().addUser(u, listener);
            }
        }
    }
}

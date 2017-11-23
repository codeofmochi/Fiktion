package ch.epfl.sweng.fiktion.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;


/**
 * Local database provider
 *
 * @author pedro
 */
public class LocalDatabaseProvider extends DatabaseProvider {
    private final User defaultUser = new User("default", "defaultID", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());
    private final User user1 = new User("user1", "id1");
    // Initiating friendlists and friendRequests
    private final String[] frList = new String[]{"defaultID"};
    private final String[] rList = new String[]{"id1"};
    private final String[] fakeFList = new String[]{"idfake"};
    private final String[] fakeRList = new String[]{"idfake"};
    private final String[] favList = new String[]{"fav POI"};
    private final String[] whishList = new String[]{"wish POI"};
    private final String[] visitedList = new String[]{"vis POI"};

    // user has "fav POI" as favourite, "vis POI" in visited and "wish POI" in wishlist
    private final User userWVFav = new User("userWVFav", "idwvfav", new TreeSet<>(Arrays.asList(favList)), new TreeSet<>(Arrays.asList(whishList)),
            new TreeSet<String>(), new TreeSet<String>(), new LinkedList<>(Arrays.asList(visitedList)), true);

    // user is friend with defaultUser and has user1 in his requests
    private final User userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
            new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)), new LinkedList<String>(), true);

    // user with a friend that is not stored in the database
    private final User userFakeF = new User("userFakeF", "idfakef", new TreeSet<String>(), new TreeSet<String>(),
            new TreeSet<>(Arrays.asList(fakeFList)), new TreeSet<String>(), new LinkedList<String>(), true);

    // user has request from fake friend
    private final User userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
            new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)), new LinkedList<String>(), true);

    private final List<User> initialList = Arrays.asList(defaultUser, user1, userFR, userFakeF, userFakeR, userWVFav);
    private final List<PointOfInterest> poiList = new ArrayList<>();
    private final List<User> users = new ArrayList<>(initialList);

    /**
     * {@inheritDoc}
     */

    public void addPoi(PointOfInterest poi, AddPoiListener listener) {
        switch (poi.name()) {
            case "SUCCESS":
                listener.onSuccess();
                return;
            case "ALREADYEXISTS":
                listener.onAlreadyExists();
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }
        if (poiList.contains(poi)) {
            // inform the listener that the poi already exists
            listener.onAlreadyExists();
        } else {
            // add the poi
            poiList.add(poi);
            // inform the listener that the operation succeeded
            listener.onSuccess();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void getPoi(String name, GetPoiListener listener) {
        switch (name) {
            case "SUCCESS":
                listener.onSuccess(new PointOfInterest("SUCCESS",
                        new Position(0, 0),
                        new TreeSet<String>(),
                        "",
                        0,
                        "",
                        ""));
                return;
            case "MODIFIED":
                listener.onModified(new PointOfInterest("MODIFIED",
                        new Position(0, 0),
                        new TreeSet<String>(),
                        "",
                        0,
                        "",
                        ""));
                return;
            case "DOESNTEXIST":
                listener.onDoesntExist();
                return;

            case "FAILURE":
                listener.onFailure();
                return;
        }
        for (PointOfInterest poi : poiList) {
            if (poi.name().equals(name)) {
                // inform the listener that we have the poi
                listener.onSuccess(poi);
                return;
            }
        }
        // inform the listener that the poi doesnt exist
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyPOI(PointOfInterest poi, ModifyPOIListener listener) {
        switch (poi.name()) {
            case "SUCCESS":
                listener.onSuccess();
                return;
            case "DOESNTEXIST":
                listener.onDoesntExist();
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }
        if (poi.name().equals("FAILURE")) {
            listener.onFailure();
            return;
        }
        for (int i = 0; i < poiList.size(); ++i) {
            if (poi.equals(poiList.get(i))) {
                poiList.set(i, poi);
                listener.onSuccess();
                return;
            }
        }
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    public void findNearPois(Position pos, int radius, FindNearPoisListener listener) {
        if (pos.latitude() == 1000 && pos.longitude() == 1000) {
            listener.onFailure();
            return;
        }
        for (PointOfInterest poi : poiList) {
            if (dist(pos.latitude(), pos.longitude(), poi.position().latitude(), poi.position().longitude()) <= radius) {
                listener.onNewValue(poi);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void searchByText(String text, SearchPOIByTextListener listener) {
        switch (text) {
            case "NEWVALUE":
                listener.onNewValue(new PointOfInterest("NEWVALUE",
                        new Position(0, 0),
                        new TreeSet<String>(),
                        "",
                        0,
                        "",
                        ""));
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }
        for (PointOfInterest poi : poiList) {
            if (poi.name().contains(text) ||
                    poi.description().contains(text) ||
                    poi.city().contains(text) ||
                    poi.country().contains(text)) {
                listener.onNewValue(poi);
            }
        }
    }

    /**
     * Returns the distance between two points with their latitude and longitude coordinates
     *
     * @param lat1  latitude of the first position
     * @param long1 longitude of the first position
     * @param lat2  latitude of the second position
     * @param long2 longitude of the second position
     * @return the distance
     */
    private double dist(double lat1, double long1, double lat2, double long2) {
        double theta = long1 - long2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        return 111.18957696 * Math.toDegrees(Math.acos(dist));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUser(User user, AddUserListener listener) {
        switch (user.getID()) {
            case "SUCCESS":
                listener.onSuccess();
                return;
            case "ALREADYEXISTS":
                listener.onAlreadyExists();
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }

        boolean contains = false;
        String id = user.getID();
        // go through all the users and check if there is one with the same id as the user in parameter
        for (User u : users) {
            contains |= u.getID().equals(id);
        }
        if (contains) {
            listener.onAlreadyExists();
        } else {
            users.add(user);
            listener.onSuccess();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUserById(String id, GetUserListener listener) {
        switch (id) {
            case "SUCCESS":
                listener.onSuccess(new User("SUCCESS", "SUCCESS"));
                return;
            case "DOESNTEXIST":
                listener.onDoesntExist();
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }
        for (User u : users) {
            if (u.getID().equals(id)) {
                listener.onSuccess(u);
                return;
            }
        }
        listener.onDoesntExist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleterUserById(String id, DeleteUserListener listener) {
        switch (id) {
            case "SUCCESS":
                listener.onSuccess();
                return;
            case "DOESNTEXIST":
                listener.onDoesntExist();
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }
        for (User u : users) {
            if (u.getID().equals(id)) {
                users.remove(u);
                listener.onSuccess();
                return;
            }
        }
        listener.onDoesntExist();
    }

    @Override
    public void modifyUser(final User user, final ModifyUserListener listener) {
        switch (user.getID()) {
            case "SUCCESS":
                listener.onSuccess();
                return;
            case "DOESNTEXIST":
                listener.onDoesntExist();
                return;
            case "FAILURE":
                listener.onFailure();
                return;
        }
        deleterUserById(user.getID(), new DeleteUserListener() {
            @Override
            public void onSuccess() {
                users.add(user);
                listener.onSuccess();
            }

            @Override
            public void onDoesntExist() {
                listener.onDoesntExist();
            }

            @Override
            public void onFailure() {
                listener.onFailure();
            }
        });
    }
}
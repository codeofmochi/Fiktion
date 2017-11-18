package ch.epfl.sweng.fiktion.providers;

import java.util.ArrayList;
import java.util.Collections;
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
    private final User defaultUser = new User("default", "defaultID", new TreeSet<String>(), new TreeSet<String>());
    private final List<PointOfInterest> poiList = new ArrayList<>();
    private final List<User> users = new ArrayList<>
            (Collections.singletonList(defaultUser));

    /**
     * {@inheritDoc}
     */
    public void addPoi(PointOfInterest poi, AddPoiListener listener) {
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
    public void findNearPois(Position pos, int radius, FindNearPoisListener listener) {
        for (PointOfInterest poi : poiList) {
            if (dist(pos.latitude(), pos.longitude(), poi.position().latitude(), poi.position().longitude()) <= radius) {
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
        boolean contains = false;
        String id = user.getID();
        // go through all the users and check if there is one with the same id as the user in parameter
        for (User u: users) {
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
        for(User u: users) {
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
        for (User u: users) {
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
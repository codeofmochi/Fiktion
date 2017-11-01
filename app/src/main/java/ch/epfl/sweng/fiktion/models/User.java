package ch.epfl.sweng.fiktion.models;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

/**This class represents the User in the application
 *@author Rodrigo
 */

public class User {
    private String name;
    //we could use same id as firebase id or create our own id system
    private final String id;

    /**
     * Creates a new User with given paramaters
     * @param input_name Username
     * @param input_id User id
     */
    public User(String input_name, String input_id) {
        name = input_name;
        id = input_id;
    }

    /**
     * Changes this user's username
     * @param newName New username value
     * @param listener Handles what happens in case of success or failure of the changement
     */
    public void changeName(final String newName, final AuthProvider.AuthListener listener) {
        if(newName!=null && !newName.equals(name) && newName.length()<=15){
            name = newName;
            listener.onSuccess();
        } else{
            listener.onFailure();
        }
    }



    @Override
    public boolean equals(Object other) {
        if (other == null || !User.class.isAssignableFrom(other.getClass())) {
            return false;
        }

        User otherUser = (User) other;

        return this.name.equals(otherUser.name)
                && this.id.equals(otherUser.id);
    }

    /**
     * @return the user display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return user ID
     */
    public String getID() {
        return id;
    }



}

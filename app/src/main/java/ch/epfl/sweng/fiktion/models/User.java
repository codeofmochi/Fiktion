package ch.epfl.sweng.fiktion.models;

/**
 * Created by rodrigo on 09.10.2017.
 */

public class User {
    private String name;
    private String email;
    //we could use same id as firebase id or create our own id system
    private final String id;
    private boolean emailVerified;

    public User(String input_name, String input_email, String input_id,Boolean input_verified) {
        name = input_name;
        email = input_email;
        id = input_id;
        emailVerified = input_verified;
    }

    public void changeName(String newName){
        name = newName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !User.class.isAssignableFrom(other.getClass())) {
            return false;
        }

        User otherUser = (User) other;

        return this.name.equals(otherUser.name)
                && this.email.equals(otherUser.email)
                && this.id.equals(otherUser.id);
    }

    /**
     * 
     * @return the user display name
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return user email
     */
    public String getEmail(){
        return email;
    }

    /**
     *
     * @return user ID
     */
    public String getID(){
        return id;
    }

    public Boolean isEmailVerified(){
        return emailVerified;
    }


}

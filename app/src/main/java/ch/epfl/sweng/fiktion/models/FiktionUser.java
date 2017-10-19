package ch.epfl.sweng.fiktion.models;

/**
 * Created by rodrigo on 09.10.2017.
 */

public class FiktionUser {
    private String name;
    private String email;
    //we could use same id as firebase id or create our own id system
    private final String id;

    public FiktionUser(String input_name, String input_email, String input_id) {
        name = input_name;
        email = input_email;
        id = input_id;
    }

    public void changeName(String newName){
        name = newName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !FiktionUser.class.isAssignableFrom(other.getClass())) {
            return false;
        }

        FiktionUser otherUser = (FiktionUser) other;

        return this.name.equals(otherUser.name)
                && this.email.equals(otherUser.email)
                && this.id.equals(otherUser.id);
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getID(){
        return id;
    }


}

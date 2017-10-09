package ch.epfl.sweng.fiktion;

/**
 * Created by rodrigo on 09.10.2017.
 */

public class FiktionUser  {
    private String name;
    private String email;
    //we could use same id as firebase id or create our own id system
    private final String id;

    public FiktionUser(String input_name, String input_email,String input_id){
        name = input_name;
        email = input_email;
        id = input_id;
    }

}

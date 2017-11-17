package ch.epfl.sweng.fiktion.providers;

/**
 * Created by Rodrigo on 16.11.2017.
 */

public class AuthSingleton {
    public static AuthProvider auth = new FirebaseAuthProvider();

    private AuthSingleton(){}
}

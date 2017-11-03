package ch.epfl.sweng.fiktion.providers;


/**
 * Class that holds provider instances
 *
 * @author pedro
 */
public class Providers {
    public static DatabaseProvider database = new FirebaseDatabaseProvider();
    public static AuthProvider auth = new FirebaseAuthProvider();
    public static AlgoliaSearchProvider algoliaSearchProvider = new AlgoliaSearchProvider();
}

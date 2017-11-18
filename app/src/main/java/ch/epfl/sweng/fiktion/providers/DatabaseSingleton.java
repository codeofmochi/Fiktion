package ch.epfl.sweng.fiktion.providers;


/**
 * Class that holds provider instances
 *
 * @author pedro
 */
<<<<<<< HEAD:app/src/main/java/ch/epfl/sweng/fiktion/providers/Providers.java
public class Providers {
    public static AuthProvider auth = new FirebaseAuthProvider();
    public static AlgoliaSearchProvider algoliaSearchProvider = new AlgoliaSearchProvider();
=======
public class DatabaseSingleton {
>>>>>>> 49dba4dd3cbb8d6f9072cb44df17557a1a97d15b:app/src/main/java/ch/epfl/sweng/fiktion/providers/DatabaseSingleton.java
    public static DatabaseProvider database = new FirebaseDatabaseProvider();
    private DatabaseSingleton(){}
}

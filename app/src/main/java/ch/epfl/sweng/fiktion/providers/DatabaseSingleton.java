package ch.epfl.sweng.fiktion.providers;


/**
 * Class that holds provider instances
 *
 * @author pedro
 */
public class DatabaseSingleton {
    public static DatabaseProvider database = new FirebaseDatabaseProvider();

    private DatabaseSingleton(){

    }
}

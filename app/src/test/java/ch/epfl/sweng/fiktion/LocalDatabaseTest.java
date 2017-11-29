package ch.epfl.sweng.fiktion;

import org.junit.After;
import org.junit.Test;

import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by pedro on 24/11/17.
 */

public class LocalDatabaseTest {

    private LocalDatabaseProvider db = new LocalDatabaseProvider();

    private PointOfInterest poiWithName(String name) {
        return new PointOfInterest(name, new Position(0, 0),
                new TreeSet<String>(), "", 0, "", "");
    }

    private PointOfInterest poi = poiWithName("poi");
    private PointOfInterest poi2 = poiWithName("poi2");
    private User user = new User("user", "user");
    private User user2 = new User("user2", "user2");

    private DatabaseProvider.AddPoiListener emptyAddPOIListener = new DatabaseProvider.AddPoiListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onAlreadyExists() {
        }

        @Override
        public void onFailure() {
        }
    };
    private DatabaseProvider.AddUserListener emptyAddUserListener = new DatabaseProvider.AddUserListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onAlreadyExists() {
        }

        @Override
        public void onFailure() {
        }
    };

    @After
    public void empty() {
        db = new LocalDatabaseProvider();
    }

    @Test
    public void addPOITest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.AddPoiListener listener = new DatabaseProvider.AddPoiListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onAlreadyExists() {
                result.set("A");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPoi(poi, listener);
        assertThat(result.get(), is("S"));
        db.addPoi(poi, listener);
        assertThat(result.get(), is("A"));
        PointOfInterest s = poiWithName("ADDPOIS");
        PointOfInterest a = poiWithName("ADDPOIA");
        PointOfInterest f = poiWithName("ADDPOIF");
        db.addPoi(s, listener);
        assertThat(result.get(), is("S"));
        db.addPoi(a, listener);
        assertThat(result.get(), is("A"));
        db.addPoi(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void getPOITest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.GetPoiListener listener = new DatabaseProvider.GetPoiListener() {
            @Override
            public void onSuccess(PointOfInterest poi) {
                result.set("S");
            }

            @Override
            public void onModified(PointOfInterest poi) {
                result.set("M");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };
        db.getPoi("poi", listener);
        assertThat(result.get(), is("D"));

        db.addPoi(poi, emptyAddPOIListener);

        db.getPoi("poi", listener);
        assertThat(result.get(), is("S"));
        db.getPoi("poi2", listener);
        assertThat(result.get(), is("D"));
        String s = "GETPOIS";
        String m = "GETPOIM";
        String d = "GETPOID";
        String f = "GETPOIF";
        db.getPoi(s, listener);
        assertThat(result.get(), is("S"));
        db.getPoi(m, listener);
        assertThat(result.get(), is("M"));
        db.getPoi(d, listener);
        assertThat(result.get(), is("D"));
        db.getPoi(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void modifyPOITest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPoi(poi, emptyAddPOIListener);
        db.modifyPOI(poi, listener);
        assertThat(result.get(), is("S"));
        db.modifyPOI(poi2, listener);
        assertThat(result.get(), is("D"));
        PointOfInterest s = poiWithName("MODIFYPOIS");
        PointOfInterest d = poiWithName("MODIFYPOID");
        PointOfInterest f = poiWithName("MODIFYPOIF");
        db.modifyPOI(s, listener);
        assertThat(result.get(), is("S"));
        db.modifyPOI(d, listener);
        assertThat(result.get(), is("D"));
        db.modifyPOI(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void upvoteTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPoi(poi, emptyAddPOIListener);
        db.upvote("poi", listener);
        assertThat(result.get(), is("S"));
        db.upvote("poi2", listener);
        assertThat(result.get(), is("D"));
        String s = "UPVOTES";
        String d = "UPVOTED";
        String f = "UPVOTEF";
        db.upvote(s, listener);
        assertThat(result.get(), is("S"));
        db.upvote(d, listener);
        assertThat(result.get(), is("D"));
        db.upvote(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void downvoteTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyPOIListener listener = new DatabaseProvider.ModifyPOIListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPoi(poi, emptyAddPOIListener);
        db.downvote("poi", listener);
        assertThat(result.get(), is("S"));
        db.downvote("poi2", listener);
        assertThat(result.get(), is("D"));
        String s = "DOWNVOTES";
        String d = "DOWNVOTED";
        String f = "DOWNVOTEF";
        db.downvote(s, listener);
        assertThat(result.get(), is("S"));
        db.downvote(d, listener);
        assertThat(result.get(), is("D"));
        db.downvote(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void findNearPOIsTest() {
        final Mutable<String> result = new Mutable<>("good");
        final Mutable<Integer> count = new Mutable<>(0);

        DatabaseProvider.FindNearPoisListener listener = new DatabaseProvider.FindNearPoisListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                count.set(count.get() + 1);
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPoi(poi, emptyAddPOIListener);
        PointOfInterest farPOI = new PointOfInterest("farPOI", new Position(10, 10),
                new TreeSet<String>(), "", 0, "", "");
        db.addPoi(farPOI, emptyAddPOIListener);
        db.findNearPois(new Position(1, 1), 200, listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));
        db.findNearPois(new Position(1000, 1000), 30, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void searchByTextTest() {
        final Mutable<String> result = new Mutable<>("good");
        final Mutable<Integer> count = new Mutable<>(0);
        DatabaseProvider.SearchPOIByTextListener listener = new DatabaseProvider.SearchPOIByTextListener() {
            @Override
            public void onNewValue(PointOfInterest poi) {
                count.set(count.get() + 1);
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addPoi(poi, emptyAddPOIListener);
        db.addPoi(poiWithName("iop"), emptyAddPOIListener);
        PointOfInterest countryPOI = new PointOfInterest("oip", new Position(0, 0),
                new TreeSet<String>(), "", 0, "poi", "");
        db.addPoi(countryPOI, emptyAddPOIListener);
        db.searchByText("poi", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(2));

        count.set(0);
        db.searchByText("SEARCHN", listener);
        assertThat(result.get(), is("good"));
        assertThat(count.get(), is(1));

        count.set(0);
        db.searchByText("SEARCHF", listener);
        assertThat(result.get(), is("F"));
        assertThat(count.get(), is(0));
    }

    @Test
    public void addUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.AddUserListener listener = new DatabaseProvider.AddUserListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onAlreadyExists() {
                result.set("A");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addUser(user, listener);
        assertThat(result.get(), is("S"));
        db.addUser(user, listener);
        assertThat(result.get(), is("A"));
        User s = new User("ADDUSERS", "ADDUSERS");
        User a = new User("ADDUSERA", "ADDUSERA");
        User f = new User("ADDUSERF", "ADDUSERF");
        db.addUser(s, listener);
        assertThat(result.get(), is("S"));
        db.addUser(a, listener);
        assertThat(result.get(), is("A"));
        db.addUser(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void getUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.GetUserListener listener = new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User u) {
                result.set("S");
            }


            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };
        db.getUserById("user", listener);
        assertThat(result.get(), is("D"));

        db.addUser(user, emptyAddUserListener);

        db.getUserById("user", listener);
        assertThat(result.get(), is("S"));
        db.getUserById("user2", listener);
        assertThat(result.get(), is("D"));
        String s = "GETUSERS";
        String d = "GETUSERD";
        String f = "GETUSERF";
        db.getUserById(s, listener);
        assertThat(result.get(), is("S"));
        db.getUserById(d, listener);
        assertThat(result.get(), is("D"));
        db.getUserById(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void deleteUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.DeleteUserListener listener = new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };
        db.addUser(user, emptyAddUserListener);

        db.deleterUserById("user", listener);
        assertThat(result.get(), is("S"));

        db.deleterUserById("user", listener);
        assertThat(result.get(), is("D"));

        String s = "DELETEUSERS";
        String d = "DELETEUSERD";
        String f = "DELETEUSERF";
        db.deleterUserById(s, listener);
        assertThat(result.get(), is("S"));
        db.deleterUserById(d, listener);
        assertThat(result.get(), is("D"));
        db.deleterUserById(f, listener);
        assertThat(result.get(), is("F"));
    }

    @Test
    public void modifyUserTest() {
        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyUserListener listener = new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                result.set("S");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }
        };

        db.addUser(user, emptyAddUserListener);

        db.modifyUser(user, listener);
        assertThat(result.get(), is("S"));
        db.modifyUser(user2, listener);
        assertThat(result.get(), is("D"));
        db.modifyUser(new User("DELETEUSERF", "DELETEUSERF"), listener);
        assertThat(result.get(), is("F"));

        User s = new User("MODIFYUSERS", "MODIFYUSERS");
        User d = new User("MODIFYUSERD", "MODIFYUSERD");
        User f = new User("MODIFYUSERF", "MODIFYUSERF");
        db.modifyUser(s, listener);
        assertThat(result.get(), is("S"));
        db.modifyUser(d, listener);
        assertThat(result.get(), is("D"));
        db.modifyUser(f, listener);
        assertThat(result.get(), is("F"));

    }
}

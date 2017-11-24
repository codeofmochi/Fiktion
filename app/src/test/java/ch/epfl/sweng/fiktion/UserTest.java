package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Config;

import static ch.epfl.sweng.fiktion.UserTest.Result.DOESNOTEXIST;
import static ch.epfl.sweng.fiktion.UserTest.Result.FAILURE;
import static ch.epfl.sweng.fiktion.UserTest.Result.FRIENDEXCEPTION;
import static ch.epfl.sweng.fiktion.UserTest.Result.NOTHING;
import static ch.epfl.sweng.fiktion.UserTest.Result.SUCCESS;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

/**
 * This class tests methods of the class USER
 * Created by Rodrigo on 22.10.2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    private User user;
    private User user1;
    private User userFR;
    private User userFakeF;
    private User userFakeR;
    private User userWVFav;

    private DatabaseProvider localDB = DatabaseProvider.getInstance();
    private DatabaseProvider.ModifyUserListener mUserListener;

    private AuthProvider.AuthListener authListener = new AuthProvider.AuthListener() {
        @Override
        public void onSuccess() {
            setResult(SUCCESS);
        }

        @Override
        public void onFailure() {
            setResult(FAILURE);
        }
    };

    private User.userListener userTestListener = new User.userListener() {
        @Override
        public void onSuccess() {
            setResult(SUCCESS);
        }

        @Override
        public void onFriendlistException() {
            setResult(FRIENDEXCEPTION);
        }

        @Override
        public void onDoesntExist() {
            setResult(DOESNOTEXIST);
        }

        @Override
        public void onFailure() {
            setResult(FAILURE);
        }
    };
    @Mock
    DatabaseReference dbRef, usersRef, userRef;
    @Mock
    DatabaseProvider mockDB;
    @Mock
    GeoFire geofire;
    @Mock
    DataSnapshot snapshot;
    @Captor
    ArgumentCaptor<DatabaseProvider.GetUserListener> getUserListenerArgumentCaptor;
    @Captor
    ArgumentCaptor<DatabaseProvider.ModifyUserListener> modifyUserListenerArgumentCaptor;

    public enum Result {SUCCESS, FAILURE, DOESNOTEXIST, FRIENDEXCEPTION, NOTHING}

    private UserTest.Result result;

    private void setResult(UserTest.Result result) {
        this.result = result;
    }

    @BeforeClass
    public static void setConfig() {
        Config.TEST_MODE = true;
    }

    @Before
    public void setUp() {

        result = NOTHING;

        // Initiating friendlists and friendRequests
        String[] frList = new String[]{"defaultID"};
        String[] rList = new String[]{"id1"};
        String[] fakeFList = new String[]{"idfake"};
        String[] fakeRList = new String[]{"idfake"};
        String[] favList = new String[]{"fav POI"};
        String[] whishList = new String[]{"wish POI"};
        String[] visitedList = new String[]{"vis POI"};

        // Initiating users
        user = new User("default", "defaultID");
        user1 = new User("user1", "id1");
        userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)), new LinkedList<String>(), true, new TreeSet<String>());
        userFakeF = new User("userFakeF", "idfakef", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<>(Arrays.asList(fakeFList)), new TreeSet<String>(), new LinkedList<String>(), true, new TreeSet<String>());
        userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)), new LinkedList<String>(), true, new TreeSet<String>());
        userWVFav = new User("userWVFav", "idwvfav", new TreeSet<>(Arrays.asList(favList)), new TreeSet<>(Arrays.asList(whishList)),
                new TreeSet<String>(), new TreeSet<String>(), new LinkedList<>(Arrays.asList(visitedList)), true, new TreeSet<String>());

        doNothing().when(mockDB).modifyUser(any(User.class), modifyUserListenerArgumentCaptor.capture());

    }


    @Test
    public void correctlyCreatesUser() {
        assertThat(user.getName(), is("default"));
        assertThat(user.getID(), is("defaultID"));
        assertThat(user.getFavourites().size(), is(0));
    }

    @Test
    public void testEquals() {
        User other = new User("other user", "defaultID", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());
        User almostEqual = new User("default", "id1", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());
        User same = new User("default", "defaultID", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());

        assertFalse(user.equals(null));
        assertFalse(user.equals(almostEqual));
        assertFalse(user.equals(other));
        assertTrue(user.equals(same));
        //test different classes
        assertFalse(user.equals(new FirebaseUser()));
    }


    @Test
    public void testDatabaseInteractionsFavourites() {

        DatabaseProvider.setInstance(mockDB);
        user.addFavourite("new POI", authListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", set, new TreeSet<String>(), new LinkedList<String>()).removeFavourite("new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testChangeNameLogic() {
        final List<User> dbUserList = ((LocalDatabaseProvider) DatabaseProvider.getInstance()).users;
        final String newName = "new";
        final User newUser = new User(newName, "defaultID");
        user.changeName(newName, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertThat(user.getName(), is(newName));
                int index = dbUserList.indexOf(newUser);
                User u = dbUserList.get(index);
                assertThat(u.getName(), is(newName));
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }


    @Test
    public void testDatabaseInteractionsChangeName() {

        DatabaseProvider.setInstance(mockDB);
        user.changeName("new", authListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testAddFavouriteLogic() {
        //change to Local database to test addFavourite logic

        user.addFavourite("new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getFavourites().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getFavourites().contains("new POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.addFavourite("new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertTrue(user.getFavourites().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getFavourites().contains("new POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }
        });
    }

    @Test
    public void testRemoveFavouriteLogic() {
        userWVFav.removeFavourite("fav POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertFalse(userWVFav.getFavourites().contains("fav POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertFalse(user.getFavourites().contains("fav POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        userWVFav.removeFavourite("fav POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertFalse(userWVFav.getFavourites().contains("fav POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertFalse(user.getFavourites().contains("fav POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }
        });
    }


    @Test
    public void testDatabaseInteractionsWishlist() {

        DatabaseProvider.setInstance(mockDB);
        user.addToWishlist("new POI", authListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", new TreeSet<String>(), set, new LinkedList<String>()).removeFromWishlist("new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testAddToWishlistLogic() {
        //change to Local database to test addFavourite logic

        user.addToWishlist("new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getWishlist().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getWishlist().contains("new POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.addToWishlist("new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertTrue(user.getWishlist().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getWishlist().contains("new POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }
        });
    }


    @Test
    public void testDatabaseInteractionsVisited() {

        DatabaseProvider.setInstance(mockDB);
        user.visit("new POI", authListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        LinkedList<String> list = new LinkedList<>();
        list.add("new POI");
        new User("", "", new TreeSet<String>(), new TreeSet<String>(), list).removeFromVisited("new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        DatabaseProvider.destroyInstance();
    }


    @Test
    public void testRemoveFromWishlistLogic() {
        userWVFav.removeFromWishlist("wish POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertFalse(userWVFav.getWishlist().contains("wish POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertFalse(user.getWishlist().contains("wish POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        userWVFav.removeFromWishlist("wish POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertFalse(userWVFav.getWishlist().contains("wish POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertFalse(user.getWishlist().contains("wish POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }
        });
    }

    @Test
    public void testVisitlistLogic() {
        //change to Local database to test addFavourite logic

        user.visit("new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getVisited().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getVisited().contains("new POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.visit("new POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertTrue(user.getVisited().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getVisited().contains("new POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }
        });
    }

    @Test
    public void testRemoveFromVisitedLogic() {
        userWVFav.removeFromVisited("vis POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertFalse(userWVFav.getVisited().contains("vis POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertFalse(userWVFav.getVisited().contains("vis POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        userWVFav.removeFromVisited("vis POI", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                assertFalse(userWVFav.getVisited().contains("vis POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertFalse(userWVFav.getVisited().contains("vis POI"));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }
        });
    }

    @Test
    public void testChangeProfilePrivacyLogic() {
        user.changeProfilePrivacy(false, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertTrue(!user.isPublicProfile());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }


    @Test
    public void testChangeProfilePrivacy() {

        DatabaseProvider.setInstance(mockDB);
        user.changeProfilePrivacy(true, authListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testSendFriendRequestLogic() {
        user.sendFriendRequest(user1.getID(), new User.userListener() {
            @Override
            public void onSuccess() {
                localDB.getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertThat(user.getRequests().size(), is(1));
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFriendlistException() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testSendFriendRequestToFriendLogic() {
        userFR.sendFriendRequest(user.getID(), new User.userListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFriendlistException() {
                // test passes
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testSendFriendRequestToNoneExistentUser() {
        User u = new User("u", "idu");
        user.sendFriendRequest(u.getID(), new User.userListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFriendlistException() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                // passes test
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }


    @Test
    public void testAcceptFriendRequestLogic() {
        userFR.acceptFriendRequest(user1.getID(), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                localDB.getUserById(userFR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getFriendlist().contains(user1.getID()));
                        assertTrue(user.getRequests().isEmpty());
                        localDB.getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                            @Override
                            public void onSuccess(User user) {
                                assertTrue(user.getFriendlist().contains(userFR.getID()));
                            }

                            @Override
                            public void onDoesntExist() {
                                Assert.fail();
                            }

                            @Override
                            public void onFailure() {
                                Assert.fail();
                            }
                        });
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }


    @Test
    public void testSendListeners() {
        doNothing().when(mockDB).getUserById(any(String.class), getUserListenerArgumentCaptor.capture());


        DatabaseProvider.setInstance(mockDB);
        user.sendFriendRequest(user1.getID(), userTestListener);

        DatabaseProvider.GetUserListener gAC = getUserListenerArgumentCaptor.getValue();

        gAC.onFailure();
        assertThat(result, is(FAILURE));
        gAC.onSuccess(user1);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        mUserListener.onDoesntExist();
        assertThat(result, is(Result.DOESNOTEXIST));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testAcceptFriendRequestNotInRequestsLogic() {
        user.acceptFriendRequest(user1.getID(), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                // test passes
            }
        });
    }

    @Test
    public void testAcceptFriendRequestNotStoredFriendLogic() {
        userFakeR.acceptFriendRequest(userFakeR.getRequests().toArray()[0].toString(), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                // test passes
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }


    @Test
    public void testAcceptFriendRequest() {

        doNothing().when(mockDB).getUserById(any(String.class), getUserListenerArgumentCaptor.capture());

        DatabaseProvider.ModifyUserListener testListener = new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onDoesntExist() {
                setResult(DOESNOTEXIST);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        DatabaseProvider.setInstance(mockDB);
        userFR.acceptFriendRequest(user1.getID(), testListener);

        DatabaseProvider.GetUserListener gAC = getUserListenerArgumentCaptor.getValue();
        gAC.onFailure();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        gAC.onSuccess(user1);
        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        mUserListener.onSuccess();
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testIgnoreFriendRequestsLogic() {
        userFR.ignoreFriendRequest(user1.getID(), new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                localDB.getUserById(userFR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getRequests().isEmpty());
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testIgnoreFriendRequestsNotInRequestsLogic() {
        user.ignoreFriendRequest(user1.getID(), new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                // test passes
            }
        });
    }

    @Test
    public void testIgnoreFriendRequestsFromNotStoredUserLogic() {

        // setUp
        String[] rL = new String[]{"id1"};
        User u = new User("u", "idu", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<>(Arrays.asList(rL)), new LinkedList<String>(), true, new TreeSet<String>());

        u.ignoreFriendRequest(user1.getID(), new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                // test passes
            }
        });
    }


    @Test
    public void testIgnoreFriendRequest() {


        DatabaseProvider.setInstance(mockDB);
        userFR.ignoreFriendRequest(user1.getID(), authListener);
        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testRemoveFromFriendlistLogic() {
        userFR.removeFromFriendlist(user.getID(), new User.userListener() {
            @Override
            public void onSuccess() {
                localDB.getUserById(userFR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getFriendlist().isEmpty());

                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFriendlistException() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testRemoveFromFriendListNotFriendLogic() {
        user.removeFromFriendlist(user1.getID(), new User.userListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFriendlistException() {
                // test passes
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testRemoveFromFriendListFriendNotStoredLogic() {
        userFakeF.removeFromFriendlist(userFakeF.getFriendlist().toArray()[0].toString(), new User.userListener() {
            @Override
            public void onSuccess() {
                localDB.getUserById(userFakeF.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertTrue(user.getFriendlist().isEmpty());
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        Assert.fail();
                    }
                });
            }

            @Override
            public void onFriendlistException() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    //TODO : REDO this test, is wrong
    @Test
    public void testRemoveFromFriendlist() {
        DatabaseProvider.setInstance(mockDB);
        doNothing().when(mockDB).getUserById(any(String.class), getUserListenerArgumentCaptor.capture());

        userFR.removeFromFriendlist(user.getID(), userTestListener);

        DatabaseProvider.GetUserListener gAC = getUserListenerArgumentCaptor.getValue();
        gAC.onFailure();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        gAC.onSuccess(user);
        mUserListener = modifyUserListenerArgumentCaptor.getValue();
//TODO: correct this
// end TODO
        DatabaseProvider.destroyInstance();

    }

}

package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;

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
import static org.mockito.Mockito.doAnswer;
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

    private DatabaseProvider localDB = new LocalDatabaseProvider();
    private DatabaseProvider.ModifyUserListener mUserListener;

    private void setDBList(DatabaseProvider.ModifyUserListener listener) {
        mUserListener = listener;
    }

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

    public enum Result {SUCCESS, FAILURE, DOESNOTEXIST, FRIENDEXCEPTION, NOTHING}

    private UserTest.Result result;

    private void setResult(UserTest.Result result) {
        this.result = result;
    }

    @Before
    public void setUp() {
        localDB = new LocalDatabaseProvider();
        result = NOTHING;

        // Initiating friendlists and friendRequests
        String[] frList = new String[] {"defaultID"};
        String[] rList = new String[] {"id1"};
        String[] fakeFList = new String[] {"idfake"};
        String[] fakeRList = new String[] {"idfake"};
        String[] favList = new String[] {"fav POI"};
        String[] whishList = new String[] {"wish POI"};
        String[] visitedList = new String[] {"vis POI"};

        // Initiating users
        user = new User("default", "defaultID", new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>());
        user1 = new User("user1", "id1");
        userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)), new LinkedList<String>(), true);
        userFakeF = new User("userFakeF", "idfakef", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<>(Arrays.asList(fakeFList)), new TreeSet<String>(), new LinkedList<String>(), true);
        userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)), new LinkedList<String>(), true);
        userWVFav = new User("userWVFav", "idwvfav", new TreeSet<>(Arrays.asList(favList)), new TreeSet<>(Arrays.asList(whishList)),
                new TreeSet<String>(), new TreeSet<String>(), new LinkedList<>(Arrays.asList(visitedList)), true);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                setDBList((DatabaseProvider.ModifyUserListener) invocation.getArgument(1));
                return null;
            }
        }).when(mockDB).modifyUser(any(User.class), any(DatabaseProvider.ModifyUserListener.class));

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

        user.addFavourite(mockDB, "new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", set, new TreeSet<String>(), new LinkedList<String>()).removeFavourite(mockDB, "new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
    }

    @Test
    public void testChangeNameLogic() {
        final String newName = "new";
        user.changeName(localDB, newName, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                assertThat(user.getName(), is(newName));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        assertThat(user.getName(), is(newName));
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
    public void testDatabaseInteractionsChangeName() {

        user.changeName(mockDB, "new", authListener);
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
    }

    @Test
    public void testAddFavouriteLogic() {
        //change to Local database to test addFavourite logic

        user.addFavourite(localDB, "new POI", new AuthProvider.AuthListener() {
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

        user.addFavourite(localDB, "new POI", new AuthProvider.AuthListener() {
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
        userWVFav.removeFavourite(localDB, "fav POI", new AuthProvider.AuthListener() {
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

        userWVFav.removeFavourite(localDB, "fav POI", new AuthProvider.AuthListener() {
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

        user.addToWishlist(mockDB, "new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", new TreeSet<String>(), set, new LinkedList<String>()).removeFromWishlist(mockDB, "new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
    }

    @Test
    public void testAddToWishlistLogic() {
        //change to Local database to test addFavourite logic

        user.addToWishlist(localDB, "new POI", new AuthProvider.AuthListener() {
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

        user.addToWishlist(localDB, "new POI", new AuthProvider.AuthListener() {
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

        user.visit(mockDB, "new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        LinkedList<String> list = new LinkedList<>();
        list.add("new POI");
        new User("", "", new TreeSet<String>(), new TreeSet<String>(), list).removeFromVisited(mockDB, "new POI", authListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
    }
    @Test
    public void testRemoveFromWishlistLogic() {
        userWVFav.removeFromWishlist(localDB, "wish POI", new AuthProvider.AuthListener() {
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

        userWVFav.removeFromWishlist(localDB, "wish POI", new AuthProvider.AuthListener() {
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

        user.visit(localDB, "new POI", new AuthProvider.AuthListener() {
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

        user.visit(localDB, "new POI", new AuthProvider.AuthListener() {
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
        userWVFav.removeFromVisited(localDB, "vis POI", new AuthProvider.AuthListener() {
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

        userWVFav.removeFromVisited(localDB, "vis POI", new AuthProvider.AuthListener() {
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
        user.changeProfilePrivacy(localDB, false, new AuthProvider.AuthListener() {
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

        AuthProvider.AuthListener testListener = new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        user.changeProfilePrivacy(mockDB, true, testListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
    }

    @Test
    public void testSendFriendRequestLogic() {
        user.sendFriendRequest(localDB, user1.getID(), new User.userListener() {
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
        userFR.sendFriendRequest(localDB, user.getID(), new User.userListener() {
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
        user.sendFriendRequest(localDB, u.getID(), new User.userListener() {
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
        userFR.acceptFriendRequest(localDB, user1.getID(), new DatabaseProvider.ModifyUserListener() {
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
    public void testSendListeners(){
        doNothing().when(mockDB).getUserById(any(String.class), getUserListenerArgumentCaptor.capture());

        User.userListener testListener = new User.userListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onFriendlistException() {
                setResult(Result.FRIENDEXCEPTION);
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
        user.sendFriendRequest(mockDB, user1.getID(), testListener);

        DatabaseProvider.GetUserListener gAC = getUserListenerArgumentCaptor.getValue();

        gAC.onFailure();
        assertThat(result, is(FAILURE));
        gAC.onSuccess(user1);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
    }

    @Test
    public void testAcceptFriendRequestNotInRequestsLogic() {
        user.acceptFriendRequest(localDB, user1.getID(), new DatabaseProvider.ModifyUserListener() {
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
        userFakeR.acceptFriendRequest(localDB, userFakeR.getRequests().toArray()[0].toString(), new DatabaseProvider.ModifyUserListener() {
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

        userFR.acceptFriendRequest(mockDB, user1.getID(), testListener);

        DatabaseProvider.GetUserListener gAC = getUserListenerArgumentCaptor.getValue();
        gAC.onFailure();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        gAC.onSuccess(user1);
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        mUserListener.onSuccess();
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));


    }

    @Test
    public void testIgnoreFriendRequestsLogic() {
        userFR.ignoreFriendRequest(localDB, user1.getID(), new AuthProvider.AuthListener() {
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
        user.ignoreFriendRequest(localDB, user1.getID(), new AuthProvider.AuthListener() {
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
        String[] rL = new String[] {"id1"};
        User u = new User("u", "idu", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<>(Arrays.asList(rL)), new LinkedList<String>(), true);

        u.ignoreFriendRequest(localDB, user1.getID(), new AuthProvider.AuthListener() {
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

        AuthProvider.AuthListener testListener = new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                setResult(SUCCESS);
            }

            @Override
            public void onFailure() {
                setResult(FAILURE);
            }
        };

        userFR.ignoreFriendRequest(mockDB, user1.getID(), testListener);

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
    }

    @Test
    public void testRemoveFromFriendlistLogic() {
        userFR.removeFromFriendlist(localDB, user.getID(), new User.userListener() {
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
        user.removeFromFriendlist(localDB, user1.getID(), new User.userListener() {
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
        userFakeF.removeFromFriendlist(localDB, userFakeF.getFriendlist().toArray()[0].toString(), new User.userListener() {
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

    @Test
    public void testRemoveFromFriendlist() {
        doNothing().when(mockDB).getUserById(any(String.class), getUserListenerArgumentCaptor.capture());

        User.userListener testListener = new User.userListener() {
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

        userFR.removeFromFriendlist(mockDB, user.getID(), testListener);

        DatabaseProvider.GetUserListener gAC = getUserListenerArgumentCaptor.getValue();
        gAC.onFailure();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        gAC.onSuccess(user);
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        mUserListener.onDoesntExist();
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(FAILURE));
        setResult(NOTHING);
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

    }

}

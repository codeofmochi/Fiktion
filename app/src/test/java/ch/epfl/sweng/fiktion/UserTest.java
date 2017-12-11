package ch.epfl.sweng.fiktion;

import junit.framework.Assert;

import org.junit.After;
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

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseUser;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Config;

import static ch.epfl.sweng.fiktion.UserTest.Result.DOESNOTEXIST;
import static ch.epfl.sweng.fiktion.UserTest.Result.FAILURE;
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
    private User userFR;
    private User userWVFav;
    private User userWithUpvoted;

    private PointOfInterest defPoi = poiWithName("poi");

    private DatabaseProvider localDB;

    private DatabaseProvider.ModifyUserListener mUserListener;

    private PointOfInterest poiWithName(String name) {
        return new PointOfInterest(name, new Position(0, 0),
                new TreeSet<String>(), "", 0, "", "");
    }

    private DatabaseProvider.AddPOIListener emptyAddPOIListener = new DatabaseProvider.AddPOIListener() {
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

    private DatabaseProvider.ModifyUserListener modifyUserListener = new DatabaseProvider.ModifyUserListener() {
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

    @Mock
    DatabaseProvider mockDB;
    @Captor
    ArgumentCaptor<DatabaseProvider.ModifyUserListener> modifyUserListenerArgumentCaptor;

    public enum Result {SUCCESS, FAILURE, DOESNOTEXIST, NOTHING}

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
        localDB = DatabaseProvider.getInstance();

        result = NOTHING;

        // Initiating friendlists and friendRequests
        String[] frList = new String[]{"defaultID"};
        String[] rList = new String[]{"id1"};
        String[] favList = new String[]{"fav POI"};
        String[] whishList = new String[]{"wish POI"};
        String[] visitedList = new String[]{"vis POI"};
        String[] upvotedList = new String[]{"poi"};

        // Initiating users
        user = new User("default", "defaultID");
        userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)), new LinkedList<String>(),
                true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));
        userWVFav = new User("userWVFav", "idwvfav", new TreeSet<>(Arrays.asList(favList)),
                new TreeSet<>(Arrays.asList(whishList)), new TreeSet<String>(), new TreeSet<String>(),
                new LinkedList<>(Arrays.asList(visitedList)), true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));
        userWithUpvoted = new User("userWVFav", "idwvfav", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<String>(), new LinkedList<String>(),
                true, new TreeSet<>(Arrays.asList(upvotedList)), new Settings(Settings.DEFAULT_SEARCH_RADIUS));

        doNothing().when(mockDB).modifyUser(any(User.class), modifyUserListenerArgumentCaptor.capture());

    }

    @After
    public void destroy() {
        DatabaseProvider.destroyInstance();
    }


    @Test
    public void correctlyCreatesUser() {
        assertThat(user.getName(), is("default"));
        assertThat(user.getID(), is("defaultID"));
        assertThat(user.getFavourites().size(), is(0));
    }


    @Test
    public void testUpVotingLogic() {
        DatabaseProvider.getInstance().addPOI(defPoi, emptyAddPOIListener);
        final String poiName = defPoi.name();
        userWithUpvoted.upVote(poiName, new DatabaseProvider.ModifyUserListener() {
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
                //should fail
            }
        });

        user.upVote(poiName, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertThat(user.getUpvoted().contains(poiName), is(true));
                DatabaseProvider.getInstance().getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertThat(user.getUpvoted().contains(poiName), is(true));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testRemovePoiVotingLogic() {
        DatabaseProvider.getInstance().addPOI(defPoi, emptyAddPOIListener);
        final String poiName = defPoi.name();
        userWithUpvoted.removeVote(poiName, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertThat(userWithUpvoted.getUpvoted().contains(poiName), is(false));
                DatabaseProvider.getInstance().getUserById(userWithUpvoted.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertThat(user.getUpvoted().contains(poiName), is(false));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
    public void testUpVotingListeners() {
        DatabaseProvider.setInstance(mockDB);
        user.upVote(defPoi.name(), modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testRemoveVotingListeners() {
        DatabaseProvider.setInstance(mockDB);
        userWithUpvoted.removeVote(defPoi.name(), modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testEquals() {
        User other = new User("other user", "defaultID");
        User almostEqual = new User("default", "id1");
        User same = new User("default", "defaultID");

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
        user.addFavourite("new POI", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", set, new TreeSet<String>(), new LinkedList<String>()).removeFavourite("new POI", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testChangeNameLogic() {
        final List<User> dbUserList = ((LocalDatabaseProvider) DatabaseProvider.getInstance()).users;
        final String newName = "new";
        final User newUser = new User(newName, "defaultID");
        user.changeName(newName, new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertThat(user.getName(), is(newName));
                int index = dbUserList.indexOf(newUser);
                User u = dbUserList.get(index);
                assertThat(u.getName(), is(newName));
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
    public void testDatabaseInteractionsChangeName() {

        DatabaseProvider.setInstance(mockDB);
        user.changeName("new", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));
        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));
        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }


    @Test
    public void testAddFavouriteLogic() {
        //change to Local database to test addFavourite logic

        user.addFavourite("new POI", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getFavourites().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertTrue(user.getFavourites().contains("new POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });

        user.addFavourite("new POI", new DatabaseProvider.ModifyUserListener() {
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
                assertTrue(user.getFavourites().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertTrue(user.getFavourites().contains("new POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
        });
    }

    @Test
    public void testRemoveFavouriteLogic() {
        userWVFav.removeFavourite("fav POI", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertFalse(userWVFav.getFavourites().contains("fav POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertFalse(user.getFavourites().contains("fav POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        userWVFav.removeFavourite("fav POI", new DatabaseProvider.ModifyUserListener() {
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
                assertFalse(userWVFav.getFavourites().contains("fav POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertFalse(user.getFavourites().contains("fav POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
        });
    }


    @Test
    public void testDatabaseInteractionsWishlist() {

        DatabaseProvider.setInstance(mockDB);
        user.addToWishlist("new POI", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        TreeSet<String> set = new TreeSet<>();
        set.add("new POI");
        new User("", "", new TreeSet<String>(), set, new LinkedList<String>()).removeFromWishlist("new POI", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();
        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));
        DatabaseProvider.destroyInstance();
    }

    @Test
    public void testAddToWishlistLogic() {
        //change to Local database to test addFavourite logic

        user.addToWishlist("new POI", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getWishlist().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertTrue(user.getWishlist().contains("new POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.addToWishlist("new POI", new DatabaseProvider.ModifyUserListener() {
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
                assertTrue(user.getWishlist().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertTrue(user.getWishlist().contains("new POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
        });
    }


    @Test
    public void testDatabaseInteractionsVisited() {

        DatabaseProvider.setInstance(mockDB);
        user.visit("new POI", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        userWVFav.removeFromVisited("vis POI", modifyUserListener);

        mUserListener = modifyUserListenerArgumentCaptor.getValue();

        mUserListener.onSuccess();
        assertThat(result, is(SUCCESS));

        mUserListener.onDoesntExist();
        assertThat(result, is(DOESNOTEXIST));

        mUserListener.onFailure();
        assertThat(result, is(FAILURE));

        DatabaseProvider.destroyInstance();
    }


    @Test
    public void testRemoveFromWishlistLogic() {
        userWVFav.removeFromWishlist("wish POI", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertFalse(userWVFav.getWishlist().contains("wish POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertFalse(user.getWishlist().contains("wish POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        userWVFav.removeFromWishlist("wish POI", new DatabaseProvider.ModifyUserListener() {
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
                assertFalse(userWVFav.getWishlist().contains("wish POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertFalse(user.getWishlist().contains("wish POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
        });
    }

    @Test
    public void testVisitlistLogic() {
        //change to Local database to test addFavourite logic

        user.visit("new POI", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertTrue(user.getVisited().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertTrue(user.getVisited().contains("new POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        user.visit("new POI", new DatabaseProvider.ModifyUserListener() {
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
                assertTrue(user.getVisited().contains("new POI"));
                localDB.getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertTrue(user.getVisited().contains("new POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
        });
    }

    @Test
    public void testRemoveFromVisitedLogic() {
        userWVFav.removeFromVisited("vis POI", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                assertFalse(userWVFav.getVisited().contains("vis POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertFalse(userWVFav.getVisited().contains("vis POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        userWVFav.removeFromVisited("vis POI", new DatabaseProvider.ModifyUserListener() {
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
                assertFalse(userWVFav.getVisited().contains("vis POI"));
                localDB.getUserById(userWVFav.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User user) {
                        assertFalse(userWVFav.getVisited().contains("vis POI"));
                    }

                    @Override
                    public void onModifiedValue(User user) {
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
    public void testAddRequest() {
        String someUserID = "someUserID";
        user.addRequest(someUserID);
        assertTrue(user.getRequests().contains(someUserID));
    }

    @Test
    public void testAddRequestAndGet() {
        String someUserID = "someUserID";
        User u = user.addRequestAndGet(someUserID);
        assertTrue(u.getRequests().contains(someUserID));
    }

    @Test
    public void testRemoveRequest() {
        String id = "id1";
        userFR.removeRequest(id);
        assertFalse(userFR.getRequests().contains(id));
    }

    @Test
    public void testRemoveRequestAndGet() {
        String id = "id1";
        User u = userFR.removeRequestAndGet(id);
        assertFalse(u.getRequests().contains(id));
    }

    @Test
    public void testAddFriend() {
        String someUserID = "someUserID";
        user.addFriend(someUserID);
        assertTrue(user.getFriendlist().contains(someUserID));
    }

    @Test
    public void testAddFriendAndGet() {
        String someUserID = "someUserID";
        User u = user.addFriendAndGet(someUserID);
        assertTrue(u.getFriendlist().contains(someUserID));
    }

    @Test
    public void testRemoveFriend() {
        String id = "defaultID";
        userFR.removeFriend(id);
        assertFalse(userFR.getFriendlist().contains(id));
    }

    @Test
    public void testRemoveFriendAndGet() {
        String id = "defaultID";
        User u = userFR.removeFriendAndGet(id);
        assertFalse(u.getFriendlist().contains(id));
    }
}

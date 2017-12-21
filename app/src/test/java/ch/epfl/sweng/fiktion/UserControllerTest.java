package ch.epfl.sweng.fiktion;

import org.junit.After;
import org.junit.Assert;
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
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.controllers.UserController;
import ch.epfl.sweng.fiktion.models.PersonalUserInfos;
import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.utils.LocalDatabaseFiller;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

/**
 * This class tests methods of the class UserController
 * Created by Christoph on 01.12.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private AuthProvider auth;
    private User user;
    private User user1;
    private User userFR;
    private User userFakeR;
    private UserController.ConstructStateListener successConstrListener = new UserController.ConstructStateListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onModified() {
            Assert.fail();
        }

        @Override
        public void onFailure() {
            Assert.fail();
        }
    };

    @BeforeClass
    public static void setConfig() {
        Config.TEST_MODE = true;
    }

    @Before
    public void setUp() {
        LocalDatabaseFiller.addBasicUsers();

        auth = AuthProvider.getInstance();
        // Initiating friendlists and friendRequests
        String[] frList = new String[]{"defaultID"};
        String[] rList = new String[]{"id1"};
        String[] fakeRList = new String[]{"idfake"};

        // Initiating users
        user = new User("default", "defaultID");
        user1 = new User("user1", "id1");
        userFR = new User("userFR", "idfr", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<>(Arrays.asList(frList)), new TreeSet<>(Arrays.asList(rList)), new LinkedList<String>(),
                true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS), new PersonalUserInfos());
        userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)), new LinkedList<String>(),
                true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS), new PersonalUserInfos());
    }

    @After
    public void cleanUp() {
        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
    }

    @Test
    public void correctlyCreatesUserController() {
        UserController uc = new UserController(successConstrListener);

        assertTrue(uc.getLocalUser().equals(user));
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionOnFailCreateUserController() {
        AuthProvider.getInstance().signOut();
        final UserController uc = new UserController(new UserController.ConstructStateListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onModified() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
            }
        });
    }

    @Test
    public void testGetLocalUser() {
        UserController uc = new UserController(successConstrListener);

        User u = uc.getLocalUser();
        assertTrue(u.equals(user));
    }

    @Test
    public void testSendFriendRequestLogic() {
        UserController uc = new UserController(successConstrListener);

        uc.sendFriendRequest(user1.getID(), new UserController.RequestListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User u) {
                        assertTrue(u.getRequests().contains(user.getID()));
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

            @Override
            public void onAlreadyFriend() {
                Assert.fail();
            }

            @Override
            public void onNewFriend() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testSendFriendRequestLogicOnDoesntExistLogic() {
        UserController uc = new UserController(successConstrListener);

        uc.sendFriendRequest("someRandomID", new UserController.RequestListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {
                Assert.fail();
            }

            @Override
            public void onAlreadyFriend() {
                Assert.fail();
            }

            @Override
            public void onNewFriend() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testSendFriendRequestOnAlreadyFriendLogic() {
        ((LocalAuthProvider) auth).currUser = userFR;
        UserController uc = new UserController(successConstrListener);

        uc.sendFriendRequest(user.getID(), new UserController.RequestListener() {
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
                Assert.fail();
            }

            @Override
            public void onAlreadyFriend() {
                assertTrue(userFR.getFriendlist().contains(user.getID()));
            }

            @Override
            public void onNewFriend() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testSendFriendRequestOnNewFriendLogic() {
        ((LocalAuthProvider) auth).currUser = userFR;
        UserController uc = new UserController(successConstrListener);

        uc.sendFriendRequest(user1.getID(), new UserController.RequestListener() {
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
                Assert.fail();
            }

            @Override
            public void onAlreadyFriend() {
                Assert.fail();
            }

            @Override
            public void onNewFriend() {
                DatabaseProvider.getInstance().getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User u) {
                        assertTrue(u.getFriendlist().contains(userFR.getID()));
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
    public void testAcceptFriendRequestLogic() {
        ((LocalAuthProvider) auth).currUser = userFR;
        UserController uc = new UserController(successConstrListener);

        uc.acceptFriendRequest(user1.getID(), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(userFR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User u) {
                        assertTrue(u.getFriendlist().contains(user1.getID()));
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
    public void testAcceptFriendRequestOnDoesntExistLogic() {
        ((LocalAuthProvider) auth).currUser = userFakeR;
        UserController uc = new UserController(successConstrListener);

        uc.acceptFriendRequest("idfake", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                DatabaseProvider.getInstance().getUserById(userFakeR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User u) {
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
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testIgnoreFriendRequestLogic() {
        ((LocalAuthProvider) auth).currUser = userFR;
        UserController uc = new UserController(successConstrListener);

        uc.ignoreFriendRequest(user1.getID(), new UserController.BinaryListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User u) {
                        assertTrue(u.getRequests().isEmpty());
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
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testRemoveFromFriendListLogic() {
        ((LocalAuthProvider) auth).currUser = userFR;
        UserController uc = new UserController(successConstrListener);

        uc.removeFromFriendList(user.getID(), new UserController.BinaryListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onNewValue(User u) {
                        assertTrue(u.getFriendlist().isEmpty());
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
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Mock
    private AuthProvider authMock;

    @Test
    public void constructorTest() {
        AuthProvider.setInstance(authMock);
        ArgumentCaptor<DatabaseProvider.GetUserListener> getUserListener = ArgumentCaptor.forClass(DatabaseProvider.GetUserListener.class);
        doNothing().when(authMock).getCurrentUser(getUserListener.capture());

        final Mutable<String> result = new Mutable<>("");

        UserController.ConstructStateListener listener = new UserController.ConstructStateListener() {
            @Override
            public void onModified() {
                result.set("M");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }

            @Override
            public void onSuccess() {
                result.set("S");
            }
        };

        UserController uc = new UserController(listener);

        getUserListener.getValue().onNewValue(user);
        assertThat(result.get(), is("S"));
        assertThat(uc.getLocalUser(), is(user));

        getUserListener.getValue().onModifiedValue(user1);
        assertThat(result.get(), is("M"));
        assertThat(uc.getLocalUser(), is(user1));

        String exceptionMessage = "nothing";

        try {
            getUserListener.getValue().onDoesntExist();
        } catch (IllegalStateException e) {
            exceptionMessage = e.getMessage();
        }
        assertThat(result.get(), is("F"));
        assertThat(exceptionMessage, is("The local user does not exist"));
        result.set("");

        try {
            getUserListener.getValue().onFailure();
        } catch (IllegalStateException e) {
            exceptionMessage = e.getMessage();
        }
        assertThat(result.get(), is("F"));
        assertThat(exceptionMessage, is("The was an error fetching local user"));
    }

    @Test
    public void constructorWithUserTest() {
        UserController uc = new UserController(user);
        assertThat(uc.getLocalUser(), is(user));
    }

    @Mock
    private DatabaseProvider dbMock;

    @Captor
    private ArgumentCaptor<DatabaseProvider.GetUserListener> getUserListener;

    @Captor
    private ArgumentCaptor<DatabaseProvider.ModifyUserListener> modifyUserListener;

    @Test
    public void sendFriendRequestTest() {
        DatabaseProvider.setInstance(dbMock);
        User u1 = new User("u1", "u1Id");
        User u2 = new User("u2", "u2Id");
        u1.addRequest(u2.getID());
        UserController uc = new UserController(u1);

        final Mutable<String> result = new Mutable<>("");
        UserController.RequestListener listener = new UserController.RequestListener() {
            @Override
            public void onAlreadyFriend() {
                result.set("A");
            }

            @Override
            public void onNewFriend() {
                result.set("N");
            }

            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }

            @Override
            public void onSuccess() {
                result.set("S");
            }
        };

        doNothing().when(dbMock).getUserById(anyString(), getUserListener.capture());

        uc.sendFriendRequest(u2.getID(), listener);

        getUserListener.getValue().onModifiedValue(null);
        assertThat(result.get(), is(""));

        getUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("D"));

        getUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        doNothing().when(dbMock).modifyUser(any(User.class), modifyUserListener.capture());
        getUserListener.getValue().onNewValue(u2);
        assertTrue(u2.getFriendlist().contains(u1.getID()));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("D"));

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        modifyUserListener.getValue().onSuccess();
        assertTrue(u1.getFriendlist().contains(u2.getID()));
        assertFalse(u1.getRequests().contains(u2.getID()));

        modifyUserListener.getValue().onSuccess();
        assertThat(result.get(), is("N"));
        assertTrue(u1.getFriendlist().contains(u2.getID()));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("F"));
        result.set("");

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        u2.removeFriend(u1.getID());
        assertFalse(u1.getFriendlist().contains(u2.getID()));
        assertFalse(u1.getRequests().contains(u2.getID()));
        assertFalse(u2.getFriendlist().contains(u1.getID()));
        assertFalse(u2.getRequests().contains(u1.getID()));


        result.set("");
        uc.sendFriendRequest(u2.getID(), listener);

        getUserListener.getValue().onModifiedValue(null);
        assertThat(result.get(), is(""));

        getUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("D"));

        getUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));


        getUserListener.getValue().onNewValue(u2);
        assertTrue(u2.getRequests().contains(u1.getID()));

        modifyUserListener.getValue().onSuccess();
        assertThat(result.get(), is("S"));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("D"));

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));


        u2.addFriend(u1.getID());
        getUserListener.getValue().onNewValue(u2);
        assertTrue(u1.getFriendlist().contains(u2.getID()));

        modifyUserListener.getValue().onSuccess();
        assertThat(result.get(), is("N"));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("F"));
        assertFalse(u1.getFriendlist().contains(u2.getID()));
        result.set("");

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));
    }

    @Test
    public void acceptFriendRequest() {
        DatabaseProvider.setInstance(dbMock);
        User u1 = new User("u1", "u1Id");
        User u2 = new User("u2", "u2Id");
        u1.addRequest(u2.getID());
        UserController uc = new UserController(u1);

        final Mutable<String> result = new Mutable<>("");
        DatabaseProvider.ModifyUserListener listener = new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onDoesntExist() {
                result.set("D");
            }

            @Override
            public void onFailure() {
                result.set("F");
            }

            @Override
            public void onSuccess() {
                result.set("S");
            }
        };

        doNothing().when(dbMock).getUserById(anyString(), getUserListener.capture());
        uc.acceptFriendRequest(u2.getID(), listener);

        getUserListener.getValue().onModifiedValue(null);
        assertThat(result.get(), is(""));

        getUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("D"));

        getUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        doNothing().when(dbMock).modifyUser(any(User.class), modifyUserListener.capture());
        getUserListener.getValue().onNewValue(u2);
        assertTrue(u2.getFriendlist().contains(u1.getID()));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("D"));

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        result.set("");
        modifyUserListener.getValue().onSuccess();
        assertThat(result.get(), is(""));

        modifyUserListener.getValue().onSuccess();
        assertTrue(u1.getFriendlist().contains(u2.getID()));
        assertThat(result.get(), is("S"));

        modifyUserListener.getValue().onDoesntExist();
        assertFalse(u1.getFriendlist().contains(u2.getID()));
        assertThat(result.get(), is("F"));
        result.set("");

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));
    }

    @Test
    public void ignoreFriendRequestTest() {
        DatabaseProvider.setInstance(dbMock);
        User u1 = new User("u1", "u1Id");
        User u2 = new User("u2", "u2Id");
        u1.addRequest(u2.getID());
        UserController uc = new UserController(u1);

        final Mutable<String> result = new Mutable<>("");
        UserController.BinaryListener listener = new UserController.BinaryListener() {
            @Override
            public void onFailure() {
                result.set("F");
            }

            @Override
            public void onSuccess() {
                result.set("S");
            }
        };

        doNothing().when(dbMock).modifyUser(any(User.class), modifyUserListener.capture());
        uc.ignoreFriendRequest(u2.getID(), listener);

        modifyUserListener.getValue().onSuccess();
        assertThat(result.get(), is("S"));
        assertFalse(u1.getRequests().contains(u2.getID()));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("F"));
        assertTrue(u1.getRequests().contains(u2.getID()));
        u1.removeRequest(u2.getID());
        result.set("");

        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));
        assertTrue(u1.getRequests().contains(u2.getID()));
    }

    @Test
    public void removeFromFriendList() {
        DatabaseProvider.setInstance(dbMock);
        User u1 = new User("u1", "u1Id");
        User u2 = new User("u2", "u2Id");
        u1.addFriend(u2.getID());
        u2.addFriend(u1.getID());
        UserController uc = new UserController(u1);

        final Mutable<String> result = new Mutable<>("");
        UserController.BinaryListener listener = new UserController.BinaryListener() {
            @Override
            public void onFailure() {
                result.set("F");
            }

            @Override
            public void onSuccess() {
                result.set("S");
            }
        };

        doNothing().when(dbMock).getUserById(anyString(), getUserListener.capture());
        uc.removeFromFriendList(u2.getID(), listener);
        assertThat(result.get(), is(""));

        getUserListener.getValue().onModifiedValue(null);
        assertThat(result.get(), is(""));

        getUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        result.set("");
        doNothing().when(dbMock).modifyUser(any(User.class), modifyUserListener.capture());
        getUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is(""));
        assertFalse(u1.getFriendlist().contains(u2.getID()));

        modifyUserListener.getValue().onSuccess();
        assertThat(result.get(), is("S"));
        assertFalse(u1.getFriendlist().contains(u2.getID()));

        modifyUserListener.getValue().onDoesntExist();
        assertThat(result.get(), is("F"));
        assertTrue(u1.getFriendlist().contains(u2.getID()));

        result.set("");
        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));
        assertTrue(u1.getFriendlist().contains(u2.getID()));


        getUserListener.getValue().onNewValue(u2);
        assertFalse(u2.getFriendlist().contains(u1.getID()));

        result.set("");
        modifyUserListener.getValue().onFailure();
        assertThat(result.get(), is("F"));

        modifyUserListener.getValue().onSuccess();
        assertFalse(u1.getFriendlist().contains(u2.getID()));

        u1.addFriend(u2.getID());
        getUserListener.getValue().onNewValue(u2);
        modifyUserListener.getValue().onDoesntExist();
        assertFalse(u1.getFriendlist().contains(u2.getID()));
    }
}

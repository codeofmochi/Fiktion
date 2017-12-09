package ch.epfl.sweng.fiktion;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.controllers.UserController;
import ch.epfl.sweng.fiktion.models.Settings;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.utils.Config;

import static junit.framework.Assert.assertTrue;

/**
 * This class tests methods of the class UserController
 * Created by Christoph on 01.12.2017.
 */

public class UserControllerTest {

    private AuthProvider auth;
    private User user;
    private User user1;
    private User userFR;
    private User userFakeR;

    @BeforeClass
    public static void setConfig() {
        Config.TEST_MODE = true;
    }

    @Before
    public void setUp() {

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
                true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));
        userFakeR = new User("userFakeR", "idfaker", new TreeSet<String>(), new TreeSet<String>(),
                new TreeSet<String>(), new TreeSet<>(Arrays.asList(fakeRList)), new LinkedList<String>(),
                true, new TreeSet<String>(), new Settings(Settings.DEFAULT_SEARCH_RADIUS));
    }

    @After
    public void cleanUp() {
        DatabaseProvider.destroyInstance();
        AuthProvider.destroyInstance();
    }

    @Test
    public void correctlyCreatesUserController() {
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

        User u = uc.getLocalUser();
        assertTrue(u.equals(user));
    }

    @Test
    public void testSendFriendRequestLogic() {
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

        uc.sendFriendRequest(user1.getID(), new UserController.RequestListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User u) {
                        assertTrue(u.getRequests().contains(user.getID()));
                    }

                    @Override
                    public void onModified(User user) {
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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

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
                    public void onSuccess(User u) {
                        assertTrue(u.getFriendlist().contains(userFR.getID()));
                    }

                    @Override
                    public void onModified(User user) {
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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

        uc.acceptFriendRequest(user1.getID(), new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(userFR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User u) {
                        assertTrue(u.getFriendlist().contains(user1.getID()));
                    }

                    @Override
                    public void onModified(User user) {
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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

        uc.acceptFriendRequest("idfake", new DatabaseProvider.ModifyUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {
                DatabaseProvider.getInstance().getUserById(userFakeR.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User u) {
                    }

                    @Override
                    public void onModified(User user) {
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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

        uc.ignoreFriendRequest(user1.getID(), new UserController.BinaryListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(user1.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User u) {
                        assertTrue(u.getRequests().isEmpty());
                    }

                    @Override
                    public void onModified(User user) {
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
        UserController uc = new UserController(new UserController.ConstructStateListener() {
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
        });

        uc.removeFromFriendList(user.getID(), new UserController.BinaryListener() {
            @Override
            public void onSuccess() {
                DatabaseProvider.getInstance().getUserById(user.getID(), new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User u) {
                        assertTrue(u.getFriendlist().isEmpty());
                    }

                    @Override
                    public void onModified(User user) {
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

}

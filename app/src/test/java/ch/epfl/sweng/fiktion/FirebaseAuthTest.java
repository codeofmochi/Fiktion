
package ch.epfl.sweng.fiktion;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseAuthProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class FirebaseAuthTest {

    private String email = "test@epfl.ch";
    private String password = "testing";
    @Mock
    FirebaseAuth fbAuth;
    @Mock
    DatabaseReference dbRef;
    @Mock
    FirebaseUser fbUser;
    @Mock
    GeoFire geofire;
    @Mock
    Task<AuthResult> taskAuthSucceedResult;
    @Mock
    Task<AuthResult> taskAuthFailResult;
    @Mock
    Task<Void> taskVoidSucceedResult;
    @Mock
    Task<Void> taskVoidFailResult;
    @Mock
    AuthResult result;
    @Mock
    OnCompleteListener<AuthResult> onCompleteAuthListener;
    @Mock
    UserProfileChangeRequest.Builder updateProfileBuilder;
    @Mock
    UserProfileChangeRequest updateProfile;
    @Mock
    OnCompleteListener<Void> onCompleteVoidListener;
    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> testOnCompleteAuthListener;
    @Captor
    private ArgumentCaptor<OnCompleteListener<Void>> testOnCompleteVoidListener;

    private FirebaseAuthProvider auth;
    private FirebaseDatabaseProvider database = new FirebaseDatabaseProvider(dbRef,geofire);

    @Before
    public void setUp() {
        auth = new FirebaseAuthProvider(fbAuth);
        //Providers.database = database;
        setTasks();

    }

    private void setTasks() {
        Mockito.when(taskAuthSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskAuthSucceedResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthSucceedResult);
        Mockito.when(taskAuthFailResult.isComplete()).thenReturn(true);
        Mockito.when(taskAuthFailResult.isSuccessful()).thenReturn(false);
        Mockito.when(taskAuthFailResult.addOnCompleteListener(testOnCompleteAuthListener.capture())).
                thenReturn(taskAuthFailResult);
        Mockito.when(taskVoidSucceedResult.isComplete()).thenReturn(true);
        Mockito.when(taskVoidSucceedResult.isSuccessful()).thenReturn(true);
        Mockito.when(taskVoidSucceedResult.addOnCompleteListener(testOnCompleteVoidListener.capture())).
                thenReturn(taskVoidSucceedResult);
        Mockito.when(taskVoidFailResult.isComplete()).thenReturn(true);
        Mockito.when(taskVoidFailResult.isSuccessful()).thenReturn(false);
        Mockito.when(taskVoidFailResult.addOnCompleteListener(testOnCompleteVoidListener.capture())).
                thenReturn(taskVoidFailResult);
    }

    /*
        @Test
        public void testSuccessfulDeleteAccount() {
            //test successful
            Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
            Mockito.when(fbUser.delete()).thenReturn(taskVoidSucceedResult);
            Mockito.when()
            auth.deleteAccount(new AuthProvider.AuthListener() {
                @Override
                public void onSuccess() {
                    //successfully deleted in firebase
                    Mockito.verify(fbUser.delete());
                }

                @Override
                public void onFailure() {
                    Assert.fail();
                }
            }, new DatabaseProvider.DeleteUserListener() {
                @Override
                public void onSuccess() {
                    //successfully deleted in the database
                    Mockito.verify(fbUser.delete());
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
            testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
        }
    */

    @Test
    public void testDoesntExistDeleteAccount() {
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.delete()).thenReturn(taskVoidFailResult);
        auth.deleteAccount(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.delete()).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }
        }, new DatabaseProvider.DeleteUserListener() {
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
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    @Test
    public void testNotLoggedInDeleteAccount() {
        //test successful
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);

        auth.deleteAccount(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                //success
            }
        }, new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Test
    public void succeedSendPasswordResetEmail() {
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbAuth.sendPasswordResetEmail(email)).thenReturn(taskVoidSucceedResult);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.sendPasswordResetEmail(email)).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);

        //try to send an email verification while no user is connected
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                //sucess
            }
        });
    }

    @Test
    public void failSendPasswordResetEmail() {
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbAuth.sendPasswordResetEmail(email)).thenReturn(taskVoidFailResult);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbAuth.sendPasswordResetEmail(email)).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    @Test
    public void testNoUserSendPasswordResetEmail() {
        //try to send a password reset email while the user is not connected
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbAuth).getCurrentUser();
            }
        });
        //try to send a password reset email without a correct email
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(null);
        auth.sendPasswordResetEmail(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser).getEmail();
            }
        });
    }

    @Test
    public void SuccedUpdateEmail() {
        final String newEmail = "new@email.ch";
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.updateEmail(newEmail)).thenReturn(taskVoidSucceedResult);

        auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbUser.updateEmail(newEmail)).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }

    @Test
    public void failUpdateEmail() {
        final String newEmail = "new@email.ch";
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.updateEmail(newEmail)).thenReturn(taskVoidFailResult);

        auth.changeEmail(newEmail, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.updateEmail(newEmail)).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }
        });
        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    @Test
    public void succedSendEmailVerification() {

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.sendEmailVerification()).thenReturn(taskVoidSucceedResult);
        auth.sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbUser.sendEmailVerification()).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidSucceedResult);
    }

    @Test
    public void failSendEmailVerification() {

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.sendEmailVerification()).thenReturn(taskVoidFailResult);
        auth.sendEmailVerification(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbUser.sendEmailVerification()).addOnCompleteListener(testOnCompleteVoidListener.capture());
            }


        });

        testOnCompleteVoidListener.getValue().onComplete(taskVoidFailResult);
    }

    @Test
    public void failCreateUser() {
        Mockito.when(fbAuth.createUserWithEmailAndPassword(email, password)).thenReturn(taskAuthFailResult);
        auth.createUserWithEmailAndPassword(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbAuth).createUserWithEmailAndPassword(email, password);
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthFailResult);
    }

    @Test
    public void succeedSignIn() {
        setTasks();
        Mockito.when(fbAuth.signInWithEmailAndPassword(email, password)).thenReturn(taskAuthSucceedResult);

        auth.signIn(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Mockito.verify(fbAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(testOnCompleteAuthListener.capture());
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthSucceedResult);
    }

    @Test
    public void failSignIn() {
        setTasks();
        Mockito.when(fbAuth.signInWithEmailAndPassword(email, password)).thenReturn(taskAuthFailResult);

        auth.signIn(email, password, new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Mockito.verify(fbAuth.signInWithEmailAndPassword(email, password)).addOnCompleteListener(testOnCompleteAuthListener.capture());
            }
        });
        testOnCompleteAuthListener.getValue().onComplete(taskAuthFailResult);
    }

    @Test
    public void testAuthSignOut() {
        Mockito.doNothing().when(fbAuth).signOut();
        auth.signOut();
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);
        auth.getCurrentUser(new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                Assert.fail();
            }

            @Override
            public void onDoesntExist() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Test
    public void testGetters() {
        final String email = "test@test.ch";

        Mockito.when(fbAuth.getCurrentUser()).thenReturn(fbUser);
        Mockito.when(fbUser.getEmail()).thenReturn(email);
        Mockito.when(fbUser.isEmailVerified()).thenReturn(true);
        assertThat(auth.isConnected(), is(true));
        assertThat(auth.getEmail(), is(email));
        assertThat(auth.isEmailVerified(), is(true));
        Mockito.when(fbUser.isEmailVerified()).thenReturn(false);
        assertThat(auth.isEmailVerified(), is(false));

        //null user return
        Mockito.when(fbAuth.getCurrentUser()).thenReturn(null);
        assertThat(auth.isConnected(), is(false));
        assertThat(auth.getEmail(), is(""));
        assertThat(auth.isEmailVerified(), is(false));


    }

    @Test
    public void testValidaters(){
        //validate email
        assertThat(auth.validateEmail(email), is(""));
        assertThat(auth.validateEmail("invalidemail"), is("Requires a valid email"));
        //validate password
        assertThat(auth.validatePassword(password), is(""));
        assertThat(auth.validatePassword(""), is("Requires a valid password"));
        assertThat(auth.validatePassword("1234"), is("Password must be at least 6 characters"));
    }

}

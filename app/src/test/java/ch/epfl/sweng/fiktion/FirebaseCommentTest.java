package ch.epfl.sweng.fiktion;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.fiktion.models.Comment;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseComment;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by pedro on 29/11/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FirebaseCommentTest {
    FirebaseDatabaseProvider database;

    @Mock
    DatabaseReference dbRef;

    @Mock
    DataSnapshot snapshot;

    @Before
    public void initializers() {
        database = new FirebaseDatabaseProvider(dbRef, null, null);
        when(dbRef.child(anyString())).thenReturn(dbRef);
    }

    @Test
    public void addCommentTest() throws NoSuchAlgorithmException {
        ArgumentCaptor<ValueEventListener> vel = ArgumentCaptor.forClass(ValueEventListener.class);
        doNothing().when(dbRef).addListenerForSingleValueEvent(vel.capture());
        final Mutable<Boolean> success = new Mutable<>();
        DatabaseProvider.AddCommentListener listener = new DatabaseProvider.AddCommentListener() {
            @Override
            public void onSuccess() {
                success.set(true);
            }

            @Override
            public void onFailure() {
                success.set(false);
            }
        };

        database.addComment(new Comment("text", "author", new Date(0), 0), "poi", listener);
        when(dbRef.setValue(any(FirebaseComment.class))).thenReturn(null);
        vel.getValue().onDataChange(snapshot);
        assertTrue(success.get());
        vel.getValue().onCancelled(null);
        assertFalse(success.get());
    }

    @Test
    public void getCommentsTest() throws NoSuchAlgorithmException {
        ArgumentCaptor<ChildEventListener> cel = ArgumentCaptor.forClass(ChildEventListener.class);
        ArgumentCaptor<ValueEventListener> vel = ArgumentCaptor.forClass(ValueEventListener.class);
        when(dbRef.addChildEventListener(cel.capture())).thenReturn(null);
        when(dbRef.addValueEventListener(vel.capture())).thenReturn(null);
        final List<Comment> comments = new ArrayList<>();
        final Mutable<Boolean> isFailure = new Mutable<>(false);
        DatabaseProvider.GetCommentsListener listener = new DatabaseProvider.GetCommentsListener() {
            @Override
            public void onNewValue(Comment comment) {
                comments.add(comment);
            }

            @Override
            public void onModifiedValue(Comment comment) {

            }

            @Override
            public void onFailure() {
                isFailure.set(true);
            }
        };

        Comment c1 = new Comment("text", "author", new Date(42), 84);

        database.getPOIComments("poi", listener);
        when(snapshot.getValue(FirebaseComment.class)).thenReturn(new FirebaseComment(c1));
        when(snapshot.getKey()).thenReturn(" ");
        when(snapshot.exists()).thenReturn(true);
        cel.getValue().onChildAdded(snapshot, "");
        vel.getValue().onDataChange(snapshot);
        assertThat(comments.size(), is(1));
        assertFalse(isFailure.get());
        Comment rc = comments.get(0);
        assertThat(rc.getAuthorId(), is(c1.getAuthorId()));
        assertThat(rc.getDate().getTime(), is(c1.getDate().getTime()));
        assertThat(rc.getRating(), is(c1.getRating()));
        assertThat(rc.getText(), is(c1.getText()));

        cel.getValue().onChildAdded(snapshot, "");
        assertThat(comments.size(), is(1));
        assertFalse(isFailure.get());

        cel.getValue().onChildChanged(snapshot, "");
        cel.getValue().onChildRemoved(snapshot);
        cel.getValue().onChildMoved(snapshot, "");
        assertFalse(isFailure.get());
        cel.getValue().onCancelled(null);
        assertTrue(isFailure.get());

    }
}

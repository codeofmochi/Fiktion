package ch.epfl.sweng.fiktion;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.fiktion.models.posts.AddPOIPost;
import ch.epfl.sweng.fiktion.models.posts.CommentPOIPost;
import ch.epfl.sweng.fiktion.models.posts.FavoritePOIPost;
import ch.epfl.sweng.fiktion.models.posts.PhotoUploadPost;
import ch.epfl.sweng.fiktion.models.posts.Post;
import ch.epfl.sweng.fiktion.models.posts.PostType;
import ch.epfl.sweng.fiktion.models.posts.VisitPOIPost;
import ch.epfl.sweng.fiktion.models.posts.WishlistPOIPost;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.FirebaseDatabaseProvider;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseAddPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseCommentPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseFavoritePOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebasePhotoUploadPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseVisitPOIPost;
import ch.epfl.sweng.fiktion.providers.firebase_models.FirebaseWishlistPOIPost;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by painguin on 16.12.17.
 */

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDatabasePostTest {
    private FirebaseDatabaseProvider database;

    private final String poiName = "p0i";
    private final String userId = "uI%%d";
    private final AddPOIPost addPost = new AddPOIPost(poiName, new Date(0), "@dd");
    private final VisitPOIPost visitPost = new VisitPOIPost(poiName, new Date(1), "vis$it");
    private final CommentPOIPost commentPost = new CommentPOIPost(poiName, "com%mentId", new Date(2), "com#ment");
    private final FavoritePOIPost favoritePost = new FavoritePOIPost(poiName, new Date(3), "{favorite}");
    private final PhotoUploadPost photoPost = new PhotoUploadPost(poiName, "[photoId]", new Date(4), "/photo");
    private final WishlistPOIPost wishlistPost = new WishlistPOIPost(poiName, new Date(5), "wish/list");

    @Mock
    private DatabaseReference dbRef;

    @Before
    public void setDB() {
        database = new FirebaseDatabaseProvider(dbRef, null, null);
        when(dbRef.child(anyString())).thenReturn(dbRef);
    }

    @Test
    public void addUserPostTest() throws NoSuchAlgorithmException {
        final Mutable<Integer> postCount = new Mutable<>(0);
        final Mutable<Boolean> isFailure = new Mutable<>(false);

        DatabaseProvider.AddPostListener listener = new DatabaseProvider.AddPostListener() {
            @Override
            public void onFailure() {
                isFailure.set(true);
            }

            @Override
            public void onSuccess() {
                postCount.set(postCount.get() + 1);
            }
        };

        when(dbRef.setValue(ArgumentMatchers.any())).thenReturn(null);

        database.addUserPost(userId, addPost, listener);

        assertThat(postCount.get(), is(1));
        assertFalse(isFailure.get());

        database.addUserPost(userId, visitPost, listener);
        database.addUserPost(userId, commentPost, listener);

        assertThat(postCount.get(), is(3));
        assertFalse(isFailure.get());

        database.addUserPost(userId, favoritePost, listener);
        database.addUserPost(userId, photoPost, listener);
        database.addUserPost(userId, wishlistPost, listener);

        assertThat(postCount.get(), is(6));
        assertFalse(isFailure.get());

        AddPOIPost strangePost = mock(AddPOIPost.class);
        when(strangePost.getType()).thenReturn(PostType.COMMENT_POI);
        when(strangePost.getId()).thenReturn("strange");

        database.addUserPost(userId, strangePost, listener);
        assertThat(postCount.get(), is(6));
        assertTrue(isFailure.get());
        isFailure.set(false);

        database.addUserPost("", addPost, listener);
        assertTrue(isFailure.get());
        isFailure.set(false);

        database.addUserPost(userId, new AddPOIPost(poiName, new Date(7), ""), listener);
        assertTrue(isFailure.get());

    }

    @Test
    public void getUserPostsTest() {
        final List<Post> posts = new ArrayList<>();
        final Mutable<Boolean> isFailure = new Mutable<>(false);

        DatabaseProvider.GetPostListener listener = new DatabaseProvider.GetPostListener() {
            @Override
            public void onFailure() {
                isFailure.set(true);
            }

            @Override
            public void onNewValue(Post post) {
                posts.add(post);
            }
        };

        when(dbRef.orderByChild(anyString())).thenReturn(dbRef);
        ArgumentCaptor<ChildEventListener> cel = ArgumentCaptor.forClass(ChildEventListener.class);
        when(dbRef.addChildEventListener(cel.capture())).thenReturn(null);

        database.getUserPosts(userId, listener);

        DataSnapshot snapshot = mock(DataSnapshot.class);
        when(snapshot.child(anyString())).thenReturn(snapshot);
        when(snapshot.getValue(FirebaseAddPOIPost.class)).thenReturn(new FirebaseAddPOIPost(addPost));
        when(snapshot.getValue(FirebaseVisitPOIPost.class)).thenReturn(new FirebaseVisitPOIPost(visitPost));
        when(snapshot.getValue(FirebaseCommentPOIPost.class)).thenReturn(new FirebaseCommentPOIPost(commentPost));
        when(snapshot.getValue(FirebaseFavoritePOIPost.class)).thenReturn(new FirebaseFavoritePOIPost(favoritePost));
        when(snapshot.getValue(FirebasePhotoUploadPost.class)).thenReturn(new FirebasePhotoUploadPost(photoPost));
        when(snapshot.getValue(FirebaseWishlistPOIPost.class)).thenReturn(new FirebaseWishlistPOIPost(wishlistPost));

        when(snapshot.getValue(PostType.class)).thenReturn(PostType.ADD_POI);
        cel.getValue().onChildAdded(snapshot, "");
        when(snapshot.getValue(PostType.class)).thenReturn(PostType.VISIT_POI);
        cel.getValue().onChildAdded(snapshot, "");
        when(snapshot.getValue(PostType.class)).thenReturn(PostType.COMMENT_POI);
        cel.getValue().onChildAdded(snapshot, "");
        when(snapshot.getValue(PostType.class)).thenReturn(PostType.FAVORITE_POI);
        cel.getValue().onChildAdded(snapshot, "");
        when(snapshot.getValue(PostType.class)).thenReturn(PostType.PHOTO_UPLOAD);
        cel.getValue().onChildAdded(snapshot, "");
        when(snapshot.getValue(PostType.class)).thenReturn(PostType.WISHLIST_POI);
        cel.getValue().onChildAdded(snapshot, "");

        assertTrue(posts.contains(addPost));
        assertTrue(posts.contains(visitPost));
        assertTrue(posts.contains(commentPost));
        assertTrue(posts.contains(favoritePost));
        assertTrue(posts.contains(photoPost));
        assertTrue(posts.contains(wishlistPost));
        assertThat(posts.size(), is(6));

        when(snapshot.getValue(PostType.class)).thenReturn(null);
        cel.getValue().onChildAdded(snapshot, "");
        assertThat(posts.size(), is(6));

        when(snapshot.getValue(PostType.class)).thenReturn(PostType.ADD_POI);
        when(snapshot.getValue(FirebaseAddPOIPost.class)).thenReturn(null);
        cel.getValue().onChildAdded(snapshot, "");
        assertThat(posts.size(), is(6));

        assertFalse(isFailure.get());

        cel.getValue().onChildChanged(snapshot, "");
        cel.getValue().onChildRemoved(snapshot);
        cel.getValue().onChildMoved(snapshot, "");

        assertThat(posts.size(), is(6));
        assertFalse(isFailure.get());

        cel.getValue().onCancelled(null);
        assertTrue(isFailure.get());
        assertThat(posts.size(), is(6));
        isFailure.set(false);

        database.getUserPosts("", listener);
        assertTrue(isFailure.get());
        assertThat(posts.size(), is(6));
    }
}

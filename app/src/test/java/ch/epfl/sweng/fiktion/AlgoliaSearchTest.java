package ch.epfl.sweng.fiktion;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.models.PointOfInterest;
import ch.epfl.sweng.fiktion.models.Position;
import ch.epfl.sweng.fiktion.providers.AlgoliaSearchProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider.AddPoiListener;
import ch.epfl.sweng.fiktion.providers.SearchProvider.SearchPOIsByTextListener;
import ch.epfl.sweng.fiktion.utils.Mutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Created by serdar on 11/16/2017.
 * Tests searching functionality of Algolia module
 */

@RunWith(MockitoJUnitRunner.class)
public class AlgoliaSearchTest {

    private PointOfInterest poi = new PointOfInterest(
            "poiName",
            new Position(3, 4),
            new TreeSet<String>(),
            "random description",
            4,
            "TestLand",
            "TestTown");

    @Mock
    Index index;

    private AlgoliaSearchProvider algolia;

    @Before
    public void setup() {
        algolia = new AlgoliaSearchProvider(index);
    }

    @Test
    public void addPOITest() {
        final Mutable<CompletionHandler> handler = new Mutable<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                handler.value = (CompletionHandler) invocation.getArgument(1);
                return null;
            }
        }).when(index).addObjectAsync(any(JSONObject.class), any(CompletionHandler.class));

        final Mutable<String> result = new Mutable<>("NOTHING");

        AddPoiListener listener = new AddPoiListener() {
            @Override
            public void onSuccess() {
                result.value = "SUCCESS";
            }

            @Override
            public void onAlreadyExists() {
                result.value = "ALREADYEXISTS";
            }

            @Override
            public void onFailure() {
                result.value = "FAILURE";
            }
        };

        algolia.addPoi(poi, listener);
        handler.value.requestCompleted(null, null);
        assertThat(result.value, is("SUCCESS"));
        PointOfInterest poiFail = new PointOfInterest("poiFail", null, new TreeSet<String>(), "desc", 0, "country", "city");
        algolia.addPoi(poiFail, listener);
        assertThat(result.value, is("FAILURE"));
    }

    @Test
    public void searchByTextTest() throws JSONException {
        final Mutable<CompletionHandler> handler = new Mutable<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                handler.value = (CompletionHandler) invocation.getArgument(1);
                return null;
            }
        }).when(index).searchAsync(any(Query.class), any(CompletionHandler.class));

        final List<String> poiNames = new ArrayList<>();
        final Mutable<Boolean> failure = new Mutable<>(false);

        SearchPOIsByTextListener listener = new SearchPOIsByTextListener() {

            @Override
            public void onSuccess(List<String> poiIDs) {
                for (String id: poiIDs)
                    poiNames.add(id);
            }

            @Override
            public void onFailure() {
                failure.value = true;
            }
        };

        algolia.searchByText("", listener);

        Collection<JSONObject> hits = new ArrayList<>();
        hits.add(new JSONObject().put("name", "poi1"));
        hits.add(new JSONObject().put("name", "poi2"));
        JSONObject json = new JSONObject().put("hits", new JSONArray(hits));

        handler.value.requestCompleted(json, null);
        assertThat(poiNames.size(), is(2));
        assertThat(failure.value, is(false));
        poiNames.clear();

        JSONObject emptyJSON = new JSONObject()
                .put("hits", new JSONArray(new ArrayList<JSONObject>()));

        handler.value.requestCompleted(emptyJSON, null);
        assertThat(poiNames.size(), is(0));
        assertThat(failure.value, is(false));
        poiNames.clear();

        JSONObject nohitJSON = new JSONObject()
                .put("notHits", 42);

        handler.value.requestCompleted(nohitJSON, null);
        assertThat(poiNames.size(), is(0));
        poiNames.clear();

        Collection<JSONObject> weirdHits = new ArrayList<>();
        weirdHits.add(null);
        weirdHits.add(new JSONObject().put("name", "poi1"));
        weirdHits.add(new JSONObject().put("nameee", "poi2"));
        weirdHits.add(new JSONObject().put("name", "poi3"));
        JSONObject weirdJSON = new JSONObject().put("hits", new JSONArray(weirdHits));

        handler.value.requestCompleted(weirdJSON, null);
        assertThat(poiNames.size(), is(2));
        assertThat(failure.value, is(false));
        poiNames.clear();

        AlgoliaException e = mock(AlgoliaException.class);

        handler.value.requestCompleted(json, e);
        assertThat(poiNames.size(), is(0));
        assertThat(failure.value, is(true));

    }

}

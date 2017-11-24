package ch.epfl.sweng.fiktion;

import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

import ch.epfl.sweng.fiktion.utils.CollectionsUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests for CollectionUtils
 * <p>
 * Created by dialexo on 24.11.17.
 */

public class CollectionUtilsTest {

    @Test
    public void testMkString() {
        Set<String> s = new TreeSet<>();
        assertThat(CollectionsUtils.mkString(s, "x"), is(""));
        s.add("a");
        s.add("b");
        s.add("c");
        assertThat(CollectionsUtils.mkString(s, "x"), is("axbxc"));
        assertThat(CollectionsUtils.mkString(s, ", "), is("a, b, c"));
    }
}

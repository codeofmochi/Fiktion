package ch.epfl.sweng.bootcamp;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertThat(2 + 2, is(4));
    }
}

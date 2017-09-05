package com.wahyudieko.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.wahyudieko.bakingapp.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by EKO on 05/09/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityRecyclerViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testClickRecyclerView(){
        // Click on the RecyclerView item at position 3
        onView(withId(R.id.recipe_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

    }

    @Test
    public void testSomeContentRecyclerView(){
        // Check item at position 1 has "Nutella Pie"
        onView(withRecyclerView(R.id.recipe_recycler_view).atPosition(0))
                .check(matches(hasDescendant(withText("Nutella Pie"))));

        // Click item at position 1
        onView(withRecyclerView(R.id.recipe_recycler_view).atPosition(0)).perform(click());
    }

    @Test
    public void testItemClick(){

        // Click item at position 2
        onView(withRecyclerView(R.id.recipe_recycler_view).atPosition(1)).perform(click());

        // Check the result, ingredients text view should be displayed
        onView(withId(R.id.ingredients_textview)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecipeNameItemClick(){
        // Click recycler view item at position 2
        onView(withId(R.id.recipe_recycler_view)).perform(TestUtils.actionOnItemViewAtPosition(1,
                R.id.recipe_name_tv,
                click()));

        // Check the result, step recycler view item at position 2 should contain a "Starting prep" step
        String followingText = "Starting prep";
        onView(withRecyclerView(R.id.recipe_step_recycler_view).atPositionOnView(1, R.id.recipe_step_name_tv)).check(matches(withText(followingText)));
    }

    @Test
    public void testRecipeStepItemClick_IsVideoDisplayed(){
        // Click recipe recycler view item at position 2
        onView(withId(R.id.recipe_recycler_view)).perform(TestUtils.actionOnItemViewAtPosition(1,
                R.id.recipe_name_tv,
                click()));

        // Click recipe step recycler view item at position 1
        onView(withId(R.id.recipe_step_recycler_view)).perform(TestUtils.actionOnItemViewAtPosition(0,
                R.id.recipe_step_name_tv,
                click()));

        // Check the result, an exo video view should be displayed in the fragment index 0
        onView(withIndex(withId(R.id.exo_player_view), 0)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecipeStepItemClick_IsVideoNotDisplayed(){
        // Click recipe recycler view item at position 2
        onView(withId(R.id.recipe_recycler_view)).perform(TestUtils.actionOnItemViewAtPosition(1,
                R.id.recipe_name_tv,
                click()));

        // Click recipe step recycler view item at position 1
        onView(withId(R.id.recipe_step_recycler_view)).perform(TestUtils.actionOnItemViewAtPosition(0,
                R.id.recipe_step_name_tv,
                click()));

        // Check the result, an exo video view should not be displayed in the fragment index 1
        onView(withIndex(withId(R.id.exo_player_view), 1)).check(matches(not(isDisplayed())));
    }

    // Convenience helper
    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    // View pager helper multiple ids
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}

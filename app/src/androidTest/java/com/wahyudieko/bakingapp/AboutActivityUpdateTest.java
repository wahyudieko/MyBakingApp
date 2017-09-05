package com.wahyudieko.bakingapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wahyudieko.bakingapp.ui.AboutActivity;
import com.wahyudieko.bakingapp.utils.Utility;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by EKO on 05/09/2017.
 */

@RunWith(AndroidJUnit4.class)
public class AboutActivityUpdateTest {
    @Rule public ActivityTestRule<AboutActivity> mActivityTestRule =
            new ActivityTestRule<>(AboutActivity.class);

    @Test
    public void clickUpdateButton_SaveLastUpdateDate(){
        // 1. find the view
        // 2. perform action on the view
        onView((withId(R.id.update_recipe_button))).perform(click());

        // 3. check if the view does what you expected

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDate = sdf.format(new Date());
        String lastUpdated = "Last update: "+ Utility.simpleDateFormat(currentDate);

        onView(withId(R.id.last_update_textview)).check(matches(withText(lastUpdated)));

    }
}

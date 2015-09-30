/*
 * Copyright (C) 2012-2015 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.moneymanagerex.android.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.money.manager.ex.home.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.moneymanagerex.android.testhelpers.UiTestHelpersRobotium;

/**
 * Robotium tests for the Main Activity.
 * Runs with JUnit 3.
 * The tests start with a fresh copy of preferences.
 *
 * Created by Alen Siljak on 24/09/2015.
 */
public class NewMainActivityTests
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private MainActivity testObject;
    private UiTestHelpersRobotium helper;

    public NewMainActivityTests() {
        super(MainActivity.class);

    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        this.helper = new UiTestHelpersRobotium(solo);
        helper.clearPreferences(getInstrumentation().getTargetContext());

        solo = new Solo(getInstrumentation(), getActivity());

        this.testObject = getActivity();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        if (solo != null) {
            solo.finishOpenedActivities();
        }
    }

    @Ignore
    public void welcomeScreen() {
        assertTrue(solo.waitForText("Welcome to MoneyManagerEx", 1, 1000));
    }

    @Ignore
    public void changelog() {
        assertTrue(solo.waitForActivity(MainActivity.class, 2000));

        assertTrue(solo.waitForDialogToOpen());

        assertTrue(solo.waitForText("Changelog"));

        assertTrue(solo.searchText("OK", true));
        solo.clickOnText("OK", 1, true);
    }

    @Ignore
    public void tutorial() {
        solo.assertCurrentActivity("wrong activity", MainActivity.class);

        assertTrue(solo.waitForActivity(MainActivity.class, 2000));

//        assertTrue("tutorial activity should open", solo.waitForActivity(TutorialActivity.class, 2000));

//        System.out.println("waiting for text Accounts");
        assertTrue(solo.waitForText("Accounts", 1, 1000, false, true));
        assertTrue("can't find text Close", solo.waitForText("Close", 1, 1000, false, true));

    }

    /**
     * This is the full test for the new installation of the app. Runs all the partial tests.
     */
    @Test
    public void testAll() {
        // First the tutorial should be shown.
        tutorial();
        solo.clickOnText("Close", 1, false);

        changelog();

        welcomeScreen();
    }

}

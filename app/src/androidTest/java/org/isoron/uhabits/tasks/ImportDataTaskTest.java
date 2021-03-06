/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.tasks;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.isoron.uhabits.BaseAndroidTest;
import org.isoron.uhabits.utils.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ImportDataTaskTest extends BaseAndroidTest
{
    private File baseDir;

    @Before
    public void setUp()
    {
        super.setUp();

        baseDir = FileUtils.getFilesDir("Backups");
        if(baseDir == null) fail("baseDir should not be null");
    }

    private void copyAssetToFile(String assetPath, File dst) throws IOException
    {
        InputStream in = testContext.getAssets().open(assetPath);
        FileUtils.copy(in, dst);
    }

    private void assertTaskResult(final int expectedResult, String assetFilename) throws Throwable
    {
        ImportDataTask task = createTask(assetFilename);

        task.setListener(new ImportDataTask.Listener()
        {
            @Override
            public void onImportDataFinished(int result)
            {
                assertThat(result, equalTo(expectedResult));
            }
        });

        task.execute();
        waitForAsyncTasks();
    }

    @NonNull
    private ImportDataTask createTask(String assetFilename) throws IOException
    {
        File file = new File(String.format("%s/%s", baseDir.getPath(), assetFilename));
        copyAssetToFile(assetFilename, file);
        return new ImportDataTask(habitList, file, null);
    }

    @Test
    public void testImportInvalidData() throws Throwable
    {
        assertTaskResult(ImportDataTask.NOT_RECOGNIZED, "icon.png");
    }

    @Test
    public void testImportValidData() throws Throwable
    {
        assertTaskResult(ImportDataTask.SUCCESS, "loop.db");
    }
}

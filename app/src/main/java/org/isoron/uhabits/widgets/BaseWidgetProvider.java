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

package org.isoron.uhabits.widgets;

import android.appwidget.*;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.widget.*;

import org.isoron.uhabits.*;
import org.isoron.uhabits.models.*;
import org.isoron.uhabits.ui.widgets.*;
import org.isoron.uhabits.utils.*;

import javax.inject.*;

import static android.os.Build.VERSION.*;
import static android.os.Build.VERSION_CODES.*;
import static org.isoron.uhabits.utils.WidgetUtils.*;

public abstract class BaseWidgetProvider extends AppWidgetProvider
{
    @Inject
    HabitList habits;

    @Inject
    WidgetPreferences widgetPrefs;

    public BaseWidgetProvider()
    {
        HabitsApplication.getComponent().inject(this);
    }

    @Override
    public void onAppWidgetOptionsChanged(@Nullable Context context,
                                          @Nullable AppWidgetManager manager,
                                          int widgetId,
                                          @Nullable Bundle options)
    {
        try
        {
            if (context == null) throw new RuntimeException("context is null");
            if (manager == null) throw new RuntimeException("manager is null");
            if (options == null) throw new RuntimeException("options is null");
            context.setTheme(R.style.TransparentWidgetTheme);

            BaseWidget widget = getWidgetFromId(context, widgetId);
            WidgetDimensions dims = getDimensionsFromOptions(context, options);
            widget.setDimensions(dims);
            updateAppWidget(manager, widget);
        }
        catch (RuntimeException e)
        {
            drawErrorWidget(context, manager, widgetId);
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleted(@Nullable Context context, @Nullable int[] ids)
    {
        if (context == null) throw new RuntimeException("context is null");
        if (ids == null) throw new RuntimeException("ids is null");

        for (int id : ids)
        {
            BaseWidget widget = getWidgetFromId(context, id);
            widget.delete();
        }
    }

    @Override
    public void onUpdate(@Nullable Context context,
                         @Nullable AppWidgetManager manager,
                         @Nullable int[] widgetIds)
    {
        if (context == null) throw new RuntimeException("context is null");
        if (manager == null) throw new RuntimeException("manager is null");
        if (widgetIds == null) throw new RuntimeException("widgetIds is null");
        context.setTheme(R.style.TransparentWidgetTheme);

        for (int id : widgetIds)
            update(context, manager, id);
    }

    @NonNull
    protected Habit getHabitFromWidgetId(int widgetId)
    {
        long habitId = widgetPrefs.getHabitIdFromWidgetId(widgetId);
        Habit habit = habits.getById(habitId);
        if (habit == null) throw new RuntimeException("habit not found");
        return habit;
    }

    @NonNull
    protected abstract BaseWidget getWidgetFromId(@NonNull Context context,
                                                  int id);

    private void drawErrorWidget(Context context,
                                 AppWidgetManager manager,
                                 int widgetId)
    {
        RemoteViews errorView =
            new RemoteViews(context.getPackageName(), R.layout.widget_error);
        manager.updateAppWidget(widgetId, errorView);
    }

    private void update(@NonNull Context context,
                        @NonNull AppWidgetManager manager,
                        int widgetId)
    {
        try
        {
            BaseWidget widget = getWidgetFromId(context, widgetId);

            if (SDK_INT > JELLY_BEAN)
            {
                Bundle options = manager.getAppWidgetOptions(widgetId);
                widget.setDimensions(
                    getDimensionsFromOptions(context, options));
            }

            updateAppWidget(manager, widget);
        }
        catch (RuntimeException e)
        {
            drawErrorWidget(context, manager, widgetId);
            e.printStackTrace();
        }
    }
}

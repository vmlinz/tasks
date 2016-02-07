package org.tasks.fragments;

import android.app.Activity;
import android.app.FragmentManager;

import com.todoroo.astrid.activity.BeastModePreferences;
import com.todoroo.astrid.data.Task;
import com.todoroo.astrid.files.FilesControlSet;
import com.todoroo.astrid.repeats.RepeatControlSet;
import com.todoroo.astrid.tags.TagsControlSet;
import com.todoroo.astrid.timers.TimerControlSet;
import com.todoroo.astrid.ui.EditTitleControlSet;
import com.todoroo.astrid.ui.HideUntilControlSet;
import com.todoroo.astrid.ui.ReminderControlSet;

import org.tasks.BuildConfig;
import org.tasks.R;
import org.tasks.preferences.Preferences;
import org.tasks.ui.CalendarControlSet;
import org.tasks.ui.DeadlineControlSet;
import org.tasks.ui.DescriptionControlSet;
import org.tasks.ui.PriorityControlSet;
import org.tasks.ui.TaskEditControlFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class TaskEditControlSetFragmentManager {

    public static final int[] TASK_EDIT_CONTROL_FRAGMENT_ROWS = new int[] {
            R.id.row_1,
            R.id.row_2,
            R.id.row_3,
            R.id.row_4,
            R.id.row_5,
            R.id.row_6,
            R.id.row_7,
            R.id.row_8,
            R.id.row_9,
            R.id.row_10,
            R.id.row_11,
    };

    private static final int[] TASK_EDIT_CONTROL_SET_FRAGMENTS = new int[] {
            EditTitleControlSet.TAG,
            DeadlineControlSet.TAG,
            CalendarControlSet.TAG,
            PriorityControlSet.TAG,
            DescriptionControlSet.TAG,
            HideUntilControlSet.TAG,
            ReminderControlSet.TAG,
            FilesControlSet.TAG,
            TimerControlSet.TAG,
            TagsControlSet.TAG,
            RepeatControlSet.TAG
    };

    static {
        if (BuildConfig.DEBUG && TASK_EDIT_CONTROL_FRAGMENT_ROWS.length != TASK_EDIT_CONTROL_SET_FRAGMENTS.length) {
            throw new AssertionError();
        }
    }

    private final Map<String, Integer> controlSetFragments = new LinkedHashMap<>();
    private final Activity activity;
    private final Preferences preferences;
    private final String hideAlwaysTrigger;
    private final FragmentManager fragmentManager;

    @Inject
    public TaskEditControlSetFragmentManager(Activity activity, Preferences preferences) {
        this.activity = activity;
        this.preferences = preferences;

        fragmentManager = activity.getFragmentManager();
        hideAlwaysTrigger = activity.getString(R.string.TEA_ctrl_hide_section_pref);

        for (int resId : TASK_EDIT_CONTROL_SET_FRAGMENTS) {
            controlSetFragments.put(activity.getString(resId), resId);
        }
    }

    public List<TaskEditControlFragment> createNewFragments(boolean isNewTask, Task task) {
        List<TaskEditControlFragment> taskEditControlFragments = new ArrayList<>();
        List<String> controlOrder = BeastModePreferences.constructOrderedControlList(preferences, activity);
        controlOrder.add(0, activity.getString(EditTitleControlSet.TAG));

        for (int i = 0; i < controlOrder.size(); i++) {
            String item = controlOrder.get(i);
            if (item.equals(hideAlwaysTrigger)) {
                break;
            }
            Integer resId = controlSetFragments.get(item);
            if (resId == null) {
                Timber.e("Unknown task edit control %s", item);
                continue;
            }

            TaskEditControlFragment fragment = createFragment(resId);
            fragment.initialize(isNewTask, task);
            taskEditControlFragments.add(fragment);
        }
        return taskEditControlFragments;
    }

    public List<TaskEditControlFragment> getFragments() {
        List<TaskEditControlFragment> fragments = new ArrayList<>();
        for (String tag : controlSetFragments.keySet()) {
            TaskEditControlFragment fragment = (TaskEditControlFragment) fragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                fragments.add(fragment);
            }
        }
        return fragments;
    }

    private TaskEditControlFragment createFragment(int fragmentId) {
        switch (fragmentId) {
            case EditTitleControlSet.TAG:
                return new EditTitleControlSet();
            case DeadlineControlSet.TAG:
                return new DeadlineControlSet();
            case PriorityControlSet.TAG:
                return new PriorityControlSet();
            case DescriptionControlSet.TAG:
                return new DescriptionControlSet();
            case CalendarControlSet.TAG:
                return new CalendarControlSet();
            case HideUntilControlSet.TAG:
                return new HideUntilControlSet();
            case ReminderControlSet.TAG:
                return new ReminderControlSet();
            case FilesControlSet.TAG:
                return new FilesControlSet();
            case TimerControlSet.TAG:
                return new TimerControlSet();
            case TagsControlSet.TAG:
                return new TagsControlSet();
            case RepeatControlSet.TAG:
                return new RepeatControlSet();
            default:
                throw new RuntimeException("Unsupported fragment");
        }
    }
}
package timetracker.com;

import java.util.ArrayList;

/**
 * Callback interface for getting the load from database finish events
 */

public interface OnLoadFromDBCompletedListener {
    void onLoadCompleted(ArrayList<WorkEntry> hoursList);
}

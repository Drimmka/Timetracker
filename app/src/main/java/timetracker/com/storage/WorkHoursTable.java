package timetracker.com.storage;

/**
 * Table representing the work hours
 */
public class WorkHoursTable {
	public static final String TAG = "WorkHoursTable";
	public static final String TABLE_WORK_HOURS = "WorkHours";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_CHECK_IN = "checkin";
	public static final String COLUMN_CHECK_OUT = "checkout";
	public static final String COLUMN_TOTAL = "start_index";

	//table create statement
	public static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_WORK_HOURS
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CHECK_IN + " BIGINT, "
			+ COLUMN_CHECK_OUT + " BIGINT, "
			+ COLUMN_TOTAL + " BIGINT"
			+ ");";
}

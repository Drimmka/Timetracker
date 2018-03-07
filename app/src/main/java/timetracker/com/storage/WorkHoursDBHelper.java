package timetracker.com.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import timetracker.com.OnLoadFromDBCompletedListener;
import timetracker.com.WorkEntry;

/**
 * Class for managing work entries database
 */
public class WorkHoursDBHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "workhours.db";
	private static final String SELECTION_LATEST = "SELECT * FROM " + WorkHoursTable.TABLE_WORK_HOURS
													+ " ORDER BY " + WorkHoursTable.COLUMN_ID + " DESC LIMIT 1;";
	private static final String SELECTION_ALL = "SELECT * FROM " + WorkHoursTable.TABLE_WORK_HOURS
													+ " ORDER BY " + WorkHoursTable.COLUMN_ID + ";";

	private static final String SELECTION_BY_ID = WorkHoursTable.COLUMN_ID + " = ?";


	public WorkHoursDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(WorkHoursTable.DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}

	/**
	 * fetch the last work record
	 * @return
	 */
	public WorkEntry getLatestRecord(){
		WorkEntry workEntry = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(SELECTION_LATEST, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			int id = cursor.getInt(cursor.getColumnIndex(WorkHoursTable.COLUMN_ID));
			long checkin = cursor.getLong(cursor.getColumnIndex(WorkHoursTable.COLUMN_CHECK_IN));
			long checkout = cursor.getLong(cursor.getColumnIndex(WorkHoursTable.COLUMN_CHECK_OUT));
			long total = cursor.getLong(cursor.getColumnIndex(WorkHoursTable.COLUMN_TOTAL));
			workEntry = new WorkEntry(checkin, checkout, total, id);
		}
		cursor.close();
		close();
		return workEntry;
	}

	/**
	 * get all work entries from the db
	 * since this is a time consuming action this is doen is a separate thread
	 * with callback to initiator
	 * @param listener
	 */
	public void getAllEntries(final OnLoadFromDBCompletedListener listener){
		new Thread(new Runnable() {
			public void run() {
				listener.onLoadCompleted(getAllEntries());
			}
		}).start();
	}

	/**
	 * get all work entries from the db
	 * @return
	 */
	private ArrayList<WorkEntry> getAllEntries(){
		ArrayList<WorkEntry> workEntries = new ArrayList<WorkEntry>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(SELECTION_ALL, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			while (!cursor.isAfterLast()){
				int id = cursor.getInt(cursor.getColumnIndex(WorkHoursTable.COLUMN_ID));
				long checkin = cursor.getLong(cursor.getColumnIndex(WorkHoursTable.COLUMN_CHECK_IN));
				long checkout = cursor.getLong(cursor.getColumnIndex(WorkHoursTable.COLUMN_CHECK_OUT));
				long total = cursor.getLong(cursor.getColumnIndex(WorkHoursTable.COLUMN_TOTAL));
				WorkEntry workEntry = new WorkEntry(checkin, checkout, total, id);
				workEntries.add(workEntry);
				cursor.moveToNext();
			}

		}
		cursor.close();
		close();
		return workEntries;
	}

	/**
	 * add checkin event to database
	 * @param timeStamp
	 */
	public  void checkIn(long timeStamp){
		WorkEntry latestEntry = getLatestRecord();

		// still in the office
		if (latestEntry != null && latestEntry.getCheckoutTime() == 0) {
			return;
		}

		SQLiteDatabase db = getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put(WorkHoursTable.COLUMN_CHECK_IN, timeStamp);
		db.insert(WorkHoursTable.TABLE_WORK_HOURS, null, values);
	}

	/**
	 * add checkout event to the database
	 * @param timeStamp
	 */
	public  void checkOut(long timeStamp){
		WorkEntry latestEntry = getLatestRecord();
		if (latestEntry == null){
			return;
		}
		if (latestEntry.getCheckoutTime() == 0){
			latestEntry.setCheckoutTime(timeStamp);
		}
		ContentValues values = new ContentValues();
		values.put(WorkHoursTable.COLUMN_CHECK_IN, latestEntry.getCheckinTime());
		values.put(WorkHoursTable.COLUMN_CHECK_OUT, latestEntry.getCheckoutTime());
		values.put(WorkHoursTable.COLUMN_TOTAL, latestEntry.getCheckoutTime() - latestEntry.getCheckinTime());

		String[] selectionArgs = {latestEntry.getId() + ""};
		SQLiteDatabase db = getReadableDatabase();
		db.delete(WorkHoursTable.TABLE_WORK_HOURS, SELECTION_BY_ID, selectionArgs);
		db.insert(WorkHoursTable.TABLE_WORK_HOURS, null, values);
	}

}


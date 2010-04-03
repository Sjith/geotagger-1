package fede.geotagger;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class GeoDbAdapter {
  private static final String DATABASE_NAME = "geoDb.db";
  
  private static final int DATABASE_VERSION = 1;
 
  
  
  
  //Picture Range
  	private static final String RANGE_TABLE = "PictureRange";
  	public static final String START_RANGE_KEY = "StartRange";
	public static final int START_RANGE_COLUMN = 1;
	public static final String END_RANGE_KEY = "EndRange";
	public static final int END_RANGE_COLUMN = 2;
	public static final String POSITION_ID_KEY = "PositionId";
	public static final int POSITION_ID_COLUMN = 3;
	
	
	public static final String ROW_ID = "_id";
	
	
  // The index (key) column name for use in where clauses.
  
  // Position Object
    private static final String POSITION_TABLE = "Position";
	public static final String POSITION_NAME_KEY = "PositionName";
	public static final int POSITION_NAME_COLUMN = 1;
	public static final String POSITION_LATITUDE_KEY = "Latitude";
	public static final int POSITION_LATITUDE_COLUMN = 2;	
	public static final String POSITION_LONGITUDE_KEY = "Longitude";
	public static final int POSITION_LONGITUDE_COLUMN = 3;
	public static final String POSITION_ALTITUDE_KEY = "Altitude";
	public static final int POSITION_ALTITUDE_COLUMN = 4;
	public static final String POSITION_ROW_ID = "_id";	
	
  // TODO: Create public field for each column in your table.
  
  // SQL Statement to create a new database.
  private static final String DATABASE_RANGE_CREATE = "create table " + 
  RANGE_TABLE + " (" + ROW_ID + 
    " integer primary key autoincrement, " +
    START_RANGE_KEY + " number, " + 
    END_RANGE_KEY + " number, " +
    POSITION_ID_KEY + " number);";
    
  private static final String DATABASE_POSITION_CREATE = "create table " + 
  POSITION_TABLE + " (" + ROW_ID + 
    " integer primary key autoincrement, " +
    POSITION_NAME_KEY + " string, " + 
    POSITION_LATITUDE_KEY + " string, " +
    POSITION_LONGITUDE_KEY + " string, " +
    POSITION_ALTITUDE_KEY + " string);";
    			
    			
  // Variable to hold the database instance
  private SQLiteDatabase db;
  // Context of the application using the database.
  private final Context context;
  // Database open/upgrade helper
  private myDbHelper dbHelper;

  public GeoDbAdapter(Context _context) {
    context = _context;
    dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public GeoDbAdapter open() throws SQLException {
    db = dbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    db.close();
  }

  // POSITION
  
  public long addPosition(String positionName, String latitude, String longitude, String altitude)
  {
	    ContentValues contentValues = new ContentValues();
  	    contentValues.put(POSITION_LATITUDE_KEY, latitude);
  	    contentValues.put(POSITION_LONGITUDE_KEY, longitude);
  	  	contentValues.put(POSITION_ALTITUDE_KEY, altitude);
  		contentValues.put(POSITION_NAME_KEY, positionName);
  	    
	   return db.insert(POSITION_TABLE, null, contentValues);
  }
  

  public boolean removePosition(long _rowIndex) {
    return db.delete(POSITION_TABLE, ROW_ID + "=" + _rowIndex, null) > 0;
  }

   
  
  public Cursor getAllPositions () {
    return db.query(POSITION_TABLE, new String[] {POSITION_ROW_ID, 
    											  POSITION_NAME_KEY, 
    											  POSITION_LATITUDE_KEY,
    											  POSITION_LONGITUDE_KEY,
    											  POSITION_ALTITUDE_KEY}, 
                    null, null, null, null, null);
  }


  
    
  public Cursor getPosition(long _rowIndex) {
    
    Cursor res = db.query(POSITION_TABLE, new String[] {POSITION_ROW_ID, 
    												 POSITION_NAME_KEY, 
													 POSITION_LATITUDE_KEY,
													 POSITION_LONGITUDE_KEY,
													 POSITION_ALTITUDE_KEY}, ROW_ID + " = " + _rowIndex, 
    		null, null, null, null);
    
    if(res != null){
    	res.moveToFirst();
    }
    return res;
  }
  
  	  
  
  
  public int updatePosition(long _rowIndex, String positionName, String latitude, String longitude, String altitude) 
  {
    String where = POSITION_ROW_ID + " = " + _rowIndex;
    ContentValues contentValues = new ContentValues();
    contentValues.put(POSITION_LATITUDE_KEY, latitude);
    contentValues.put(POSITION_LONGITUDE_KEY, longitude);
  	contentValues.put(POSITION_ALTITUDE_KEY, altitude);
	contentValues.put(POSITION_NAME_KEY, positionName);

   // TODO Fill in the ContentValue based on the new object
    return db.update(POSITION_TABLE, contentValues, where, null);
  }
  
  
  // RANGE
  

  public long addRange(int startRange, int stopRange, int positionId)
  {
	    ContentValues contentValues = new ContentValues();
  	    contentValues.put(START_RANGE_KEY, startRange);
  	    contentValues.put(END_RANGE_KEY, stopRange);
	    contentValues.put(POSITION_ID_KEY, positionId);
	   return db.insert(RANGE_TABLE, null, contentValues);
  }
  

  public boolean removeRange(long _rowIndex) {
    return db.delete(RANGE_TABLE, ROW_ID + "=" + _rowIndex, null) > 0;
  }

   
  
  public Cursor getAllRanges () {
    return db.query(RANGE_TABLE, new String[] {ROW_ID, START_RANGE_KEY, END_RANGE_KEY, POSITION_ID_KEY}, 
                    null, null, null, null, null);
  }


  
    
  public Cursor getRange(long _rowIndex) {
    
    Cursor res = db.query(RANGE_TABLE, new String[] {ROW_ID, START_RANGE_KEY, END_RANGE_KEY, POSITION_ID_KEY}, ROW_ID + " = " + _rowIndex, 
    		null, null, null, null);
    
    if(res != null){
    	res.moveToFirst();
    }
    return res;
  }
  
  	  
  
  
  public int updateRange(long _rowIndex, int fromRange, int toRange, int positionId) {
    String where = ROW_ID + " = " + _rowIndex;
    ContentValues contentValues = new ContentValues();
    contentValues.put(START_RANGE_KEY, fromRange);
    contentValues.put(END_RANGE_KEY, toRange);
    contentValues.put(POSITION_ID_KEY, positionId);
    
   // TODO Fill in the ContentValue based on the new object
    return db.update(RANGE_TABLE, contentValues, where, null);
  }
  
  

  private static class myDbHelper extends SQLiteOpenHelper {

    public myDbHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one. 
    @Override
    public void onCreate(SQLiteDatabase _db) {
      _db.execSQL(DATABASE_RANGE_CREATE);
      _db.execSQL(DATABASE_POSITION_CREATE);
    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " + 
                             _oldVersion + " to " +
                             _newVersion + ", which will destroy all old data");
        
      // Upgrade the existing database to conform to the new version. Multiple 
      // previous versions can be handled by comparing _oldVersion and _newVersion
      // values.

      // The simplest case is to drop the old table and create a new one.
      _db.execSQL("DROP TABLE IF EXISTS " + RANGE_TABLE + ";");
      _db.execSQL("DROP TABLE IF EXISTS " + POSITION_TABLE + ";");
      // Create a new one.
      onCreate(_db);
    }
  }
 
  /** Dummy object to allow class to compile */

}


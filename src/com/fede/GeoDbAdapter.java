package com.fede;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class GeoDbAdapter {
  private static final String DATABASE_NAME = "geoDb.db";
  
  private static final int DATABASE_VERSION = 5;
  private boolean mOpen;
 
  
  
  
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
	public static final String POSITION_DATE_KEY = "Date";
	public static final int POSITION_DATE_COLUMN = 5;
	public static final String POSITION_ROW_ID = "_id";	
	
  // TODO: Create public field for each column in your table.
  
  // SQL Statement to create a new database.
  private static final String DATABASE_RANGE_CREATE = "create table " + 
  RANGE_TABLE + " (" + ROW_ID + 
    " integer primary key autoincrement, " +
    START_RANGE_KEY + " number, " + 
    END_RANGE_KEY + " number, " +
    POSITION_ID_KEY + " integer, " +
    "foreign key(" + POSITION_ID_KEY + ") references " + POSITION_TABLE + "(" + POSITION_ROW_ID + "));";
  
  private static final String CREATE_INDEX_RANGE_START = "create unique index idx_from_range on " + 
  RANGE_TABLE + " (" + START_RANGE_KEY + ")";
  
  private static final String CREATE_INDEX_RANGE_END = "create unique index idx_end_range on " + 
  RANGE_TABLE + " (" + END_RANGE_KEY + ")";
  
  private static final String DATABASE_POSITION_CREATE = "create table " + 
  POSITION_TABLE + " (" + ROW_ID + 
    " integer primary key autoincrement, " +
    POSITION_NAME_KEY + " string, " + 
    POSITION_LATITUDE_KEY + " string, " +
    POSITION_LONGITUDE_KEY + " string, " +
    POSITION_ALTITUDE_KEY + " string, " + 
    POSITION_DATE_KEY + " integer);";
    			
    			
  // Variable to hold the database instance
  private SQLiteDatabase db;
  // Context of the application using the database.
  private final Context context;
  // Database open/upgrade helper
  private myDbHelper dbHelper;

  public GeoDbAdapter(Context _context) {
    context = _context;
    dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    mOpen = false;
  }

  public GeoDbAdapter open() throws SQLException {
	if(mOpen == false){		// this because I want to call this in onresume and onactivityresult methods
		db = dbHelper.getWritableDatabase();
		return this;
	}else{			
		return this;
	}
  }

  public void close() {
	if(mOpen == true){
		db.close();
	}else{
		return;
	}
  }

  // POSITION
  
  public long addPosition(String positionName, String latitude, String longitude, String altitude, Date date)
  {
	    ContentValues contentValues = new ContentValues();
  	    contentValues.put(POSITION_LATITUDE_KEY, latitude);
  	    contentValues.put(POSITION_LONGITUDE_KEY, longitude);
  	  	contentValues.put(POSITION_ALTITUDE_KEY, altitude);
  		contentValues.put(POSITION_NAME_KEY, positionName);
  		contentValues.put(POSITION_DATE_KEY, date.getTime());
  	    
	   return db.insert(POSITION_TABLE, null, contentValues);
  }
  
  public long addPosition(Position p)
  {
	  return addPosition(p.getName(), p.getLatitude(), p.getLongitude(), p.getAltitude(), p.getDate());
  }
  

  public boolean removePosition(Long _rowIndex) {
	if(canDeletePosition(_rowIndex)){
		return db.delete(POSITION_TABLE, ROW_ID + "=" + _rowIndex, null) > 0;
	}else{
		return false;
	}
  }

  public boolean removeAllPositions()
  {
		return db.delete(POSITION_TABLE, null, null) > 0;
  }
   
  
  public Cursor getAllPositions () {
    return db.query(POSITION_TABLE, new String[] {POSITION_ROW_ID, 
    											  POSITION_NAME_KEY, 
    											  POSITION_LATITUDE_KEY,
    											  POSITION_LONGITUDE_KEY,
    											  POSITION_ALTITUDE_KEY,
    											  POSITION_DATE_KEY}, 
                    null, null, null, null, null);
  }


  
    
  public Cursor getPosition(long _rowIndex) {
    
    Cursor res = db.query(POSITION_TABLE, new String[] {POSITION_ROW_ID, 
    												 POSITION_NAME_KEY, 
													 POSITION_LATITUDE_KEY,
													 POSITION_LONGITUDE_KEY,
													 POSITION_ALTITUDE_KEY,
													 POSITION_DATE_KEY}, ROW_ID + " = " + _rowIndex, 
    		null, null, null, null);
    
    if(res != null){
    	res.moveToFirst();
    }
    
    return res;
  }
  
  public Position getPositionObj(long _rowIndex){
	  Cursor res = getPosition(_rowIndex);
	  if(res == null){
		  return null;
	  }
	  res.moveToFirst();
	  
	  if(res != null){
		  Position pos = fetchPosition(res);
	  
		  res.close();			
		  return pos;
	  }
	  return new Position();
  }
  
  	  
  
  
  public int updatePosition(long _rowIndex, String positionName, String latitude, String longitude, String altitude, Date date) 
  {
    String where = POSITION_ROW_ID + " = " + _rowIndex;
    ContentValues contentValues = new ContentValues();
    contentValues.put(POSITION_LATITUDE_KEY, latitude);
    contentValues.put(POSITION_LONGITUDE_KEY, longitude);
  	contentValues.put(POSITION_ALTITUDE_KEY, altitude);
	contentValues.put(POSITION_NAME_KEY, positionName);
	contentValues.put(POSITION_DATE_KEY, date.getTime());
    return db.update(POSITION_TABLE, contentValues, where, null);
  }
  
  private boolean canDeletePosition(long positionId){

	    Cursor res = db.query(RANGE_TABLE, new String[] {ROW_ID, START_RANGE_KEY}, POSITION_ID_KEY + " = " + positionId, 
	    		null, null, null, null);
	    
	    if(res != null){
	    	if(res.getCount() > 0)
	    		return false;
	    }
	    return true;
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
  
  public boolean removeAllRanges()
  {
	  return db.delete(RANGE_TABLE, null, null) > 0;
  }
  
  public Cursor getAllRanges () {
    return db.query(RANGE_TABLE, new String[] {ROW_ID, START_RANGE_KEY, END_RANGE_KEY, POSITION_ID_KEY}, 
                    null, null, null, null, null);
  }

  
  public Cursor getRangesPositions()
  {
	  String query = "select r." + ROW_ID + 
	  				 ", r." + START_RANGE_KEY +
	  				 ", r." + END_RANGE_KEY + 
	  				 ", p." + POSITION_NAME_KEY +
	  				 ", p." + POSITION_LATITUDE_KEY + 
	  				 ", p." + POSITION_LONGITUDE_KEY +
	  				 ", P." + POSITION_ALTITUDE_KEY +
	  				", P." + POSITION_DATE_KEY +
	  				 " from " + RANGE_TABLE + " r , " + POSITION_TABLE + " p " +
	  				 "where r." + POSITION_ID_KEY + " = " + "p." + POSITION_ROW_ID; 
	  
	  Cursor c = db.rawQuery(query, null); 
	  return c;
  }
  
  private String getGpxStringDate(Date d)
  {
	  Calendar c = Calendar.getInstance();
	  c.setTime(d);
	  //2001-11-28T21:05:28Z
	  String res = String.format("%02d-%02d-%02dT%02d:%02d:%02dZ", c.get(Calendar.YEAR), 
			  													   c.get(Calendar.MONTH) + 1, 
			  													   c.get(Calendar.DAY_OF_MONTH),
			  													   c.get(Calendar.HOUR_OF_DAY),
			  													   c.get(Calendar.MINUTE), 
			  													   c.get(Calendar.SECOND));
	  return res;
  }
  
  public static String buildOutputFileName(String ext)
	{
		Date now = new Date();
		
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		return "geotagger" + String.format("%02d%02d%02d-%02d%02d", c.get(Calendar.YEAR), 
				  													   c.get(Calendar.MONTH) + 1, 
				  													   c.get(Calendar.DAY_OF_MONTH),
				  													   c.get(Calendar.HOUR_OF_DAY),
				  													   c.get(Calendar.MINUTE)) + "." + ext;		
	}

  public boolean storeToGpx(String fileName)
  {
	  Cursor positionsCursor = getAllPositions();
	  if(positionsCursor.getCount() <= 0)
		  return false;
	  
	  if(positionsCursor.moveToFirst() == false)
		  return false;
	  	  
	  File newxmlfile = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
      try{
        newxmlfile.createNewFile();
      }catch(IOException e){
        return false;
      }
      FileOutputStream fileos = null;          
      try{
        fileos = new FileOutputStream(newxmlfile);
      }catch(FileNotFoundException e){
        return false;
      }

      XmlSerializer serializer = Xml.newSerializer();
      try {

             serializer.setOutput(fileos, "UTF-8");
             //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
             serializer.startDocument(null, Boolean.valueOf(true));
             //set indentation option
             serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
             
             serializer.startTag(null, "gpx");
             
             do{
            	 Position p = fetchPosition(positionsCursor);
                  
                  serializer.startTag(null, "wpt");
                  serializer.attribute(null, "lat", p.getLatitude());
                  serializer.attribute(null, "lon", p.getLongitude());
                  	serializer.startTag(null, "ele");
                  		serializer.text(p.getAltitude());
                  	serializer.endTag(null, "ele");
                  	serializer.startTag(null, "name");
              			serializer.text(p.getName());
              		serializer.endTag(null, "name");
              		serializer.startTag(null, "time");
              			Date d = p.getDate();
          				serializer.text(getGpxStringDate(d));
          			serializer.endTag(null, "time");
                  serializer.endTag(null, "wpt");
                   
             
             } while (positionsCursor.moveToNext());     
             serializer.endTag(null, "gpx");
             serializer.endDocument();
             //write xml data into the FileOutputStream
             serializer.flush();
             //finally we close the file stream
             fileos.close();
        } catch (Exception e) {
             return false;
        }
   
	  return true;
  }
  
  
  public boolean storeToXml(String fileName)
  {
	  Cursor positionRangesCursor = getRangesPositions();
	  if(positionRangesCursor.getCount() <= 0)
		  return false;
	  
	  if(positionRangesCursor.moveToFirst() == false)
		  return false;
	  	  
	  File newxmlfile = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
      try{
        newxmlfile.createNewFile();
      }catch(IOException e){
        return false;
      }
      FileOutputStream fileos = null;          
      try{
        fileos = new FileOutputStream(newxmlfile);
      }catch(FileNotFoundException e){
        return false;
      }

      XmlSerializer serializer = Xml.newSerializer();
      try {

             serializer.setOutput(fileos, "UTF-8");
             //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
             serializer.startDocument(null, Boolean.valueOf(true));
             //set indentation option
             serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
             //start a tag called "root"
             serializer.startTag(null, "ranges");
             
             do{
            	 PositionForRange pos = fetchPositionForRange(positionRangesCursor);
                  
                  serializer.startTag(null, "range");
                  
                  serializer.attribute(null, "from", pos.getFrom().toString());
                  serializer.attribute(null, "to", pos.getTo().toString());
                  serializer.attribute(null, "latitude", pos.getLatitude());
                  serializer.attribute(null, "longitude", pos.getLongitude());
                  serializer.attribute(null, "altitude", pos.getAltitude());
                  serializer.endTag(null, "range");
             
             } while (positionRangesCursor.moveToNext());     
             serializer.endTag(null, "ranges");
             serializer.endDocument();
             //write xml data into the FileOutputStream
             serializer.flush();
             //finally we close the file stream
             fileos.close();
        } catch (Exception e) {
             return false;
        }
   
	  return true;
  }
  
  
  public PositionForRange fetchPositionForRange(Cursor c)
  {
	  int first = 1;
	  PositionForRange res = new PositionForRange(c.getLong(first++),
			  									  c.getLong(first++),
			  									  c.getString(first++),
			  									  c.getString(first++),
			  									  c.getString(first++),
			  									  c.getString(first++),
			  									  new Date(c.getLong(first++)));
	  return res;
  }

  public Position fetchPosition(Cursor c)
  {
	  return new Position( c.getString(POSITION_NAME_COLUMN),
			  			   c.getString(POSITION_LATITUDE_COLUMN),
			  			   c.getString(POSITION_LONGITUDE_COLUMN),
			  			   c.getString(POSITION_ALTITUDE_COLUMN),
			  			   new Date(c.getLong(POSITION_DATE_COLUMN)));	  	  
  }
    
  public Cursor getRange(long _rowIndex) {
    
    Cursor res = db.query(RANGE_TABLE, new String[] {ROW_ID, START_RANGE_KEY, END_RANGE_KEY, POSITION_ID_KEY}, ROW_ID + " = " + _rowIndex, 
    		null, null, null, null);
    
    if(res != null){
    	res.moveToFirst();
    }
    return res;
  }
  
  public Long getMaxEndRange() {
	    String query = "select 1 as _id, " +	// since cursors need a column named _id, I fool it by putting this fake column
	    		"max(" + END_RANGE_KEY + ") as maxRange from " + RANGE_TABLE;
	    Cursor c = db.rawQuery(query, null); 
	    Long res = new Long(0);
	    if(c != null){
	    	c.moveToFirst();
	     	res = c.getLong(1);
	    }
	    return res;
  }	  
  
  
  // checks the value is already in a stored range
  public boolean goodRangeBound(Long bound)
  {
	  String query = START_RANGE_KEY + " <= " + bound.toString() + " and " + END_RANGE_KEY + " > " + bound.toString();
	  Cursor res = db.query(RANGE_TABLE, new String[] {ROW_ID, START_RANGE_KEY}, 
			  								query, 			
			  								null, null, null, null);
	  if(res == null)
		  return true;
	  
	  if(res.getCount() > 0){
		  return false;
	  }
	  
	  return true;
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
  
  public void removeAll()
  {
	  removeAllRanges();
	  removeAllPositions();
	  
  }


  private static class myDbHelper extends SQLiteOpenHelper {

    public myDbHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one. 
    @Override
    public void onCreate(SQLiteDatabase _db) {      
      _db.execSQL(DATABASE_POSITION_CREATE);
      _db.execSQL(DATABASE_RANGE_CREATE);
      _db.execSQL(CREATE_INDEX_RANGE_START);
      _db.execSQL(CREATE_INDEX_RANGE_END);
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


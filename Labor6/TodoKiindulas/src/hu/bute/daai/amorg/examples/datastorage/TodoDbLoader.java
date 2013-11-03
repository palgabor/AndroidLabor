package hu.bute.daai.amorg.examples.datastorage;

import hu.bute.daai.amorg.examples.data.Todo;
import hu.bute.daai.amorg.examples.data.Todo.Priority;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.widget.AdapterView;

public class TodoDbLoader {
	
	private Context ctx;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase mDb;
	
	public TodoDbLoader(Context ctx) {
		this.ctx = ctx;
	}
	
	public void open() throws SQLException{
		// DatabaseHelper objektum
		dbHelper = new DatabaseHelper(
				ctx, DbConstants.DATABASE_NAME);
		// adatbázis objektum
		mDb = dbHelper.getWritableDatabase();
		// ha nincs még séma, akkor létrehozzuk
		dbHelper.onCreate(mDb);
	}
	
	public void close(){
		dbHelper.close();
	}

	// INSERT 
	public long createTodo(Todo todo){
		ContentValues values = new ContentValues();
		values.put(DbConstants.Todo.KEY_TITLE, todo.getTitle());
		values.put(DbConstants.Todo.KEY_DUEDATE, todo.getDueDate());
		values.put(DbConstants.Todo.KEY_DESCRIPTION, todo.getDescription());
		values.put(DbConstants.Todo.KEY_PRIORITY, todo.getPriority().name());
		
		return mDb.insert(DbConstants.Todo.DATABASE_TABLE, null, values);
	}

	// DELETE
	public boolean deleteTodo(long rowId){
		return mDb.delete(
				DbConstants.Todo.DATABASE_TABLE, 
				DbConstants.Todo.KEY_ROWID + "=" + rowId, 
				null) > 0;
	}
	
	public void deleteAllTodo(){
		Cursor cursor = mDb.rawQuery("select * from todo", null);
		int number;
		for(int i=0;i<cursor.getCount();i++){
			cursor.moveToPosition(i);
			number = cursor.getInt(cursor.getColumnIndex(DbConstants.Todo.KEY_ROWID));
			deleteTodo(number);
		}
	}

	// UPDATE
	public boolean updateProduct(long rowId, Todo newTodo){
		ContentValues values = new ContentValues();
		values.put(DbConstants.Todo.KEY_TITLE, newTodo.getTitle());
		values.put(DbConstants.Todo.KEY_DUEDATE, newTodo.getDueDate());
		values.put(DbConstants.Todo.KEY_DESCRIPTION, newTodo.getDescription());
		values.put(DbConstants.Todo.KEY_PRIORITY, newTodo.getPriority().name());
		return mDb.update(
				DbConstants.Todo.DATABASE_TABLE, 
				values, 
				DbConstants.Todo.KEY_ROWID + "=" + rowId , 
				null) > 0;
	}
	
	public Cursor fetchAll(){
		// cursor minden rekordra (where = null)
		return mDb.query(
				DbConstants.Todo.DATABASE_TABLE, 
				new String[]{ 
						DbConstants.Todo.KEY_ROWID,
						DbConstants.Todo.KEY_TITLE,
						DbConstants.Todo.KEY_DESCRIPTION,
						DbConstants.Todo.KEY_DUEDATE,
						DbConstants.Todo.KEY_PRIORITY
				}, null, null, null, null, DbConstants.Todo.KEY_TITLE);
	}

	// egy Todo lekérése
	public Todo fetchTodo(long rowId){
		// a Todo-ra mutato cursor
		Cursor c = mDb.query(
				DbConstants.Todo.DATABASE_TABLE, 
				new String[]{ 
						DbConstants.Todo.KEY_ROWID,
						DbConstants.Todo.KEY_TITLE,
						DbConstants.Todo.KEY_DESCRIPTION,
						DbConstants.Todo.KEY_DUEDATE,
						DbConstants.Todo.KEY_PRIORITY
				}, DbConstants.Todo.KEY_ROWID + "=" + rowId, 
				null, null, null, DbConstants.Todo.KEY_TITLE);
		// ha van rekord amire a Cursor mutat
		if(c.moveToFirst())
			return getTodoByCursor(c);
		// egyebkent null-al terunk vissza
		return null;
	}
	
	public static Todo getTodoByCursor(Cursor c){
		return new Todo(
				c.getString(c.getColumnIndex(DbConstants.Todo.KEY_TITLE)), // title 
				Priority.valueOf(c.getString(c.getColumnIndex(DbConstants.Todo.KEY_PRIORITY))), // priority 
				c.getString(c.getColumnIndex(DbConstants.Todo.KEY_DUEDATE)), // dueDate 
				c.getString(c.getColumnIndex(DbConstants.Todo.KEY_DESCRIPTION)) // description
				);
	}
}
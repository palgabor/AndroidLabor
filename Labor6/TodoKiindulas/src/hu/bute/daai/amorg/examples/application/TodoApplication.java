package hu.bute.daai.amorg.examples.application;

import hu.bute.daai.amorg.examples.datastorage.TodoDbLoader;
import android.app.Application;

public class TodoApplication extends Application {
	private static TodoDbLoader dbLoader;

	public static TodoDbLoader getTodoDbLoader() {
		return dbLoader;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		dbLoader = new TodoDbLoader(this);
		dbLoader.open();
	}

	@Override
	public void onTerminate() {
		// Close the internal db
		dbLoader.close();
		
		super.onTerminate();
	}
}

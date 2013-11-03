package hu.bute.daai.amorg.examples.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Todo implements Parcelable {

	public enum Priority {
		LOW, MEDIUM, HIGH
	}

	private String title;
	private Priority priority;
	private String dueDate;
	private String description;

	public Todo(String aTitle, Priority aPriority, String aDueDate,
			String aDescription) {
		title = aTitle;
		priority = aPriority;
		dueDate = aDueDate;
		description = aDescription;
	}

	public Todo(Parcel in) {
		this.title = in.readString();
		String priorityName = in.readString();
		if (priorityName != null)
			this.priority = Priority.valueOf(priorityName);
		this.dueDate = in.readString();
		this.description = in.readString();
	}

	public String getTitle() {
		return title;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getDueDate() {
		return dueDate;
	}

	public String getDescription() {
		return description;
	}

	// Parcelable
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(priority == null ? null : priority.name());
		dest.writeString(dueDate);
		dest.writeString(description);
	}

	public static final Parcelable.Creator<Todo> CREATOR = new Parcelable.Creator<Todo>() {
		public Todo createFromParcel(Parcel in) {
			return new Todo(in);
		}

		public Todo[] newArray(int size) {
			return new Todo[size];
		}
	};
}

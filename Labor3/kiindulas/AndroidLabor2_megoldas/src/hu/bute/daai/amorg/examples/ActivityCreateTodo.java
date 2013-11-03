package hu.bute.daai.amorg.examples;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityCreateTodo extends Activity {
	
	private static final int DATE_DUE_DIALOG_ID = 1;
	
	private Calendar calSelectedDate = Calendar.getInstance();
	
	private EditText editTodoTitle;
	private Spinner spnrTodoPriority;
	private TextView txtDueDate;
	private EditText editTodoDescription;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createtodo);
        
        // aktuális dátum beállítása
        calSelectedDate.setTime(new Date(System.currentTimeMillis()));

        // UI elem referenciák elkérése
        editTodoTitle = (EditText)this.findViewById(R.id.todoTitle);
        
        spnrTodoPriority = (Spinner)this.findViewById(R.id.todoPriority);
        String[] priorities=new String[3];
        priorities[0]="Low";
        priorities[1]="Medium";
        priorities[2]="High";
        spnrTodoPriority.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_item, priorities));
        
        txtDueDate = (TextView)this.findViewById(R.id.todoDueDate);
        refreshDateText();
        txtDueDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				showDialog(DATE_DUE_DIALOG_ID);
			}
	    });
        
        editTodoDescription = (EditText)this.findViewById(R.id.todoDescription);
        
		// gomb eseménykezelõk beállítása
		Button btnOk = (Button)findViewById(R.id.btnCreateTodo);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Todo.Priority selectedPriority = Todo.Priority.LOW;
				
				switch (spnrTodoPriority.getSelectedItemPosition()) {
					case 0:
						selectedPriority = Todo.Priority.LOW;
						break;
					case 1:
						selectedPriority = Todo.Priority.MEDIUM;
						break;
					case 2:
						selectedPriority = Todo.Priority.HIGH;
						break;
					default:
						break;
				}
				
				// NAGYON RONDA MEGOLDÁS
				// Intent elõadáson megmutatjuk a helyeset!
				DataPreferences.todoToCreate = new Todo(
						editTodoTitle.getText().toString(),
						selectedPriority,
						txtDueDate.getText().toString(),
						editTodoDescription.getText().toString()
				);
		
				finish();
			}
		});
		
		Button btnCancel = (Button)findViewById(R.id.btnCancelCreateTodo);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DataPreferences.todoToCreate = null;
				finish();
			}
		});
    }
    
    private void refreshDateText()
    {
    	StringBuilder dateString = new StringBuilder();
    	dateString.append(calSelectedDate.get(Calendar.YEAR));
    	dateString.append(". ");
    	dateString.append(calSelectedDate.get(Calendar.MONTH)+1);
    	dateString.append(". ");
    	dateString.append(calSelectedDate.get(Calendar.DAY_OF_MONTH));
    	dateString.append(".");
    	
    	txtDueDate.setText(dateString.toString());
    }
    
    @Override    
    protected Dialog onCreateDialog(int id) 
    {
        switch (id) {
            case DATE_DUE_DIALOG_ID:
            {
                return new DatePickerDialog(
                    this, mDateSetListener, calSelectedDate.get(Calendar.YEAR),
                    calSelectedDate.get(Calendar.MONTH), calSelectedDate.get(Calendar.DAY_OF_MONTH)); 
            }
        }
        return null;    
    }
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() 
        {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) 
            {
            	// Új dátum beállítása
            	calSelectedDate.set(Calendar.YEAR, year);
            	calSelectedDate.set(Calendar.MONTH, monthOfYear);
            	calSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            	
            	refreshDateText();
            	
                removeDialog(DATE_DUE_DIALOG_ID);
            }
        };
}

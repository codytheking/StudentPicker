import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This program reads student names from a .txt file
 * and picks a random name.
 *
 * The user picks the class period from the list and then
 * clicks the button to pick a name. The program will
 * parse through the entire list of names before a
 * name will get picked a second time.
 *
 * @author Cody King
 * @date 11/10/2016
 * 
 * v1.1: May 2017
 * 	-Minor fixes
 * 	-Still crashes occasionally	
 * 
 * v2.0 beta Feb 2018
 * 	-Class reorganization
 * 	-Working on fixing crashes	
 */

public class StudentPicker extends Application
{
	public final Text MESSAGE = new Text();

	// pre: none
	// post: sets up gui window
	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("King's Student Picker");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 300, 225);


		// Top Menu Bar
		MenuBar menuBar = new MenuBar();
		Menu menu1 = new Menu("Help");
		final String os = System.getProperty("os.name");

		MenuItem menuItemA = new MenuItem("About");
		menuItemA.setOnAction(e -> aboutMenuAction());
		/*menuItemA.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent e)
			{
				aboutMenuAction();
			}
		});*/

		menu1.getItems().add(menuItemA);
		menuBar.getMenus().add(menu1);
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		if(os != null && os.startsWith("Mac"))
		{
			menuBar.useSystemMenuBarProperty().set(true);
		}

		grid.getChildren().add(menuBar);
		primaryStage.setScene(scene);


		// Text for displaying student names (or error messages)
		Text welcomeText = new Text("Choose the class period");
		welcomeText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(welcomeText, 0, 1);

		// Options for ComboBox (class periods)
		ObservableList<String> options = FXCollections.observableArrayList(
				"Period 0", "Period 1", "Period 2", "Period 3", "Period 4",
				"Period 5", "Period 6", "Period 7", "Period 8");

		final ComboBox<String> perComboBox = new ComboBox<String>(options);
		grid.add(perComboBox, 0, 2);

		// Button for picking a new name
		Button pickBtn = new Button();
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_LEFT);
		hbBtn.getChildren().add(pickBtn);
		grid.add(hbBtn, 0, 3);
		pickBtn.setText("Pick");

		// print error or picked name
		//error.setId("error");  //link to css file
		grid.add(MESSAGE, 0, 4);

		pickBtn.setOnAction(e -> pickActions(perComboBox));
		/*pickBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				pickActions(perComboBox);
			}
		});*/

		primaryStage.show();
	}


	/*
	 * Helper methods
	 */

	// pre: gets information from ComboBox
	// post: performs picks and prints name
	public void pickActions(ComboBox<String> perComboBox)
	{
		int num = 0;
		String namesFile = "";
		String callableFile = "";
		List<Integer> callable = null;
		List<String> names = null;
		MESSAGE.setFill(Color.FIREBRICK);

		if(perComboBox.getValue() == null)
		{
			MESSAGE.setText("Please pick a period.");
		}

		else
		{
			namesFile = "period" + period(perComboBox.getValue()) + ".txt";
			callableFile = "period" + period(perComboBox.getValue()) + "_called.txt";

			// make sure files exist
			if(!fileExists(namesFile))
			{
				MESSAGE.setText("No file found with names.");
				return;
			}

			// ArrayList with nums able to be picked
			callable = getCallable(callableFile);

			// ArrayList with student names
			names = getNames(namesFile);

			if(names.get(0).equals("ERROR"))
			{
				MESSAGE.setText("Please pick a valid period.");
				return;
			}

			// if all names have been called, reset
			// and start picking from entire list.
			if(callable.size() == 0)
			{	
				resetCallableFile(callableFile, names.size());
				for(int i = 0; i < names.size(); i++)
				{
					callable.add(i);
				}
			}

			// pick a random student who hasn't been called yet
			num = (int) (callable.size() * Math.random());

			// print picked name
			MESSAGE.setText(names.get(callable.get(num)));

			// remove index so it doesn't get called again
			callable.remove(num);
		}

		// update file with indices able to be picked.
		store_called(callable, callableFile);
	}

	// pre: takes file name
	// post: returns ArrayList of student names
	public List<String> getNames(String file)
	{
		Scanner inFile = null;

		try
		{
			inFile = new Scanner(new File(file));
		}

		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			List<String> err = new ArrayList<String>();
			err.add("ERROR");
			return err;
		}

		List<String> names = new ArrayList<String>();

		while(inFile.hasNext())
		{
			names.add(inFile.nextLine());
		}
		inFile.close();

		return names;
	}

	// pre: takes file name
	// post: returns an array of integers of called indices
	public List<Integer> getCallable(String file)
	{
		Scanner inFile = null;
		try 
		{
			inFile = new Scanner(new File(file));
		} 
		catch (FileNotFoundException e) 
		{
			File f = new File("./" + file);
			f.getParentFile().mkdirs(); 
			
			try 
			{
				f.createNewFile();
				
				try 
				{
					inFile = new Scanner(new File(file));
				} 
				catch (FileNotFoundException e1) 
				{
					e1.printStackTrace();
				}
			} 
			catch (IOException e2) 
			{
				e2.printStackTrace();
			}
		}

		List<Integer> nums = new ArrayList<Integer>();

		String str = "";

		while(inFile.hasNext())
		{
			str = inFile.nextLine();
			nums.add(Integer.parseInt(str));
		}
		inFile.close();

		return nums;
	}

	// pre: takes integer array of current called indices, its file name, and number picked in round
	// post: updates file of called indices
	public void store_called(List<Integer> callable, String file)
	{
		try
		{
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));

			for(Integer i: callable)
			{
				writer.write(i + "\n");
			}

			writer.close();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	// pre: takes period choice from ComboBox (Period 3, Period 5, etc.)
	// post: return period number as int
	public int period(Object object)
	{
		return Character.getNumericValue(((String) object).charAt(7));
	}

	// pre: none
	// post: opens window with application information
	public void aboutMenuAction()
	{
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Stage aboutStage = new Stage();
		aboutStage.setTitle("About Student Picker");
		aboutStage.setScene(new Scene(grid, 250, 225));

		Text aboutText = new Text("King's Student Picker");
		aboutText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		Text coderText = new Text("Created by Cody King\nFeel free to redistribute application and\n"
				+ "adapt code to fit your needs.");
		coderText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
		Text verText = new Text("Ver 1.0");
		verText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));

		grid.add(aboutText, 0, 0);
		grid.add(coderText, 0, 1);
		grid.add(verText, 0, 3);

		aboutStage.show();
	}

	public void resetCallableFile(String file, int len)
	{	
		try
		{
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));

			for(int i = 0; i < len; i++)
			{
				writer.write(i + "\n");
			}

			writer.close();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean fileExists(String file)
	{
		File f = new File(file);

		return (f.exists() && !f.isDirectory());
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}

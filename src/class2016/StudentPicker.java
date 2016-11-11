package class2016;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
 */

public class StudentPicker extends Application
{
	public static int i = 0, num = 0;
	public static String perNames, perCalled;
	public static List<String> names = new ArrayList<>();
	public static int[] called = new int[40];

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException
	{
		primaryStage.setTitle("Student Picker");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Scene scene = new Scene(grid, 300, 275);


		//Top Menu Bar
		/*MenuBar menuBar = new MenuBar();
	    Menu menu1 = new Menu("Menu");
	    MenuItem menuItemA = new MenuItem("Item A");
	    menuItemA.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	            System.out.println("Item A Clicked");
	        }
	    });
	    MenuItem menuItemB = new MenuItem("Item B");
	    menuItemB.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	            System.out.println("Item B Clicked");
	        }
	    });
	    MenuItem menuItemC = new MenuItem("Item C");
	    menuItemC.setOnAction(new EventHandler<ActionEvent>() {
	        @Override public void handle(ActionEvent e) {
	            System.out.println("Item C Clicked");
	        }
	    });
	    menu1.getItems().add(menuItemA);
	    menu1.getItems().add(menuItemB);
	    menu1.getItems().add(menuItemC);
	    menuBar.getMenus().add(menu1);
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
	    grid.getChildren().add(menuBar);
	    primaryStage.setScene(scene);
	    primaryStage.show();*/







		/*MenuBar menuBar = new MenuBar();
		menuBar.useSystemMenuBarProperty().set(true);
		Menu menu = new Menu("java");
		MenuItem item = new MenuItem("Test");
		menu.getItems().add(item);
		menuBar.getMenus().add(menu);*/





		primaryStage.setScene(scene);

		Text welcomeText = new Text("King's Student Picker");
		welcomeText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(welcomeText, 0, 1);

		ObservableList<String> options =
				FXCollections.observableArrayList("Period 0", "Period 1", "Period 2", "Period 3",
						"Period 4", "Period 5", "Period 6", "Period 7", "Period 8");

		final ComboBox<String> perComboBox = new ComboBox<>(options);
		grid.add(perComboBox, 0, 2);


		Button pickBtn = new Button();
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_LEFT);
		hbBtn.getChildren().add(pickBtn);
		grid.add(hbBtn, 0, 3);
		pickBtn.setText("Pick");


		// print error or picked name
		final Text message = new Text();
		//error.setId("error");
		grid.add(message, 0, 4);

		pickBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				message.setFill(Color.FIREBRICK);

				if(perComboBox.getValue() == null)
				{
					message.setText("Please pick a period.");
				}

				else
				{
					perNames = "period" + period(perComboBox.getValue()) + ".txt";
					perCalled = "period" + period(perComboBox.getValue()) + "_called.txt";

					// array with picked nums
					try
					{
						called = called(perCalled);
						int len = called.length;
						i = called[len - 1];
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}

					// ArrayList with student names
					try
					{
						names = names(perNames);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					if(names.get(0).equals("ERROR"))
					{
						message.setText("Please pick a valid period.");
						return;
					}

					Random r = new Random();
					boolean found = true;
					boolean spec = false;
					int loops = 0;
					int over = 0;

					// keep picking random numbers until new
					// student is picked so a student won't
					// get picked more frequently than others
					do
					{
						found = false;
						num = r.nextInt(names.size());

						for(int j = 0; j < called.length; j++)
						{
							if(called[j] == num)
							{
								found = true;
							}
						}

						loops++;

						// stop if taking too long to find a new name
						if(loops > 100000)
						{
							found = false;

							if(i >= names.size() - 1)
							{
								spec = true;
								over = num;
							}
						}
					}
					while(found);

					// print picked name
					message.setText(names.get(num));

					// put picked index in array, with other
					// already picked indices.
					called[i] = num;
					i++;

					// if all names have been called, reset
					// and start picking from entire list.
					if(i >= names.size())
					{
						i = 0;
						Arrays.fill(called, -1);
					}

					// took too long to find name, added picked name
					// to reset list.
					if(spec)
					{
						called[i] = over;
						i++;
						spec = false;
					}
				}

				// update file with picked indices.
				try
				{
					store_called(called, perCalled, i);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});


		primaryStage.show();
	}

	// pre: takes file name
	// post: returns ArrayList of student names
	public static List<String> names(String file) throws IOException
	{
		Scanner inFile = null;

		try
		{
			inFile = new Scanner(new File(file));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			List<String> err = new ArrayList<>();
			err.add("ERROR");
			return err;
		}

		List<String> names = new ArrayList<>();

		while(inFile.hasNext())
		{
			names.add(inFile.nextLine());
		}
		inFile.close();

		return names;
	}

	// pre: takes file name
	// post: returns array of called indices
	public static int[] called(String file) throws IOException
	{
		Scanner inFile = new Scanner(new File(file));
		List<Integer> nums = new ArrayList<>();
		int[] arr = new int[50];
		String str;

		while(inFile.hasNext())
		{
			str = inFile.nextLine();
			nums.add(Integer.parseInt(str));
		}
		inFile.close();

		for(int j = 0; j < nums.size(); j++)
		{
			arr[j] = nums.get(j);
		}

		return arr;
	}

	// pre: takes integer array of called indices, String for name of text file, number picked as int
	// post: updates text file.
	public static void store_called(int[] called, String file, int picks) throws IOException
	{
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "utf-8"))) {

			for(int j = 0; j < called.length - 1; j++)
			{
				writer.write(called[j] + "\n");
			}

			writer.write(picks + "\n");
		}
	}

	// pre: String p is the period from ComboBox (Period 3, Period 5, etc.)
	// post: return period number as int
	public static int period(String p)
	{
		return Character.getNumericValue(p.charAt(7));
	}
}
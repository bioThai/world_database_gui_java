//bioThai
//Create a GUI to access and display data from a world facts database.
//Image Attribution: Globe search icon is by Freepik from flaticon.com.

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DatabaseGUI extends Application {
	//fields
	private Connection dbConnection;
	private ListView<String> listView; 
	private TextArea factsTextArea;
	private RadioButton citiesRadio;
	private RadioButton countriesRadio;
	private RadioButton languagesRadio;
	private CheckBox countryCheck;
	private CheckBox continentCheck;
	private CheckBox populationCheck;
	private CheckBox lifeExpectancyCheck;
	private CheckBox languageCheck;
	
	
	/********************************************************* 
	 * openDBconnection()
	 * Opens a connection to the WorldDB database located in the current project directory.
	 * @throws SQLException
	 *********************************************************/
	public void openDBconnection() throws SQLException
	{
		//local variables
		final String DB_URL = "jdbc:derby:WorldDB";
		
		dbConnection = DriverManager.getConnection(DB_URL);
	}
	
	/********************************************************* 
	 * closeDBconnection()
	 * Closes a connection to the WorldDB database located in the current project directory.
	 * @throws SQLException
	 *********************************************************/
	public void closeDBconnection() throws SQLException
	{
		dbConnection.close();
	}
	
	/********************************************************* 
	 * main(String[] args)
	 * Opens and closes a connection to the WorldDB database.
	 * Launches a javaFX application window.
	 *********************************************************/
	public static void main(String[] args) 
	{
		//launch application
		launch(args);
	}
	
	/********************************************************* 
	 * start(Stage)
	 * Subclass method to override abstract Application.start() method.
	 * Sets the scene for the main stage.
	 *********************************************************/
	@Override
	public void start(Stage primaryStage)
	{
		//create labels
		Label bannerLabel = new Label("World Facts Database");
		Label radioGroupLabel = new Label("1. Select a list:");
		Label listViewLabel = new Label("2. Select an item from the list:");
		Label checkboxesLabel = new Label("3. Select facts to show:");
		Label textAreaLabel = new Label("Facts:");
		
		//create images for app banner
		Image img1 = new Image("file:globe.png");
		ImageView globeIconView = new ImageView(img1);
		
		//create toggle group, create radio buttons and add to toggle group
		ToggleGroup radioGroup = new ToggleGroup();
		citiesRadio = new RadioButton("City names");
		countriesRadio = new RadioButton("Country names");
		languagesRadio = new RadioButton("Languages");
		citiesRadio.setToggleGroup(radioGroup);
		countriesRadio.setToggleGroup(radioGroup);
		languagesRadio.setToggleGroup(radioGroup);
		
		//create checkboxes
		countryCheck = new CheckBox("Country name(s)");
		continentCheck = new CheckBox("Continent(s)");
		populationCheck = new CheckBox("Population");
		lifeExpectancyCheck = new CheckBox("Average life expectancy");
		languageCheck = new CheckBox("Language(s)");
				
		//create regular buttons
		Button showFactsButton = new Button("Show Facts");
	
		//create TextArea
		factsTextArea = new TextArea();
				
		//format labels, images, buttons, checkboxes, and other controls
		bannerLabel.setFont(Font.font(34));
		bannerLabel.setTextFill(Paint.valueOf("gray"));
		radioGroupLabel.setWrapText(true);
		listViewLabel.setWrapText(true);
		listViewLabel.setPadding(new Insets(10, 0, 0, 0));
		checkboxesLabel.setWrapText(true);
		textAreaLabel.setWrapText(true);		
		citiesRadio.setSelected(true);				//citiesRadio will be selected as default upon stage startup
		globeIconView.setFitHeight(45);				//set height of icon to 45px
		globeIconView.setPreserveRatio(true);	
		showFactsButton.setDefaultButton(true);		//this button is "clicked" when Enter key is pressed		
		showFactsButton.setPrefSize(135, 30);
		showFactsButton.setFont(Font.font(20));
		factsTextArea.setEditable(false);			//text area will be "read-only" and can't be rewritten by user
		
		//create ListView and create another thread to populate it with items from database
		listView = new ListView<>();
		ListViewTask setUpTask = new ListViewTask();
		Thread listViewSetUpThread = new Thread(setUpTask);
		listViewSetUpThread.start();
		
		//register event handler for citiesRadio
		citiesRadio.setOnAction(event ->
		{
			try 
			{
				ListViewTask citiesTask = new ListViewTask();
				Thread citiesViewThread = new Thread(citiesTask);
				citiesViewThread.start();
			}
			catch (Exception e)
			{
				Alert alert = new Alert(AlertType.ERROR, e.getMessage());
				alert.setHeaderText("Error Message");
				alert.showAndWait();
			}	
		});
		
		//register event handler for countriesRadio
		countriesRadio.setOnAction(event ->
		{
			try 
			{
				ListViewTask countriesTask = new ListViewTask();
				Thread countriesViewThread = new Thread(countriesTask);
				countriesViewThread.start();
			}
			catch (Exception e)
			{
				Alert alert = new Alert(AlertType.ERROR, e.getMessage());
				alert.setHeaderText("Error Message");
				alert.showAndWait();
			}	
		});
		
		//register event handler for countriesRadio
		languagesRadio.setOnAction(event ->
		{
			try 
			{
				ListViewTask languagesTask = new ListViewTask();
				Thread languagesViewThread = new Thread(languagesTask);
				languagesViewThread.start();
			}
			catch (Exception e)
			{
				Alert alert = new Alert(AlertType.ERROR, e.getMessage());
				alert.setHeaderText("Error Message");
				alert.showAndWait();
			}	
		});
		
		//register event handler for showFactsButton
		showFactsButton.setOnAction(event -> 
		{
			if ((listView.getSelectionModel().isEmpty()) ||
			(!countryCheck.isSelected() && !continentCheck.isSelected() && !populationCheck.isSelected() && !lifeExpectancyCheck.isSelected() && !languageCheck.isSelected()))
			{
				Alert alert = new Alert(AlertType.WARNING, "Please select an item from the list and/or at least one fact to show.");
				alert.setHeaderText("Warning Message");
				alert.showAndWait();
			}
			else
			{
				try 
				{
					//create and run a separate thread to populate textArea with info from database
					TextAreaTask factsTask = new TextAreaTask();
					Thread showFactsThread = new Thread(factsTask);
					showFactsThread.start();
				}
				catch (Exception e)
				{
					Alert alert = new Alert(AlertType.ERROR, e.getMessage());
					alert.setHeaderText("Error Message");
					alert.showAndWait();
				}	
			}
		});
		
		//create HBox/VBox containers to hold labels and their corresponding controls
		HBox hBox1 = new HBox(15, globeIconView, bannerLabel);
		VBox showFactsButtonVBox = new VBox(5, showFactsButton);
		VBox vBox1 = new VBox(5, radioGroupLabel, citiesRadio, countriesRadio, languagesRadio, listViewLabel, listView);
		VBox vBox2 = new VBox(5, checkboxesLabel, countryCheck, continentCheck, populationCheck, lifeExpectancyCheck, languageCheck, showFactsButtonVBox);
		VBox vBox3 = new VBox(5, textAreaLabel, factsTextArea);
		
		//format HBox/VBox containers
		hBox1.setAlignment(Pos.CENTER);
		hBox1.setPadding(new Insets(5));
		showFactsButtonVBox.setAlignment(Pos.CENTER);
		showFactsButtonVBox.setPadding(new Insets(15,0,15,0));
		vBox1.setPrefWidth(235);
		vBox1.setPadding(new Insets(15, 20, 15, 20));
		vBox1.setStyle(
				"-fx-background-color: white;" 
				+ "-fx-background-insets: 2;"
				+ "-fx-border-color: linear-gradient(to bottom right, cyan, darkmagenta);" 
				+ "-fx-border-width: 2;" 
				+ "-fx-border-radius: 5;"
				+ "-fx-effect: dropshadow(gaussian, #999999, 20, 0, 0, 0)");
		vBox2.setPrefWidth(235);
		vBox2.setPadding(new Insets(15, 20, 15, 20));
		vBox2.setStyle(
				"-fx-background-color: white;" 
				+ "-fx-background-insets: 2;"
				+ "-fx-border-color: linear-gradient(to bottom left, cyan, darkmagenta);" 
				+ "-fx-border-width: 2;" 
				+ "-fx-border-radius: 5;"
				+ "-fx-effect: dropshadow(gaussian, #999999, 20, 0, 0, 0)");
		vBox3.setPrefWidth(235);
		vBox3.setPadding(new Insets(15, 20, 15, 20));
		vBox3.setStyle(
				"-fx-background-color: white;" 
				+ "-fx-background-insets: 2;"
				+ "-fx-border-color: linear-gradient(to bottom right, cyan, darkmagenta);" 
				+ "-fx-border-width: 2;" 
				+ "-fx-border-radius: 5;"
				+ "-fx-effect: dropshadow(gaussian, #999999, 20, 0, 0, 0)");
	
		//create HBox container to hold vBox1-3
		HBox mainHBox = new HBox(20, vBox1, vBox2, vBox3);
		
		//format main HBox
		mainHBox.setAlignment(Pos.CENTER);
		mainHBox.setStyle(
				"-fx-background-color: whitesmoke;");
		mainHBox.setPrefHeight(260);
		
		//create VBox container to hold all other VBoxes and HBoxes
		VBox mainVBox = new VBox(10, hBox1, mainHBox);
				
		//format main VBox
		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.setPadding(new Insets(10, 30,30,30));
				
		//create a Scene
		Scene mainScene = new Scene(mainVBox);
		
		//set the stage
		primaryStage.setScene(mainScene);
		primaryStage.setTitle("World Facts Database");
		primaryStage.getIcons().add(img1);
		
		//show the application window
		primaryStage.show();
	}
	
	/********************************************************* 
	 * ListViewTask
	 * Nested/inner class within DatabaseGUI.
	 * Creates a Task that creates/executes database queries and populates a specific listView with a list of cities, countries, or languages.
	 * Uses a Runnable object in an anonymous inner class to create and run threads aside from the main JavaFX thread.
	 *********************************************************/
	public class ListViewTask extends Task<Void>
	{
		@Override
		protected Void call() throws Exception
		{
			//anonymous inner class
			Platform.runLater(new Runnable()
			{
				@Override 
				public void run()
				{
					//local variables
					ObservableList<String> resultsList = FXCollections.observableArrayList();
					Statement listQuery;
					ResultSet listResult = null;
					String queryString = "";
					String name;
					
					try
					{
						resultsList.clear();
						openDBconnection();
						listQuery = dbConnection.createStatement();
						
						if (citiesRadio.isSelected())
							queryString = "SELECT cityName FROM City ORDER BY cityName";
						else if (countriesRadio.isSelected())
							queryString = "SELECT name FROM Country ORDER BY name";
						else if (languagesRadio.isSelected())
							queryString = "SELECT DISTINCT language FROM Language ORDER BY language";
						
						listResult = listQuery.executeQuery(queryString);
						while (listResult.next())
						{
							name = listResult.getString(1);
							resultsList.add(name);
						}	
						listView.setItems(resultsList);
						closeDBconnection();
					}
					catch (Exception e)
					{
						System.out.println("Error: " + e.getMessage());
					}
				}
			});
			return null;
		}
	}
	
	/********************************************************* 
	 * TextAreaTask
	 * Nested/inner class within DatabaseGUI.
	 * Creates a Task that creates/executes database queries and populates a specific TextArea with database info that the user chooses to show.
	 * Uses a Runnable object in an anonymous inner class to create and run threads aside from the main JavaFX thread.
	 *********************************************************/
	public class TextAreaTask extends Task<Void>
	{
		@Override
		protected Void call() throws Exception
		{
			//anonymous inner class
			Platform.runLater(new Runnable()
			{	
				/********************************************************* 
				 * showQueryResults(ResultSet queryResults, String category)
				 * Displays results of a database query in the appropriate category in the factsTextArea.
				 * @throws SQLException
				 *********************************************************/
				public void showQueryResults(ResultSet queryResults, String category) throws SQLException
				{
					//local variables
					ArrayList<String> resultsList = new ArrayList<String>();
					String resultString = "";
					
					while (queryResults.next())
					{
						resultString = queryResults.getString(1).trim();
						resultsList.add(resultString);
					}
					factsTextArea.appendText("\n" + category + ":\n\t");
					
					for (int i = 0; i < resultsList.size(); i++)
					{
						if (i == resultsList.size() - 1)
							factsTextArea.appendText(resultsList.get(i));
						else
							factsTextArea.appendText(resultsList.get(i) + "\n\t");
					}
				}
				
				@Override 
				public void run()
				{
					//local variables
					Statement textAreaQuery;
					ResultSet textAreaResult = null;
					String queryString = "";
					String selectClauseCondition = "";
					final String fromClause = "FROM (city "
							+ "JOIN country ON (city.countryCode = country.countryCode)) "
							+ "JOIN language ON (country.countryCode = language.countryCode)";
					String selectedItem = "";
					String whereClauseEqualsValue = "";
					String whereClause = "";
					String resultString = "";
					int resultInt = 0;
					float resultFloat = 0;
					
					try
					{
						factsTextArea.clear();
						openDBconnection();
						textAreaQuery = dbConnection.createStatement();
						
						//if an item has been selected from the listView
						if (!listView.getSelectionModel().isEmpty())
						{
							//get the selected item from listview
							selectedItem = listView.getSelectionModel().getSelectedItem();
							
							//set whereClauseEqualsValue equal to value of selected item, with leading and trailing whitespace trimmed off
							whereClauseEqualsValue = selectedItem.trim();
						}
						
						/******************** Radio button actions ********************/
						if (citiesRadio.isSelected())
						{
							whereClause = "WHERE cityName = '" + whereClauseEqualsValue + "'";
							factsTextArea.appendText("City: " + whereClauseEqualsValue + "\n");
						}
						else if (countriesRadio.isSelected())
						{
							whereClause = "WHERE name = '" + whereClauseEqualsValue + "'";
							factsTextArea.appendText("Country Name: " + whereClauseEqualsValue + "\n");
						}
						else if (languagesRadio.isSelected())
						{
							whereClause = "WHERE language = '" + whereClauseEqualsValue + "'";
							factsTextArea.appendText("Language: " + whereClauseEqualsValue + "\n");
						}
						
						/******************** Checkbox actions ********************/
						if (countryCheck.isSelected())
						{
							queryString = "SELECT name " + fromClause + " " + whereClause + " ORDER BY name";
							textAreaResult = textAreaQuery.executeQuery(queryString);
							showQueryResults(textAreaResult, countryCheck.getText());
						}
						if (continentCheck.isSelected())
						{	
							queryString = "SELECT DISTINCT continent " + fromClause + " " + whereClause + " ORDER BY continent";
							textAreaResult = textAreaQuery.executeQuery(queryString);
							showQueryResults(textAreaResult, continentCheck.getText());
						}
						if (populationCheck.isSelected())
						{
							if (citiesRadio.isSelected())
								selectClauseCondition = "city.population";
							else if(countriesRadio.isSelected())
								selectClauseCondition = "country.population";
							else if (languagesRadio.isSelected())
								selectClauseCondition = "SUM(country.population)";
							
							queryString = "SELECT " + selectClauseCondition + " " + fromClause + " " + whereClause;
							textAreaResult = textAreaQuery.executeQuery(queryString);
							
							if (textAreaResult.next())
							{
								resultInt = textAreaResult.getInt(1);
								//format resultInt with system's default number format (in this case, with commas as the thousands separators)
								resultString = NumberFormat.getNumberInstance(Locale.getDefault()).format(resultInt);
								factsTextArea.appendText("\nPopulation:\n\t" + resultString);
							}
							else
							{
								factsTextArea.appendText("\nPopulation:\n\tData not available");
							}
						}
						if (lifeExpectancyCheck.isSelected())
						{	
							if (languagesRadio.isSelected())
								selectClauseCondition = "AVG(lifeExpectancy)";
							else
								selectClauseCondition = "lifeExpectancy";
							
							queryString = "SELECT " + selectClauseCondition + " " + fromClause + " " + whereClause;
							textAreaResult = textAreaQuery.executeQuery(queryString);
							
							if (textAreaResult.next())
							{
								resultFloat = textAreaResult.getFloat(1);
								//format resultFloat to 1 decimal place
								resultString = String.format("%.1f", resultFloat);
								factsTextArea.appendText("\nAverage life expectancy:\n\t" + resultString);
							}
							else 
							{
								factsTextArea.appendText("\nAverage life expectancy:\n\tData not available");	
							}
						}
						if (languageCheck.isSelected())
						{		
							queryString = "SELECT DISTINCT language " + fromClause + " " + whereClause + " ORDER BY language";
							textAreaResult = textAreaQuery.executeQuery(queryString);
							showQueryResults(textAreaResult, languageCheck.getText());
						}
						
						closeDBconnection();
					}
					catch (Exception e)
					{
						System.out.println("Error: " + e.getMessage());
					}
				}
			});
			return null;
		}
	}
	
}

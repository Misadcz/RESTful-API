package java2.lab12.client;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.time.Duration;
import javafx.util.converter.LongStringConverter;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class TableViewSample extends Application
{

    private static final Logger logger = LogManager.getLogger(TableViewSample.class);

    final private TableView<Training> table = new TableView<>();

    private final ObservableList<Training> data;

    final HBox hb = new HBox();
    final HBox hbb = new HBox();


    private TextField addExercise;
    private TextField addDay;
    private TextField addWeight;
    private TextField addEntry;
    public static String choice;



    public static void main(String choices, String[] args)
    {
        choice = choices;
        launch(args);
    }


    public TableViewSample()
    {
        data = FXCollections.observableArrayList(getTrainings());

        int i = 0 ;
        for(Training d : data)
        {
            int temp = i;
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter last = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime time = LocalDateTime.parse(d.getToday(), last);

            Duration duration = Duration.between(time, now);
            long seconds = (duration.getSeconds())%60;

            long minutes = (duration.getSeconds())/60;
            if(minutes > 60)
                minutes = minutes %60;
            long hours = (duration.getSeconds())/3600;
            if(hours > 24)
                hours = hours %24;

            long days = ((duration.getSeconds())/86400);

            String result = Long.toString(days) + "d "+ Long.toString(hours) + "h " + Long.toString(minutes) + "m " + Long.toString(seconds) +"s ";
            d.setAgo(result);
            data.set(temp, d);

                return null;
            });
            i++;
        }


    }
    ResourceBundle bundle = ResourceBundle.getBundle("language", new Locale(choice));


    @Override
    public void start(Stage stage)
    {

        Scene scene = new Scene(new Group());
        stage.setTitle(bundle.getString("first_name"));
        stage.setWidth(700);
        stage.setHeight(550);

        final Label label = new Label(bundle.getString("second_name"));
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);
        table.setItems(data);

        TableColumn<Training, String> exerciseCol = constructColumn(bundle.getString("exercise"), "exercise", this::handleOnEditCommitExercise);
        TableColumn<Training, String > dayCol = constructColumn3(bundle.getString("today"), "today", this::handleOnEditCommitDay);
        TableColumn<Training, Long> weightCol = constructColumn2(bundle.getString("weight"), "weight", this::handleOnEditCommitWeight);
        TableColumn<Training, Long> entryCol = constructColumn2(bundle.getString("entry"), "entry", this::handleOnEditCommitEntry);
        TableColumn<Training, String> agoCol = constructColumn(bundle.getString("ago"), "ago", this::handleOnEditCommitAgo);

        TableColumn<Training, Training> actionCol = new TableColumn<>("");
        actionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actionCol.setCellFactory(param -> new TableCell<>()
        {
            private final Button deleteButton = new Button(bundle.getString("delete"));
            @Override
            protected void updateItem(Training training, boolean empty)
            {
                super.updateItem(training, empty);
                if (training == null)
                {
                    setGraphic(null);
                    return;
                }
                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> handleOnDelete(event, training));
            }
        });

        table.getColumns().addAll(exerciseCol,dayCol,weightCol, entryCol,agoCol, actionCol);

        addExercise = constructTextField(exerciseCol, bundle.getString("exercise"));
        addDay = constructTextField(dayCol, bundle.getString("today"));

        addWeight = constructTextField2(weightCol, bundle.getString("weight"));
        addEntry = constructTextField2(entryCol, bundle.getString("entry"));

        final Button addButton = new Button(bundle.getString("add"));
        addButton.setOnAction(this::handleOnAdd);

        hb.getChildren().addAll(addExercise, addWeight, addEntry, addButton);

        hb.setSpacing(7);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, hb);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setScene(scene);

        stage.show();
        stage.setOnCloseRequest(this::exitProgram);
    }

    private TextField constructTextField(TableColumn<Training,String> column, String string)
    {
        TextField result = new TextField();
        result.setPromptText(string);
        result.setMaxWidth(column.getPrefWidth());
        return result;
    }
    private TextField constructTextField2(TableColumn<Training,Long> column, String string)
    {
        TextField result = new TextField();
        result.setPromptText(string);
        result.setMaxWidth(column.getPrefWidth());
        return result;
    }

    private TableColumn<Training, String> constructColumn(String name, String propertyName, EventHandler<CellEditEvent<Training, String>> callback)
    {
        TableColumn<Training, String> col = new TableColumn<>(name);
        col.setMinWidth(100);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit(callback);
        return col;
    }

    private TableColumn<Training, String> constructColumn3(String name, String propertyName, EventHandler<CellEditEvent<Training, String>> callback)
    {
        TableColumn<Training, String> col = new TableColumn<>(name);
        col.setMinWidth(150);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        col.setOnEditCommit(callback);
        return col;
    }


    private TableColumn<Training, Long> constructColumn2(String name, String propertyName, EventHandler<CellEditEvent<Training, Long>> callback) {
        TableColumn<Training, Long> col = new TableColumn<>(name);
        col.setMinWidth(100);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        col.setOnEditCommit(callback);
        return col;
    }


    private void handleOnAdd(@SuppressWarnings("unused") ActionEvent e)
    {
        Training t = new Training();

        t.setExercise(addExercise.getText());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        t.setToday(now.format(formatter));
        t.setWeight(Long.parseLong(addWeight.getText()));
        t.setEntry(Long.parseLong(addEntry.getText()));
        t.setAgo("0h 0m 0s ");

        data.add(t);
        createTraining(t);
        addDay.clear();
        addExercise.clear();
        addWeight.clear();
        addEntry.clear();
    }
    private void handleOnDelete(@SuppressWarnings("unused") ActionEvent event, Training t)
    {
        data.remove(t);
        removeTraining(t);
    }

    private void handleOnEditCommitExercise(CellEditEvent<Training, String> t) {
        Training p = t.getTableView().getItems().get(t.getTablePosition().getRow());
        p.setExercise(t.getNewValue());
        updateTraining(p);
    }

    private void handleOnEditCommitDay(CellEditEvent<Training, String> t) {
        Training p = t.getTableView().getItems().get(t.getTablePosition().getRow());
        p.setToday(t.getNewValue());
        updateTraining(p);
    }

    private void handleOnEditCommitWeight(CellEditEvent<Training, Long> t) {
        Training p = t.getTableView().getItems().get(t.getTablePosition().getRow());
        p.setWeight(t.getNewValue());
        updateTraining(p);
    }

    private void handleOnEditCommitEntry(CellEditEvent<Training, Long> t) {
        Training p = t.getTableView().getItems().get(t.getTablePosition().getRow());
        p.setEntry(t.getNewValue());
        updateTraining(p);
    }

    private void handleOnEditCommitAgo(CellEditEvent<Training, String> t) {
        Training p = t.getTableView().getItems().get(t.getTablePosition().getRow());
        p.setAgo(t.getNewValue());
        updateTraining(p);
    }

    private void exitProgram(WindowEvent evt)
    {
        logger.info(bundle.getString("end"));
        System.exit(0);
    }

    private Collection<Training> getTrainings()
    {
        logger.info(bundle.getString("start"));
        return getClient().getTrainings();
    }

    private void updateTraining(Training training)
    {
        logger.info(bundle.getString("update") + training.getId());
        update_control();
        getClient().updateTrainings(training);
    }

    private void update_control()
    {
        int i = 0 ;
        for(Training d : data)
        {
            int temp = i;

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter last = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                LocalDateTime time = LocalDateTime.parse(d.getToday(), last);

                Duration duration = Duration.between(time, now);
                long seconds = (duration.getSeconds())%60;

                long minutes = (duration.getSeconds())/60;
                if(minutes > 60)
                    minutes = minutes %60;
                long hours = (duration.getSeconds())/3600;
                if(hours > 24)
                    hours = hours %24;

                long days = ((duration.getSeconds())/86400);

                String result = Long.toString(days) + "d "+ Long.toString(hours) + "h " + Long.toString(minutes) + "m " + Long.toString(seconds) +"s ";
                d.setAgo(result);
                data.set(temp, d);

            i++;
        }
    }

    private void removeTraining(Training training)
    {
        logger.info(bundle.getString("remove") + training.getId());
        getClient().removeTraining(training.getId());
    }

    private void createTraining(Training training)
    {
        Long id = getClient().createTraining(training);
        training.setId(id);
        logger.info(bundle.getString("create") + training.getId());
    }

    private static TrainingClient getClient()
    {
        return JAXRSClientFactory.create("http://localhost:8080", TrainingClient.class, Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE).disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)), new HashMap<>(), true);
    }
}
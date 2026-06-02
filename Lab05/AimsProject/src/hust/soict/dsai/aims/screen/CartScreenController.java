package hust.soict.dsai.aims.screen;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import hust.soict.dsai.aims.cart.Cart;
import hust.soict.dsai.aims.media.Media;
import hust.soict.dsai.aims.media.Playable;

public class CartScreenController {
    private Cart cart;

    @FXML private TableView<Media> tblMedia;
    @FXML private TableColumn<Media, String> colMediaTitle;
    @FXML private TableColumn<Media, String> colMediacategory;
    @FXML private TableColumn<Media, Float> colMediaCost;
    @FXML private Button btnPlay;
    @FXML private Button btnRemove;
    @FXML private TextField tfFilter;
    @FXML private RadioButton radioBtnFilterId;
    @FXML private RadioButton radioBtnFilterTitle;
    @FXML private Label lblTotalCost; // total money

    public CartScreenController(Cart cart) {
        super();
        this.cart = cart;
    }

    @FXML
    private void initialize() {
        colMediaTitle.setCellValueFactory(new PropertyValueFactory<Media, String>("title"));
        colMediacategory.setCellValueFactory(new PropertyValueFactory<Media, String>("category"));
        colMediaCost.setCellValueFactory(new PropertyValueFactory<Media, Float>("cost"));
        
        refreshTableAndTotal();

        btnPlay.setVisible(false);
        btnRemove.setVisible(false);

        tblMedia.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Media>() {
                    @Override
                    public void changed(ObservableValue<? extends Media> observable, Media oldValue, Media newValue) {
                        if(newValue != null) {
                            updateButtonBar(newValue);
                        }
                    }
                });
        
        tfFilter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                showFilteredMedia(newValue);
            }
        });
    }

    void updateButtonBar(Media media) {
        btnRemove.setVisible(true);
        if (media instanceof Playable) {
            btnPlay.setVisible(true);
        } else {
            btnPlay.setVisible(false);
        }
    }
    
    @FXML
    void btnRemovePressed(ActionEvent event) {
        Media media = tblMedia.getSelectionModel().getSelectedItem();
        if (media != null) {
            cart.removeMedia(media);
            refreshTableAndTotal(); 
            btnRemove.setVisible(false);
            btnPlay.setVisible(false);
        }
    }
    @FXML
    void btnPlayPressed(ActionEvent event) {
        Media media = tblMedia.getSelectionModel().getSelectedItem();
        if (media != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Playing Media");
            alert.setHeaderText(null);
            alert.setContentText("Playing: " + media.getTitle() + " ... Enjoy your time!");
            alert.showAndWait();
        }
    }
    @FXML
    void btnPlaceOrderPressed(ActionEvent event) {
        if (this.cart.getItemsOrdered().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setContentText("Your cart is empty. Please add items before placing an order!");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Order Success");
        alert.setHeaderText("Thank you for your order!");
        alert.setContentText("An order has been created successfully!");
        alert.showAndWait();

        this.cart.getItemsOrdered().clear(); 
        refreshTableAndTotal();
    }

    @FXML
    void filterModeChanged(ActionEvent event) {
        showFilteredMedia(tfFilter.getText());
    }

    private void refreshTableAndTotal() {
        ObservableList<Media> observableMediaList = FXCollections.observableArrayList(this.cart.getItemsOrdered());
        tblMedia.setItems(observableMediaList);
        
        float total = 0;
        for (Media m : this.cart.getItemsOrdered()) {
            total += m.getCost();
        }
        lblTotalCost.setText(String.format("%.2f $", total));
    }
    
    void showFilteredMedia(String newValue) {
        ObservableList<Media> observableMediaList = FXCollections.observableArrayList(this.cart.getItemsOrdered());
        FilteredList<Media> filteredData = new FilteredList<Media>(observableMediaList, p -> true);
        
        filteredData.setPredicate(media -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            
            if (radioBtnFilterTitle.isSelected()) {
                if (media.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
            } else if (radioBtnFilterId.isSelected()) {
                try {
                    if (media.getId() == Integer.parseInt(lowerCaseFilter)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        });
        
        tblMedia.setItems(filteredData);
    }
}
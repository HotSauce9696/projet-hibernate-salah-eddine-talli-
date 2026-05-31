/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplicationproduit;
import classes.Produit;
import classes.ProduitService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {
    
    @FXML private TextField codeField;
    @FXML private TextField libelleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField quantiteField;
    @FXML private CheckBox disponibiliteCheckBox;
    @FXML private TextField idField;
    @FXML private TextField searchField;
    
    @FXML private TableView<Produit> produitTableView;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String> colCode;
    @FXML private TableColumn<Produit, String> colLibelle;
    @FXML private TableColumn<Produit, String> colType;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private TableColumn<Produit, Boolean> colDisponibilite;
    
    private ObservableList<Produit> produitList;
    private FilteredList<Produit> filteredList;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ProduitService.openSession();

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
            colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteEnStock"));
            colDisponibilite.setCellValueFactory(cellData -> 
                new SimpleBooleanProperty(cellData.getValue().isDisponibilite()));

            loadProduits();

            setupSearchFilter();

            produitTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectProduit(newValue));

            loadTypes();
            
            System.out.println("Controller initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("Error initializing controller: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }
    }
    
    private void loadProduits() {
        try {
            List<Produit> produits = ProduitService.getAllProduits();
            if (produits != null && !produits.isEmpty()) {
                produitList = FXCollections.observableArrayList(produits);
                produitTableView.setItems(produitList);
                System.out.println("Loaded " + produitList.size() + " products");
            } else {
                produitList = FXCollections.observableArrayList();
                produitTableView.setItems(produitList);
                System.out.println("No products found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            produitList = FXCollections.observableArrayList();
            produitTableView.setItems(produitList);
        }
    }
    
    private void loadTypes() {
        try {
            List<String> types = ProduitService.getDistinctTypes();
            if (types != null && !types.isEmpty()) {
                typeComboBox.setItems(FXCollections.observableArrayList(types));
                System.out.println("Loaded " + types.size() + " types");
            }
        } catch (Exception e) {
            e.printStackTrace();
            typeComboBox.getItems().addAll("Fruits", "Légumes", "Produits laitiers", "Boulangerie", "Boissons");
        }
    }
    
    private void setupSearchFilter() {
        if (produitList != null) {
            filteredList = new FilteredList<>(produitList, p -> true);
            produitTableView.setItems(filteredList);
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(produit -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return produit.getLibelle().toLowerCase().contains(lowerCaseFilter) ||
                           produit.getType().toLowerCase().contains(lowerCaseFilter);
                });
            });
        }
    }
    
    private void selectProduit(Produit produit) {
        if (produit != null) {
            idField.setText(String.valueOf(produit.getId()));
            codeField.setText(produit.getCode());
            libelleField.setText(produit.getLibelle());
            typeComboBox.setValue(produit.getType());
            quantiteField.setText(String.valueOf(produit.getQuantiteEnStock()));
            disponibiliteCheckBox.setSelected(produit.isDisponibilite());
        }
    }
    
    private void clearForm() {
        idField.clear();
        codeField.clear();
        libelleField.clear();
        typeComboBox.setValue(null);
        quantiteField.clear();
        disponibiliteCheckBox.setSelected(false);
        produitTableView.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void handleAjouter() {
        if (validateFields()) {
            try {
                Produit produit = new Produit();
                produit.setCode(codeField.getText());
                produit.setLibelle(libelleField.getText());
                produit.setType(typeComboBox.getValue());
                produit.setQuantiteEnStock(Integer.parseInt(quantiteField.getText()));
                produit.setDisponibilite(disponibiliteCheckBox.isSelected());
                
                int result = ProduitService.ajouterProduit(produit);
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès!");
                    loadProduits();
                    clearForm();
                    setupSearchFilter();
                    loadTypes();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout!");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être un nombre valide!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleModifier() {
    if (codeField.getText().isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à modifier!");
        return;
    }
    
    if (validateFields()) {
        try {
            Produit produit = new Produit();
            produit.setCode(codeField.getText());
            produit.setLibelle(libelleField.getText());
            produit.setType(typeComboBox.getValue());
            produit.setQuantiteEnStock(Integer.parseInt(quantiteField.getText()));
            produit.setDisponibilite(disponibiliteCheckBox.isSelected());
            
            System.out.println("Attempting to modify product with code: " + produit.getCode());

            ProduitService.modifierProduit(produit);

            loadProduits();
            
            setupSearchFilter();

            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit modifié avec succès!");
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être un nombre valide!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
        }
    }
}
    
    @FXML
    private void handleSupprimer() {
        if (idField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à supprimer!");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le produit");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                int id = Integer.parseInt(idField.getText());
                ProduitService.supprimerProduit(id);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès!");
                loadProduits();
                clearForm();
                setupSearchFilter();
                loadTypes();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleActualiser() {
        loadProduits();
        clearForm();
        searchField.clear();
        setupSearchFilter();
        loadTypes();
        showAlert(Alert.AlertType.INFORMATION, "Info", "Liste actualisée!");
    }
    
    @FXML
    private void handleClearSearch() {
        searchField.clear();
    }
    
    private boolean validateFields() {
        if (codeField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le code est obligatoire!");
            return false;
        }
        if (libelleField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le libellé est obligatoire!");
            return false;
        }
        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le type est obligatoire!");
            return false;
        }
        if (quantiteField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La quantité est obligatoire!");
            return false;
        }
        return true;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
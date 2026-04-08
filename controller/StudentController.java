public class StudentController {

    // CHAMPS FXML 

    // Champ texte pour le prénom (lié au FXML avec fx:id)
    @FXML private TextField firstNameField;

    // Champ texte pour le nom
    @FXML private TextField lastNameField;

    // Table qui affiche la liste des étudiants
    @FXML private TableView<Student> studentTable;

    // Colonnes de la table
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;

    // DAO pour accéder à la base de données
    private StudentDAO studentDAO = new StudentDAO();

    //INITIALISATION

    // Méthode appelée automatiquement au chargement de la vue
    @FXML
    public void initialize() {

        // Associe chaque colonne à un attribut de la classe Student
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Listener : quand on sélectionne une ligne dans la table
        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {

                // Si une ligne est sélectionnée
                if (newValue != null) {

                    // Remplit les champs texte avec les données
                    firstNameField.setText(newValue.getFirstName());
                    lastNameField.setText(newValue.getLastName());
                }
            }
        );

        try {
            // Charge les données au démarrage
            loadStudents();
        } catch (Exception e) {
            showError(e);
        }
    }

    // CREATE

    @FXML
    public void handleAdd() {

        try {
            // Vérifie que les champs ne sont pas vides
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                showAlert("Champs obligatoires !");
                return; // stop la méthode
            }

            // Création d’un nouvel étudiant
            Student s = new Student();

            // Récupération des valeurs saisies
            s.setFirstName(firstNameField.getText());
            s.setLastName(lastNameField.getText());

            // Enregistrement en base
            studentDAO.create(s);

            // Vide les champs après ajout
            clearFields();

            // Rafraîchit la table
            loadStudents();

        } catch (Exception e) {
            showError(e);
        }
    }

    // READ 

    public void loadStudents() throws Exception {

        // Récupère les étudiants depuis la base
        ObservableList<Student> list =
                FXCollections.observableArrayList(studentDAO.readAll());

        // Met la liste dans la table
        studentTable.setItems(list);
    }

    // UPDATE 

    @FXML
    public void handleUpdate() {

        // Récupère l'étudiant sélectionné
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Si rien n'est sélectionné
        if (selected == null) {
            showAlert("Aucun étudiant sélectionné !");
            return;
        }

        try {
            // Vérifie les champs
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                showAlert("Champs obligatoires !");
                return;
            }

            // Mise à jour des données
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());

            // Update en base
            studentDAO.update(selected);

            // Rafraîchit la table
            loadStudents();

            // Vide les champs
            clearFields();

        } catch (Exception e) {
            showError(e);
        }
    }

    // DELETE 

    @FXML
    public void handleDelete() {

        // Récupère l'étudiant sélectionné
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Si rien sélectionné
        if (selected == null) {
            showAlert("Aucun étudiant sélectionné !");
            return;
        }

        try {
            // Demande confirmation avant suppression
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Supprimer cet étudiant ?");

            Optional<ButtonType> result = confirm.showAndWait();

            // Si l'utilisateur confirme
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Suppression en base via l'ID
                studentDAO.delete(selected.getId());

                // Rafraîchit la table
                loadStudents();

                // Vide les champs
                clearFields();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // UTIL

    // Vide les champs texte
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
    }

    // Affiche une alerte simple (message utilisateur)
    private void showAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Affiche une erreur technique
    private void showError(Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        // Log console pour debug
        e.printStackTrace();
    }
}
public class StudentController {

    // Champs de saisie pour le prénom et le nom
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    // TableView pour afficher les étudiants
    @FXML private TableView<Student> studentTable;

    // Colonnes de la table
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;

    // DAO (Data Access Object) pour gérer les opérations base de données
    private StudentDAO studentDAO = new StudentDAO();

    // Méthode appelée automatiquement au chargement de la vue
    @FXML
    public void initialize() {

        // Associe la colonne ID à la propriété "id" de la classe Student
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Associe la colonne prénom à "firstName"
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        // Associe la colonne nom à "lastName"
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        try {
            // Charge les étudiants depuis la base et les affiche dans la table
            loadStudents();
        } catch (Exception e) {
            // Affiche l'erreur dans la console
            e.printStackTrace();
        }
    }

    // CREATE 
    @FXML
    public void handleAdd() {
        try {
            // Création d'un nouvel objet Student
            Student s = new Student();

            // Récupère les valeurs des champs texte et les assigne à l'objet
            s.setFirstName(firstNameField.getText());
            s.setLastName(lastNameField.getText());

            // Appelle le DAO pour insérer l'étudiant en base
            studentDAO.create(s);

            // Vide les champs après ajout
            clearFields();

            // Recharge la table pour afficher le nouvel étudiant
            loadStudents();

        } catch (Exception e) {
            // Affiche l'erreur
            e.printStackTrace();
        }
    }

    //  READ
    public void loadStudents() throws Exception {

        // Récupère tous les étudiants depuis la base et les met dans la table
        studentTable.getItems().setAll(studentDAO.readAll());
    }

    //  UPDATE 
    @FXML
    public void handleUpdate() {

        // Récupère l'étudiant sélectionné dans la table
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Si rien n'est sélectionné, on quitte la méthode
        if (selected == null) return;

        try {
            // Met à jour les données de l'objet avec les champs texte
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());

            // Met à jour l'étudiant en base
            studentDAO.update(selected);

            // Recharge la table pour voir les modifications
            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE 
    @FXML
    public void handleDelete() {

        // Récupère l'étudiant sélectionné
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Si rien n'est sélectionné, on ne fait rien
        if (selected == null) return;

        try {
            // Supprime l'étudiant en base via son ID
            studentDAO.delete(selected.getId());

            // Recharge la table
            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SELECTION 
    // Méthode appelée quand on clique sur une ligne (si liée dans le FXML)
    @FXML
    public void handleRowSelect() {

        // Récupère l'étudiant sélectionné
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Si un étudiant est sélectionné
        if (selected != null) {

            // Remplit les champs texte avec ses données
            firstNameField.setText(selected.getFirstName());
            lastNameField.setText(selected.getLastName());
        }
    }

    // UTIL 
    private void clearFields() {

        // Vide le champ prénom
        firstNameField.clear();

        // Vide le champ nom
        lastNameField.clear();
    }
}
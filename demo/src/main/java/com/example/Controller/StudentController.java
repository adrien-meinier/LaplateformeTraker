package com.example.Controller;
public class StudentController {

    // FXML fields (linked to UI components)
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;

    // TableView to display students
    @FXML private TableView<Student> studentTable;

    // Table columns
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;

    // New date columns
    @FXML private TableColumn<Student, LocalDate> colBirthDate;
    @FXML private TableColumn<Student, LocalDate> colCreationDate;
    @FXML private TableColumn<Student, LocalDate> colLastModifiedDate;

    // DAO to interact with database
    private StudentDAO studentDAO = new StudentDAO();

    // Method automatically called when the view is loaded
    @FXML
    public void initialize() {

        // Bind table columns to Student properties
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Bind new date columns
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        colCreationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        colLastModifiedDate.setCellValueFactory(new PropertyValueFactory<>("lastModifiedDate"));

        // Listener triggered when a row is selected in the table
        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {

                // If a student is selected
                if (newValue != null) {

                    // Fill text fields with selected student data
                    firstNameField.setText(newValue.getFirstName());
                    lastNameField.setText(newValue.getLastName());

                    // Set birth date in DatePicker
                    birthDatePicker.setValue(newValue.getBirthDate());
                }
            }
        );

        try {
            // Load students when UI starts
            loadStudents();
        } catch (Exception e) {
            showError(e);
        }
    }

    // CREATE: Add a new student
    @FXML
    public void handleAdd() {

        try {
            // Validate required fields
            if (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                birthDatePicker.getValue() == null) {

                showAlert("All fields are required!");
                return;
            }

            // Create new Student object
            Student s = new Student();

            // Set values from UI
            s.setFirstName(firstNameField.getText());
            s.setLastName(lastNameField.getText());
            s.setBirthDate(birthDatePicker.getValue());

            // Set creation date to current date
            s.setCreationDate(LocalDate.now());

            // No modification yet
            s.setLastModifiedDate(null);

            // Save to database
            studentDAO.create(s);

            // Clear input fields
            clearFields();

            // Refresh table
            loadStudents();

        } catch (Exception e) {
            showError(e);
        }
    }

    // READ: Load all students from database
    public void loadStudents() throws Exception {

        // Convert list to ObservableList for JavaFX
        ObservableList<Student> list =
                FXCollections.observableArrayList(studentDAO.readAll());

        // Set data into table
        studentTable.setItems(list);
    }

    // UPDATE: Modify selected student
    @FXML
    public void handleUpdate() {

        // Get selected student
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Check if a student is selected
        if (selected == null) {
            showAlert("No student selected!");
            return;
        }

        try {
            // Validate input fields
            if (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                birthDatePicker.getValue() == null) {

                showAlert("All fields are required!");
                return;
            }

            // Update student data
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());
            selected.setBirthDate(birthDatePicker.getValue());

            // Update last modified date
            selected.setLastModifiedDate(LocalDate.now());

            // Save changes to database
            studentDAO.update(selected);

            // Refresh table
            loadStudents();

            // Clear input fields
            clearFields();

        } catch (Exception e) {
            showError(e);
        }
    }

    // DELETE: Remove selected student
    @FXML
    public void handleDelete() {

        // Get selected student
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        // Check selection
        if (selected == null) {
            showAlert("No student selected!");
            return;
        }

        try {
            // Confirmation dialog
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setContentText("Delete this student?");

            Optional<ButtonType> result = confirm.showAndWait();

            // If user confirms deletion
            if (result.isPresent() && result.get() == ButtonType.OK) {

                // Delete from database using ID
                studentDAO.delete(selected.getId());

                // Refresh table
                loadStudents();

                // Clear fields
                clearFields();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // Clear all input fields
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        birthDatePicker.setValue(null);
    }

    // Show warning message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show error message and print stack trace
    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}
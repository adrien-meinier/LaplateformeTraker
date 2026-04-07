public class StudentController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;

    private StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        try {
            loadStudents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CREATE
    @FXML
    public void handleAdd() {
        try {
            Student s = new Student();
            s.setFirstName(firstNameField.getText());
            s.setLastName(lastNameField.getText());

            studentDAO.create(s);

            clearFields();
            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // READ
    public void loadStudents() throws Exception {
        studentTable.getItems().setAll(studentDAO.readAll());
    }

    //  UPDATE
    @FXML
    public void handleUpdate() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) return;

        try {
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());

            studentDAO.update(selected);
            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  DELETE
    @FXML
    public void handleDelete() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) return;

        try {
            studentDAO.delete(selected.getId());
            loadStudents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  Remplir les champs quand on clique sur une ligne
    @FXML
    public void handleRowSelect() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            firstNameField.setText(selected.getFirstName());
            lastNameField.setText(selected.getLastName());
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
    }
}

module com.example.learningjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.learningjavafx to javafx.fxml;
    exports com.example.learningjavafx;
}
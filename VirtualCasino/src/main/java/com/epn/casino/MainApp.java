package com.epn.casino;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de arranque para la interfaz gráfica JavaFX.
 * Actúa también como gestor de escenas (Router) para la navegación.
 */
public class MainApp extends Application {

    // Mantenemos una referencia estática a la ventana principal para poder cambiar su contenido
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Premium Virtual Casino - EPN");
        primaryStage.setResizable(false); // Evitamos que se deforme el diseño

        // Arrancamos el sistema cargando la vista de Login
        switchScene("/com/epn/casino/views/login.fxml");
        primaryStage.show();
    }

    /**
     * Método de utilidad estático para cambiar de pantalla de forma sencilla.
     */
    public static void switchScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
    }

    /**
     * Método avanzado de enrutamiento.
     * Carga una vista y devuelve su FXMLLoader para permitirnos inyectar datos (como el User) al controlador.
     */
    public static FXMLLoader getLoaderAndSwitch(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        return loader;
    }

    public static void main(String[] args) {
        // Este comando arranca el hilo de JavaFX
        launch();
    }
}
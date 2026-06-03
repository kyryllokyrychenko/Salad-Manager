package ui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;

public class SaladCard extends VBox {

    public SaladCard(String name, String imagePath,
                     Runnable onOpen, Runnable onDelete) {
        super(8);
        getStyleClass().add("salad-card");
        setPrefWidth(180);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(12));

        ImageView imageView = buildImageView(imagePath);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        Button openBtn   = new Button("Відкрити");
        Button deleteBtn = new Button("Видалити");
        openBtn.getStyleClass().add("btn-primary");
        deleteBtn.getStyleClass().add("btn-danger-small");
        openBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);

        openBtn.setOnAction(e -> onOpen.run());
        deleteBtn.setOnAction(e -> onDelete.run());

        getChildren().addAll(imageView, nameLabel, openBtn, deleteBtn);
    }

    private ImageView buildImageView(String imagePath) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(156);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(false);
        imageView.getStyleClass().add("card-image");

        if (imagePath != null && !imagePath.isBlank()) {
            try {
                imageView.setImage(new Image(new FileInputStream(imagePath)));
            } catch (Exception e) {
                setPlaceholder(imageView);
            }
        } else {
            setPlaceholder(imageView);
        }
        return imageView;
    }

    private void setPlaceholder(ImageView imageView) {
        imageView.setStyle("-fx-background-color: #e8f5e9;");
    }
}
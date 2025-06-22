package com.example.controller.submenuView;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.App;
import com.example.model.Event;
import com.example.model.ProductType;
import com.example.utils.DataBase;
import com.example.utils.DoubleToCop;
import com.example.utils.ImageCropper;
import com.example.utils.PopupMessage;
import com.example.utils.ProductIcon;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class ProductRegister extends SubmenuView{
    @FXML private ImageView icon;
    @FXML private TextField priceInput;
    @FXML private TextArea nameInput;
    @FXML private ComboBox<String> supplierSelector;
    @FXML private ComboBox<ProductType> typeSelector;
    @FXML private Label name;
    @FXML private Label price;
    @FXML private Label type;
    @FXML private Label supplier;
    @FXML private Label icons;
    @FXML private Button upload;
    @FXML private Pane iconContainer;
    @FXML private HBox iconPreviewContainer;
    @FXML private Pane iconUpload;

    private ImageCropper imageCropper;
    private FileChooser fileChooser;
    private ArrayList<IconPreview> iconPreviews;
    private PopupMessage successMessage;

    private final String pathToIconsFolder;

    private String insertProductIconSQL;
    private String insertProductSQL;
    private String getSuppliersSQL;

    //ProductInfo
    private String productName;
    private double productPrice;
    private int productSupplier;
    private String productType;
    private ProductIcon productIcon;

    private Event onProductAdded;

    public ProductRegister(){
        initUI("ProductRegister");
        initQuerys();
        initComponents();
        makeConnections();
        reset();

        pathToIconsFolder = "src/main/resources/com/example/storage/producticons";
        productType = "Giftcard";
    }
    
    public void reloadSuppliers(){
        supplierSelector.getItems().clear();

        try{
            ResultSet result = DataBase.executeQuery(getSuppliersSQL);
            boolean hasNext = result.next();

            while (hasNext) {
                supplierSelector.getItems()
                    .add(result.getString("name"));
                hasNext = result.next();
            }

            if (supplierSelector.getItems().size() >0){
                supplierSelector.getSelectionModel().select(0);
            }
        }catch(SQLException e){}
    }

    private void makeConnections(){
        setOnMouseClicked(e->{
            requestFocus();
        });

        iconUpload.setOnMouseClicked(e->{
            File imageFile = fileChooser.showOpenDialog(App.getStage());

            if (imageFile==null){return;}
            Image image = new Image(imageFile.toURI().toString());

            imageCropper.setImage(image);
            imageCropper.show();
        });

        priceInput.focusedProperty().addListener((x)->{
            boolean focused = priceInput.isFocused();
            if (focused){
                priceInput.setText("");
            }else{checkPriceInput();}
        });

        nameInput.focusedProperty().addListener((x)->{
            boolean focused = nameInput.isFocused();
            if (focused){
                nameInput.setText("");
            }else{checkNameInput();}
        });
    
        supplierSelector.getSelectionModel().selectedIndexProperty().addListener((x)->{
            productSupplier = supplierSelector.getSelectionModel().getSelectedIndex();
            supplier.setText(supplierSelector.getSelectionModel().getSelectedItem());
        });
        
        typeSelector.getSelectionModel().selectedIndexProperty().addListener((x)->{
            String a = typeSelector.getSelectionModel()
            .getSelectedItem().toString();

            productType = a;
            type.setText(a);
        });
    
        imageCropper.setOnCropSuccess(()-> {
            WritableImage image = imageCropper.getCroppedImage();
            productIcon.add(image);

            int iconAmount = productIcon.getIconAmount();
            if (iconAmount == 1){
                icon.setImage(productIcon.getIcon(0, 0));
            }else{
                IconPreview preview = new 
                    IconPreview(productIcon.getIcon(iconAmount - 1, 1));

                iconPreviewContainer.getChildren().add(preview);
            }

            icons.setText(iconAmount + "");
        });
    
        successMessage.addOption("Ok", "", e->{
            successMessage.hide();
            if(onProductAdded != null){onProductAdded.call();}

            icon.setImage(null);
            Iterator<IconPreview> it = iconPreviews.iterator();
            while(it.hasNext()){
                IconPreview current = it.next();
                iconPreviewContainer.getChildren().remove(current);
            }
        });

        upload.setOnMouseClicked(e->{
            try{
                PreparedStatement statement = DataBase
                    .getConnection().prepareStatement(insertProductSQL,
                    Statement.RETURN_GENERATED_KEYS);

                statement.setInt(1, productSupplier +1);
                statement.setString(2, productName);
                statement.setDouble(3, productPrice);
                statement.setString(4, productType);

                int rows = statement.executeUpdate();
                if (rows >0){
                    successMessage.setMessage("Product Registerd!");

                    ResultSet generated = statement.getGeneratedKeys();
                    if (generated.next()){
                        int productId = generated.getInt(1);
                        ArrayList<ProductIcon.IconContainer> 
                            containers = productIcon.makeFiles(pathToIconsFolder, productName);

                        for (ProductIcon.IconContainer a : containers){
                            int iconIndex = a.getIconIndex();

                            ArrayList<ProductIcon.Icon> b = a.getIcons();
                            for (ProductIcon.Icon x : b){
                                PreparedStatement insertIconStatement = DataBase.getConnection()
                                    .prepareStatement(insertProductIconSQL);
                                insertIconStatement.setInt(1, productId);
                                insertIconStatement.setString(2, x.getPath());
                                insertIconStatement.setInt(3, iconIndex);
                                insertIconStatement.setString(4, x.getResolution());

                                insertIconStatement.executeUpdate();
                            }
                        }
                        reset();
                        //if(onProductAdded != null){onProductAdded.call();}

                        successMessage.show();
                    }
                }else{
                    //successMessage.setMessage("Something went wrong:(");}
                    //successMessage.show();
                }
            }catch(SQLException ex){ex.printStackTrace();}
            
        });
    }

    private void initQuerys(){
        getSuppliersSQL = "SELECT name FROM supplier";
        insertProductSQL = "INSERT INTO product (supplierid, name, price, type) VALUES (?, ?, ?, ?)";
        insertProductIconSQL = "INSERT INTO producticon (productid, url, icon_index, resolution) VALUES (?, ? ,?, ?)";
    }

    private void reset(){
        nameInput.setText("New Product");
        priceInput.setText("0");
        typeSelector.getSelectionModel().select(0);
        supplierSelector.getSelectionModel().select(0);
        productIcon = new ProductIcon();

        checkPriceInput();
        checkNameInput();
        removeIconPreviews();
    }

    private void removeIconPreviews(){
        for (IconPreview x : iconPreviews){
            iconPreviewContainer.getChildren().remove(x);
        }
    }

    private void checkPriceInput(){
        String x = priceInput.getText();        
        try{
            productPrice = Double.parseDouble(x);
            price.setText(DoubleToCop.get(productPrice));
        }catch(NumberFormatException e){
            priceInput.setText(DoubleToCop.get(productPrice));}

        priceInput.setText(DoubleToCop.get(productPrice));
    }

    private void checkNameInput(){
        String x = nameInput.getText();
        if (!x.isBlank()){
            productName = x;
            name.setText(nameInput.getText());
        }
        nameInput.setText(productName);
    }

    private void initComponents(){
        //image cropper
        imageCropper = new ImageCropper();
        imageCropper.hide();
        imageCropper.setRadius(.08);
        getChildren().add(imageCropper);

        //file chooser
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files, ","*.png", "*.jpg", "*.jpeg")
            );
        //
        typeSelector.getItems().addAll(
            ProductType.Giftcard,
            ProductType.Skin,
            ProductType.Product,
            ProductType.Licence,
            ProductType.Game_Key,
            ProductType.Other
        );
        typeSelector.getSelectionModel().select(0);
        //
        iconPreviews = new ArrayList<>();
        //

        Rectangle clip = new Rectangle();
        clip.setWidth(260);
        clip.setHeight(260);
        double arc = 260 * .08 * 2;

        clip.setArcWidth(arc);
        clip.setArcHeight(arc);

        iconContainer.setClip(clip);

        //
        successMessage = new PopupMessage(this, "Product Registered!", 1.5);
    }

    public void setOnProductAdded(Event e){
        this.onProductAdded = e;
    }

    private static class IconPreview extends StackPane{
        public IconPreview(WritableImage image){
            setPrefSize(150, 150);
            ImageView view = new ImageView(image);

            getChildren().add(view);

            Rectangle clip = new Rectangle();
            clip.setWidth(150);
            clip.setHeight(150);
            double arc = 150 * .08 * 2;

            clip.setArcWidth(arc);
            clip.setArcHeight(arc);

            setClip(clip);
        }
    }
}

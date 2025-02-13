package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;

import org.jsoup.Jsoup;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class ChatCadController {


   @FXML
   private ImageView image;

   @FXML
   private Label label;

   @FXML
   private TextFlow text;

   @FXML
   private Label missedM;

    public void setImage(byte[] i){
        if(i != null){

            ByteArrayInputStream inputStream = new ByteArrayInputStream(i);
            Image image = new Image(inputStream);
            this.image.setImage(image);
        }
    }   
    public void setLabel(String s){
        if(s != null)
        label.setText(s);
    }


    public void setText(String content) {
        // System.out.println("Original Content1: " + content);
        if (content != null) {
            text.getChildren().clear();
            displayFormattedContent(content);
        }
    }

    private boolean isHtml(String content) {
        return content.trim().matches("(?i).*<(html|body|head|p|b|i|u|span|strong|em|div|br)[^>]*>.*");
    }
    
 /*  
    private String cleanHtml(String content) {

        content = content.replaceAll("(?i)<head[^>]*>.*?</head>", "");

        content = content.replaceAll("(?i)</?(html|body)[^>]*>", "");
        return content.trim();
    }
    

    private void displayFormattedContent(String content) {
        text.getChildren().clear(); 
        
        if (isHtml(content)) {
        
            content = cleanHtml(content); 
            TextFlow formattedContent = HtmlToTextFlowConverter.convertHtmlToTextFlow(content);
    
            StringBuilder visibleText = new StringBuilder();
            for (Node node : formattedContent.getChildren()) {
                if (node instanceof Text) {
                    ((Text) node).setWrappingWidth(200);
                    Text textNode = (Text) node;
                    String nodeText = textNode.getText();
    
                    if (visibleText.length() + nodeText.length() <= 7) {
                        visibleText.append(nodeText);
                    } else {
                        int remaining = 7 - visibleText.length();
                        visibleText.append(nodeText, 0, remaining).append("...");
                        break;
                    }
                }
            }
    
            Text truncatedPreview = new Text(visibleText.toString());
            truncatedPreview.setWrappingWidth(200);
            text.getChildren().clear();
            text.getChildren().add(truncatedPreview);
            
            
        } else {
            Text plainText = new Text(content.length() > 7 ? content.substring(0, 7) + "..." : content);
            plainText.setWrappingWidth(200);
            text.getChildren().add(plainText);
        }
    }
    
   */ 

  private void displayFormattedContent(String content) {
    text.getChildren().clear();
    
   // System.out.println("Original Content: " + content);

    String plainText;
    if (isHtml(content)) {
        plainText = Jsoup.parse(content).text();
        // System.out.println("Content after Jsoup parse: " + plainText);
    } else {
        plainText = content;
    }

    if (plainText.isEmpty()) {
        System.out.println("Parsed content is empty.");
    }

    String preview = plainText.length() > 7 ? plainText.substring(0, 7) + "..." : plainText;

    Text plainTextNode = new Text(preview);
    plainTextNode.setWrappingWidth(200);
    text.getChildren().add(plainTextNode);

    text.setOnMouseMoved(event -> {
        String fullMessageContent = plainText;  
        Text fullMessage = new Text(fullMessageContent);
        fullMessage.setWrappingWidth(300);
        text.getChildren().setAll(fullMessage);
    });

    text.setOnMouseExited(event -> {
        Text truncatedText = new Text(preview);
        truncatedText.setWrappingWidth(200);
        text.getChildren().setAll(truncatedText);
    });
}

    @FXML
    private void initialize() {
         
                missedM.setFont(Font.font("Arial", 18)); // Stylish font
        missedM.setStyle("-fx-text-fill: linear-gradient(to right, #ff416c, #ff4b2b);"); // Gradient text color

        // Add a slight shadow to make it pop
        DropShadow textShadow = new DropShadow();
        textShadow.setColor(Color.rgb(255, 75, 43, 0.6)); // Soft glow
        textShadow.setRadius(8);
        textShadow.setSpread(0.3);
        missedM.setEffect(textShadow);

        Circle clip = new Circle();
        clip.setRadius(20); 
        clip.setCenterX(20);
        clip.setCenterY(20);
        image.setClip(clip);
    }


    /**
     * @return ImageView return the image
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * @return Label return the label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * @return Label return the missedM
     */
    public int getMissedM() {
        return Integer.valueOf(missedM.getText());
    }

    /**
     * @param missedM the missedM to set
     */
    public void setMissedM(int missedM) {
        if(missedM ==0){
        this.missedM.setText("");

        }else{

        this.missedM.setText("+" + String.valueOf(missedM));
        }
    }

}

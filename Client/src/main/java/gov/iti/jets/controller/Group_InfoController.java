package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.ChatDTO;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Amina
 */
public class Group_InfoController implements Initializable {

    @FXML
    private Button exitBtn;
    @FXML
    private ImageView groupImage;
    @FXML
    private Label groupNameLabel;
    @FXML
    private Label creatorLabel;
    @FXML
    private Label memberCountLabel;
    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    @FXML
    private ListView<UserDTO> membersContainer;
    @FXML
    private Button addMemberButton;
    @FXML
    private ImageView memberImage;
    @FXML
    private Label memberName;

    @FXML
    private Button exitButton;
    @FXML
    private VBox groupContainer;

     @FXML
    private TextField phoneNumberField;

    private ChatDTO chatDTO;
    private UserChatDAOInterface userChatDAO;

    private UserDTO currentUser; 

    private ChatDAOInterface chatDAO;
    private UserDAOInterface userDAO;
    private ContactDAOInterface contactDAO;

public void setCurrentUser(UserDTO user) {
    this.currentUser = user;
}


    public void setChatDTO(ChatDTO chatDTO) {
        this.chatDTO = chatDTO;
    
     
        groupNameLabel.setText(chatDTO.getChatName());
    
        if (chatDTO.getChatPicture() != null) {
            groupImage.setImage(new Image(new ByteArrayInputStream(chatDTO.getChatPicture())));
        }

        try {
         
            List<Integer> participantIds = userChatDAO.getChatParticipants(chatDTO.getChatID());
    
            if (!participantIds.isEmpty()) {
                // The first participant is considered the creator
                int creatorId = participantIds.get(0);
                UserDTO creator = userChatDAO.getUserById(creatorId);
                if (creator != null) {
                    creatorLabel.setText("Creator: " + creator.getName());
                } else {
                    creatorLabel.setText("Creator: Not Found");
                }
            } else {
                creatorLabel.setText("Creator: Not Found");
            }
            memberCountLabel.setText("Members: " + participantIds.size());
     
            contacts.clear();
            for (int userId : participantIds) {
                UserDTO user = userChatDAO.getUserById(userId);
                if (user != null) {
                    contacts.add(user);
                }
            }
    
        
            membersContainer.setItems(contacts);
    
        } catch (RemoteException e) {
            e.printStackTrace();
            creatorLabel.setText("Creator: Error");
        }
    }

    //show alert  to insure exsit
    @FXML
private void handleExitGroup() {

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirm Exit");
    alert.setHeaderText("Are you sure you want to leave this group?");
    alert.setContentText("You will no longer be able to send messages in this group.");

  
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
        exitGroup();
    }
}

private void exitGroup() {
    try {
        if (currentUser == null) {
            System.out.println("Error: Current user is null");
            return;
        }

        int currentUserId = currentUser.getUserID();

        boolean isRemoved = userChatDAO.removeUserFromChat(chatDTO.getChatID(), currentUserId);

        if (isRemoved) {
            contacts.removeIf(user -> user.getUserID() == currentUserId);
            membersContainer.setItems(contacts);
        
            memberCountLabel.setText("Members: " + contacts.size());

        
            if (contacts.isEmpty()) {
                int remainingMembers = userChatDAO.getChatMembersCount(chatDTO.getChatID());
                if (remainingMembers == 0) {
                    chatDAO.deleteChat(chatDTO.getChatID());
                }
              //  removeChatFromList(chatDTO.getChatID()); 
            }

      
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("You have left the group successfully.");
            successAlert.showAndWait();

            closeWindow();
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to leave the group. Please try again.");
            errorAlert.showAndWait();
        }
    } catch (RemoteException e) {
        e.printStackTrace();
    }
}


@FXML
private void addmember(ActionEvent event) {
    String phoneNumber = phoneNumberField.getText().trim();
    System.out.println("DEBUG: Entered phone number → " + phoneNumber);

    if (phoneNumber.isEmpty()) {
        showAlert("Error", "Please enter a phone number.", Alert.AlertType.ERROR);
        return;
    }

    try {
        UserDTO newUser = userDAO.findUserByPhone(phoneNumber);
        System.out.println("DEBUG: User found → " + (newUser != null ? newUser.getName() : "NULL"));

        if (newUser == null) {
            showAlert("Error", "User not found.", Alert.AlertType.ERROR);
            return;
        }

      
        List<UserDTO> userContacts = contactDAO.findAllContactsACCEPTED(currentUser.getPhone());


        if (contacts.stream().anyMatch(user -> user.getUserID() == newUser.getUserID())) {
            showAlert("Error", "User is already in the group.", Alert.AlertType.ERROR);
            return;
        }
    
        if (userContacts.stream().noneMatch(user -> user.getUserID() == newUser.getUserID())) {
            showAlert("Error", "User must be in your contacts to add them to the group.", Alert.AlertType.ERROR);
            return;
        }


        boolean isAdded = userChatDAO.addUserToChat(chatDTO.getChatID(), newUser.getUserID());
        System.out.println("DEBUG: User added to chat → " + isAdded);
        if (isAdded) {
            contacts.add(newUser);
            membersContainer.setItems(contacts);
            memberCountLabel.setText("Members: " + contacts.size());
            showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to add user. Try again.", Alert.AlertType.ERROR);
        }
    } catch (RemoteException e) {
        e.printStackTrace();
        showAlert("Error", "An error occurred.", Alert.AlertType.ERROR);
    }
}


private void showAlert(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}



    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        RMIConfig p = null;
        try {
      
            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI());
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
        } catch (JAXBException | URISyntaxException ex) {
            ex.printStackTrace();
        }

        String ip = p.getIp();
        int port = p.getPort();

        try {
          
            Registry reg = LocateRegistry.getRegistry(ip, port);
            userChatDAO = (UserChatDAOInterface) reg.lookup("userChatDAO");
            chatDAO = (ChatDAOInterface) reg.lookup("chatDAO");
            userDAO = (UserDAOInterface) reg.lookup("userDAO");
            contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");
            if (userDAO == null) {
                System.out.println("DEBUG: userDAO is NULL after lookup!");
            } else {
                System.out.println("DEBUG: userDAO reference obtained.");
            }
            

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        membersContainer.setCellFactory(param -> new ListCell<UserDTO>() {
            @Override
            protected void updateItem(UserDTO user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
               
                    HBox hbox = new HBox(10);
        
                    ImageView imageView = new ImageView();
                    if (user.getUserPicture() != null) {
                        imageView.setImage(new Image(new ByteArrayInputStream(user.getUserPicture())));
                        imageView.setFitHeight(40);
                        imageView.setFitWidth(40);
                    }
        
             
                    Label nameLabel = new Label(user.getName());
                    nameLabel.setStyle("-fx-font-weight: bold;"); 
        
                    hbox.getChildren().addAll(imageView, nameLabel);
        
        
                    setGraphic(hbox);
                }
            }
        });
    }

    @FXML
    private void closeWindow() {
        if (groupContainer != null) {
            Stage stage = (Stage) groupContainer.getScene().getWindow();
            stage.close();
        }
    }
}
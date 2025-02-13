package gov.iti.jets.client;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.controller.ChatCadController;
import gov.iti.jets.controller.ContactCardController;
import gov.iti.jets.controller.MessageChatController;
import gov.iti.jets.dao.NotificationDAOInterface;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserStatus;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class ClientImplChat extends UnicastRemoteObject implements ClientInt<Object> {

    public Object msgControl;
    public int chatID;
    public int userID;
    public NotificationDAOInterface notificationDAO;

    public ClientImplChat(int chatID,int userID, Object m) throws RemoteException {
        super();
        this.chatID = chatID;
        this.userID = userID;
        // System.out.println(chatID);s
        msgControl = m;
                RMIConfig p = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream("/rmi.xml");
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(inputStream);
            inputStream.close();
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip = p.getIp();
        int port = p.getPort();

        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip, port);
                        notificationDAO = (NotificationDAOInterface) reg.lookup("notificationDAO");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void sendMessage(Object user) throws RemoteException {
        if(user instanceof MessageDTO messageDTO){

            if (messageDTO.getChatID() == chatID) {
                
                if (msgControl instanceof ChatCadController chatCadController) {
                    String ret = messageDTO.getMessageContent();
                    // if (ret.length() > 7)
                    //     ret = ret.substring(0, 7) + "...";
                    String ret2= ret;
                    int notCNT = notificationDAO.getMissed(userID, chatID);
                    System.out.println(notCNT);
                    {
                        Platform.runLater(() -> {
                            chatCadController.setMissedM(notCNT);
                            chatCadController.setText(ret2);

                        });
                        
                    }
                    
                }
                
            }
        }else if(user instanceof UserDTO userDTO){
            if (userDTO.getUserID() == chatID) {

            if(msgControl instanceof ChatCadController chatCadController){
                Platform.runLater(() -> {

                if (userDTO.getUserPicture() != null) {
                    chatCadController.setImage(userDTO.getUserPicture());
                }
                if (userDTO.getName() != null) {
                    chatCadController.setLabel(userDTO.getName());
                }
            });

            }
        }
    }
    }

    @Override
    public int get() throws RemoteException{
        return chatID;
    }
    

}
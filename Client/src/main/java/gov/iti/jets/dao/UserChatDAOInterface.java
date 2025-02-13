package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import gov.iti.jets.dto.UserDTO;

public interface UserChatDAOInterface extends Remote{




    public List<UserDTO> findAll(int userId) throws RemoteException ;



    public List<Integer> getChatParticipants(int chatId) throws RemoteException ;


    

    public List<Integer> getUserChats(int userId) throws RemoteException ;

    public UserDTO getUserById(int userId) throws RemoteException;
    boolean removeUserFromChat(int chatId, int userId) throws RemoteException;
    int getChatMembersCount(int chatId) throws RemoteException;

    
    public boolean addUserToChat(int chatId, int userId) throws RemoteException;


}

//----------------------------------------------------------------------
//
//  Filename: PresenceService.java
//  Description:
//
//  $Id:$
//
//----------------------------------------------------------------------
package compute;

/**
 * @author Jonathan Engelsma
 *
 */
/**
 * The abstract interface that is to implemented by a remote
 * presence server.  ChatClients will use this interface to
 * register themselves with the presence server, and also to
 * determine and locate other users who are available for chat
 * sessions.
 */
public interface PresenceService {

    /**
     * Register a webclient.client with the presence service.
     * @param reg The information that is to be registered about a webclient.client.
     */
    void register(RegistrationInfo reg) throws Exception;

    /**
     * Unregister a webclient.client from the presence service.  Client must call this
     * method when it terminates execution.
     * @param userName The name of the user to be unregistered.
     */
    void unregister(String userName) throws Exception;

    /**
     * Lookup the registration information of another webclient.client.
     * @param name The name of the webclient.client that is to be located.
     * @return The RegistrationInfo info for the webclient.client, or null if
     * no such webclient.client was found.
     */
    RegistrationInfo lookup(String name) throws Exception;


    /**
     * Sets the user's presence status.
     * @param userName The name of the user whose status is to be set.
     * @param status true if user is available, false otherwise.
     */
    void setStatus(String userName, boolean status) throws Exception;

    /**
     * Determine all users who are currently registered in the system.
     * @return An array of RegistrationInfo objects - one for each webclient.client
     * present in the system.
     */
    RegistrationInfo[] listRegisteredUsers() throws Exception;
}
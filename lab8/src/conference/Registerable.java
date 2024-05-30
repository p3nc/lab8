package conference;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Registerable extends Remote {
    int registerConferee(Conferee conferee) throws RemoteException;
    String getConfereeList() throws RemoteException;
}
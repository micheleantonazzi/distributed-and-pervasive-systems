package server;

import messages.AdministratorInfoMsgOuterClass.AdministratorInfoMsg;
import utility.HashSetSynchronized;

public class Administrators extends HashSetSynchronized<AdministratorInfoMsg> {

    private static Administrators instance;

    private Administrators(){}

    public static Administrators getInstance(){
        if (instance == null)
            instance = new Administrators();
        return instance;
    }
}

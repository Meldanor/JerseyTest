package security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import manager.AccountManager;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class AccountTests {

    @Test
    public void createAccount() {
        System.out.println("Adding accounts to the system");
        AccountManager aManager = AccountManager.getInstance();
        assertTrue(aManager.addUser("Kilian", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Matze", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Tabea", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Luigi", Hex.encodeHexString("1q2w3e4r".getBytes())));
        assertTrue(aManager.addUser("Markus", Hex.encodeHexString("1q2w3e4r".getBytes())));
        System.out.println("Accounts added");
    }

    @Test
    public void verifiyAccounts() {
        AccountManager aManager = AccountManager.getInstance();
        System.out.print("Login with User Kilian and his correct password:");
        assertTrue(aManager.validateUser("Kilian", Hex.encodeHexString("1q2w3e4r".getBytes())));
        System.out.println(" OK");
        System.out.print("Login with User Kilian and his wrong password:");
        assertFalse(aManager.validateUser("Kilian", Hex.encodeHexString("1q2w3e4".getBytes())));
        System.out.println(" OK");
    }
}

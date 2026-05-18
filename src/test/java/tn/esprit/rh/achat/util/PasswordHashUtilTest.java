package tn.esprit.rh.achat.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHashUtilTest {

    @Test
    void testHashAndVerifyPasswordSuccess() {
        String password = "SecUrE_P@ssw0rd!";
        String storedHash = PasswordHashUtil.hashPassword(password);
        
        assertNotNull(storedHash);
        assertTrue(storedHash.contains(":"));
        
        boolean isMatch = PasswordHashUtil.verifyPassword(password, storedHash);
        assertTrue(isMatch);
    }

    @Test
    void testVerifyPasswordFailure() {
        String password = "SecUrE_P@ssw0rd!";
        String storedHash = PasswordHashUtil.hashPassword(password);
        
        boolean isMatch = PasswordHashUtil.verifyPassword("wrongPassword", storedHash);
        assertFalse(isMatch);
    }

    @Test
    void testVerifyPasswordInvalidFormat() {
        boolean isMatch = PasswordHashUtil.verifyPassword("somePassword", "invalidFormatHash");
        assertFalse(isMatch);
    }

    @Test
    void testVerifyPasswordNullValues() {
        assertFalse(PasswordHashUtil.verifyPassword(null, "salt:hash"));
        assertFalse(PasswordHashUtil.verifyPassword("password", null));
    }
}

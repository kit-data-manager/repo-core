package edu.kit.datamanager.repo.util;

import edu.kit.datamanager.exceptions.ServiceUnavailableException;
import edu.kit.datamanager.exceptions.UnsupportedMediaTypeException;
import org.datacite.schema.kernel_4.RelatedIdentifierType;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorUtilTest {

    @Test
    public void validNumberOfValidators(){
        Assert.assertEquals(ValidatorUtil.soleInstance().getAllAvailableValidatorTypes().size(),3);
    }

    @Test
    public void valid() {
        assertTrue(ValidatorUtil.soleInstance().isValid("https://kit.edu", RelatedIdentifierType.URL));
    }

    @Test
    public void validTypeString() {
        assertTrue(ValidatorUtil.soleInstance().isValid("https://kit.edu", "URL"));
    }

    @Test
    public void invalidInputString() {
        try{
            assertFalse(ValidatorUtil.soleInstance().isValid("https://kit.example", RelatedIdentifierType.URL));
        } catch(ServiceUnavailableException ignored){
        }
    }

    @Test
    public void invalidTypeString() {
        try{
            assertTrue(ValidatorUtil.soleInstance().isValid("https://kit.edu", "INVALID"));
        } catch (UnsupportedMediaTypeException ignored) {
        }
    }

    @Test
    public void unimplementedType() {
        try{
            assertFalse(ValidatorUtil.soleInstance().isValid("https://kit.edu", RelatedIdentifierType.ARK));
        } catch(UnsupportedMediaTypeException ignored){
        }
    }
}
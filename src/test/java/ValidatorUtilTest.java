import edu.kit.datamanager.repo.util.ValidatorUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class ValidatorUtilTest {

    @Test
    public void testLoader(){
        Assert.assertEquals(ValidatorUtil.soleInstance().getAllAvailableValidatorTypes().size(),2);
    }
}
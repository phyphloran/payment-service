package yookassa.unit.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import yookassa.domain.services.impl.IpValidatorImpl;
import yookassa.unit.data.TestData;
import static org.junit.jupiter.api.Assertions.*;


class IpValidatorImplTest {

    private IpValidatorImpl validatorService;

    @BeforeEach
    void setUp() {
        validatorService = new IpValidatorImpl();
        ReflectionTestUtils.setField(validatorService, "ALLOWED_IPS", TestData.ALLOWED_IPS);
    }

    @Test
    void isValid_positive() {
        for (String ip : TestData.VALID_IPS) {
            assertTrue(validatorService.isValid(ip), "isValid_positive() not passed");
        }
    }

    @Test
    void isValid_negative() {
        for (String ip : TestData.INVALID_IPS) {
            assertFalse(validatorService.isValid(ip), "isValid_negative() not passed");
        }
    }

}

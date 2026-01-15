package yookassa.integration.services;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yookassa.domain.services.impl.IpValidatorImpl;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = IpValidatorImpl.class)
class IpValidatorImplTest {

    @Autowired
    private IpValidatorImpl ipValidatorImpl;

    private final String[] validIps = new String[]
        {
            "185.71.76.5",
            "185.71.76.20",
            "185.71.77.10",
            "185.71.77.25",
            "77.75.153.15",
            "77.75.153.100",
            "77.75.154.150",
            "77.75.154.200",
            "2a02:5180:0000:0000:0000:0000:0000:0001",
            "2a02:5180:0000:0000:abcd:ef12:3456:7890"
        };

    private final String[] invalidIps = new String[]
            {
                    "185.71.78.5",
                    "185.71.79.10",
                    "77.75.152.50",
                    "77.75.155.200",
                    "77.75.157.1",
                    "2a02:5181::1",
                    "2a02:5182:abcd::1234",
                    "2a03::1",
                    "2b02:5180::abcd",
                    "2001:db8::1"
            };

    @Test
    void isValid_positive() {
        for (String ip : validIps) {
            assertTrue(ipValidatorImpl.isValid(ip), "isValid_positive() not passed");
        }
    }

    @Test
    void isValid_negative() {
        for (String ip : invalidIps) {
            assertTrue(ipValidatorImpl.isValid(ip), "isValid_negative() not passed");
        }
    }

}

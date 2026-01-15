package yookassa.domain.services.impl;


import org.springframework.stereotype.Service;
import yookassa.domain.services.IpValidator;
import java.math.BigInteger;
import java.net.InetAddress;


@Service
public class IpValidatorImpl implements IpValidator {

    private final String[] ALLOWED_IPS = {
            "185.71.76.0/27",
            "185.71.77.0/27",
            "77.75.153.0/25",
            "77.75.156.11",
            "77.75.156.35",
            "77.75.154.128/25",
            "2a02:5180::/32"
    };

    @Override
    public boolean isValid(String ip) {
        if (ip == null || ip.isBlank()) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);

            for (String allowed : ALLOWED_IPS) {
                if (allowed.contains("/")) {
                    if (isInCidr(address, allowed)) {
                        return true;
                    }
                } else if (address.equals(InetAddress.getByName(allowed))) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private boolean isInCidr(InetAddress address, String cidr) throws Exception {
        String[] parts = cidr.split("/");
        InetAddress network = InetAddress.getByName(parts[0]);
        int prefixLength = Integer.parseInt(parts[1]);

        byte[] addressBytes = address.getAddress();
        byte[] networkBytes = network.getAddress();

        if (addressBytes.length != networkBytes.length) {
            return false;
        }

        BigInteger ipVal = new BigInteger(1, addressBytes);
        BigInteger netVal = new BigInteger(1, networkBytes);

        int maxBits = addressBytes.length * 8;
        BigInteger mask = BigInteger.ONE.shiftLeft(maxBits)
                .subtract(BigInteger.ONE)
                .shiftRight(prefixLength)
                .not();

        return ipVal.and(mask).equals(netVal.and(mask));
    }

}

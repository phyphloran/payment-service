package yookassa.domain.services.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yookassa.domain.services.IpValidator;
import java.math.BigInteger;
import java.net.InetAddress;


@Service
public class IpValidatorImpl implements IpValidator {

    @Value("${yookassa.allowed-ips}")
    private String[] ALLOWED_IPS;

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

    /*@PostConstruct
    public void init() {
        Arrays.stream(ALLOWED_IPS).forEach(s -> System.out.println(s));
        System.out.println("\n" + ALLOWED_IPS[3]);
    }*/

}

package info.ferrarimarco.uniroma2.msa.resourcesharing.services;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class DatatypeConversionService {

    public String bytesToHexString(byte[] bytes) {
        char[] hexEncodedHash = Hex.encodeHex(bytes);

        return new String(hexEncodedHash);
    }
}

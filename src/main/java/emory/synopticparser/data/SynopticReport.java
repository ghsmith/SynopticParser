package emory.synopticparser.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ghsmith
 */
public class SynopticReport {
    public String accessionNumber;
    public String part;
    public Map<String, String> nameValueMap = new HashMap<>();
    public String signatureLine1;
    public String signatureLine2;
    public String signatureLine3;
}

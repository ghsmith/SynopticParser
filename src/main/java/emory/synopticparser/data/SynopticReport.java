package emory.synopticparser.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ghsmith
 */
public class SynopticReport {
    public String accessionNumber;
    public String part;
    public Map<String, String> nameValueMap = new HashMap<>();
    public List<String> signatureList = new ArrayList<>();
    public StringBuffer body = new StringBuffer();
}

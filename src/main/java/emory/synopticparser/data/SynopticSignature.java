package emory.synopticparser.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ghsmith
 */
public class SynopticSignature {
    public String synopticName;
    public List<String> signatureList = new ArrayList<>();
    public String diagnosis;
    public List<String> tnmList = new ArrayList<>();
}

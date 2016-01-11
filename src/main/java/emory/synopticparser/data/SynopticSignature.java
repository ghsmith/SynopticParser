package emory.synopticparser.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ghsmith
 */
public class SynopticSignature {
    public String synopticName;
    public String signatureLine1;
    public String signatureLine2;
    public String signatureLine3;
    public String diagnosis;
    public List<String> tnmList = new ArrayList<>();
}

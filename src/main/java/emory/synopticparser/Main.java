package emory.synopticparser;

import emory.synopticparser.data.SynopticReport;
import emory.synopticparser.data.SynopticSignature;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author ghsmith
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws FileNotFoundException, IOException {

        // *********************************************************************
        // load the synoptic report signature definitions
        List<SynopticSignature> sigList = new ArrayList<>();
        ICsvMapReader mapReader = new CsvMapReader(new FileReader(args[0]), CsvPreference.TAB_PREFERENCE);
        final String[] header = mapReader.getHeader(true);
        Map<String, String> tsvMap;
        while((tsvMap = mapReader.read(header)) != null) {
            SynopticSignature sig = new SynopticSignature();
            sig.synopticName = tsvMap.get("synoptic_name");
            sig.signatureLine1 = tsvMap.get("signature_line1");
            sig.signatureLine2 = tsvMap.get("signature_line2");
            sig.signatureLine3 = tsvMap.get("signature_line3");
            sig.diagnosis = tsvMap.get("diagnosis");
            for(String tnm : tsvMap.get("tnm").split(";")) {
                sig.tnmList.add(tnm);
            }
            sigList.add(sig);
        }
        mapReader.close();
        LOGGER.info(sigList.size() + " signatures loaded");
        
        // *********************************************************************
        // load the synoptic report file
        List<SynopticReport> repList = new ArrayList<>();
        //BufferedReader in = new BufferedReader(new FileReader(args[1]));
        BufferedReader in = BufferedReaderFactory.getBufferedReaderInstance(args[1]);
        String line;
        String lastAccNo = null;
        while((line = in.readLine()) != null) {
            SynopticReport rep = new SynopticReport();
            {
                Pattern pattern = Pattern.compile("^(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)$");
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()) {
                    lastAccNo = matcher.group(5);
                    rep.accessionNumber = lastAccNo;
                }
                else {
                    // e.g., breast case might have both sides but the header only appears once
                    rep.accessionNumber = lastAccNo;
                }
            }
            String lastName = null;
            int nameNumber = 1;
            while((line = in.readLine()) != null && !line.startsWith("----------")) {
                Pattern pattern1 = Pattern.compile("^([A-Za-z0-9].+?):(.*)$");
                Matcher matcher1 = pattern1.matcher(line);
                if(matcher1.find()) {
                    lastName = matcher1.group(1).trim();
                    rep.nameValueMap.put(lastName, matcher1.group(2).trim());
                    if(nameNumber == 1) {rep.signatureLine1 = lastName;}
                    if(nameNumber == 2) {rep.signatureLine2 = lastName;}
                    if(nameNumber == 3) {rep.signatureLine3 = lastName;}
                    nameNumber++;
                }
                else {
                    Pattern pattern2 = Pattern.compile("^ *(.*)$");
                    Matcher matcher2 = pattern2.matcher(line);
                    if(matcher2.find()) {
                        rep.nameValueMap.put(lastName, rep.nameValueMap.get(lastName) + "; " + matcher2.group(1).trim());
                    }
                }
            }
            repList.add(rep);
            while((line = in.readLine()) != null && !line.startsWith("\"\tSynoptic Diagnosis")) {
            }
        }
        in.close();
        LOGGER.info(repList.size() + " reports loaded");

        // *********************************************************************
        // extract the diagnosis and staging information from the synoptic
        // reports
        for(SynopticReport rep : repList) {
            for(SynopticSignature sig : sigList) {
                if(
                    rep.signatureLine1.equals(sig.signatureLine1)
                    && rep.signatureLine2.equals(sig.signatureLine2)
                    && rep.signatureLine3.equals(sig.signatureLine3)
                ) {
                    StringBuilder tnm = new StringBuilder();
                    {
                        StringBuilder tnmValues = new StringBuilder();
                        for(String tnmName : sig.tnmList) {
                            tnmValues.append(rep.nameValueMap.get(tnmName));
                        }
                        Pattern pattern1 = Pattern.compile("(pT[^ :]*)");
                        Matcher matcher1 = pattern1.matcher(tnmValues);
                        while(matcher1.find()) {
                            tnm.append(matcher1.group(1));
                        }
                        Pattern pattern2 = Pattern.compile("p(N[^ :]*)");
                        Matcher matcher2 = pattern2.matcher(tnmValues);
                        while(matcher2.find()) {
                            tnm.append(matcher2.group(1));
                        }
                        Pattern pattern3 = Pattern.compile("p(M[^ :]*)");
                        Matcher matcher3 = pattern3.matcher(tnmValues);
                        while(matcher3.find()) {
                            tnm.append(matcher3.group(1));
                        }
                    }
                    System.out.println(
                        rep.accessionNumber
                        + "\t" + sig.synopticName
                        //+ "\t" + (rep.nameValueMap.get("Specimen Laterality") != null ? rep.nameValueMap.get("Specimen Laterality") : "")
                        + "\t" + rep.nameValueMap.get(sig.diagnosis)
                        + "\t" + tnm
                    );
                }
            }
        }
        
    }
    
}

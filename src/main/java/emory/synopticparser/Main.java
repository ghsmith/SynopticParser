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
        {
            ICsvMapReader mapReader = new CsvMapReader(new FileReader(args[0]), CsvPreference.TAB_PREFERENCE);
            final String[] header = mapReader.getHeader(true);
            Map<String, String> tsvMap;
            while((tsvMap = mapReader.read(header)) != null) {
                SynopticSignature sig = new SynopticSignature();
                sig.synopticName = tsvMap.get("synoptic_name");
                for(String signature : tsvMap.get("signature").split(";")) {
                    sig.signatureList.add(signature);
                }
                sig.diagnosis = tsvMap.get("diagnosis");
                for(String tnm : tsvMap.get("tnm").split(";")) {
                    sig.tnmList.add(tnm);
                }
                sigList.add(sig);
            }
            mapReader.close();
        }
        LOGGER.info(sigList.size() + " signatures loaded");
        
        // *********************************************************************
        // load the synoptic report file
        List<SynopticReport> repList = new ArrayList<>();
        {
            BufferedReader in = BufferedReaderFactory.getBufferedReaderInstance(args[1]);
            String line;
            SynopticReport rep = null;
            String lastName = null;
            while((line = in.readLine()) != null) {
                {
                    Pattern pattern = Pattern.compile("^(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)\\t(.*)$");
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.find()) {
                        rep = new SynopticReport();
                        rep.accessionNumber = matcher.group(5);
                        rep.body.append(("[" + line + "]").trim().replaceAll(" +", " ").replaceAll("\\t+", " "));
                        continue;
                    }
                }
                {
                    if(line.startsWith("----------")) {
                        repList.add(rep);
                        String lastAccNo = rep.accessionNumber;
                        rep = new SynopticReport();
                        rep.accessionNumber = lastAccNo;
                        continue;
                    }
                }
                {
                    Pattern pattern1 = Pattern.compile("^([A-Za-z0-9].+?):(.*)$");
                    Matcher matcher1 = pattern1.matcher(line);
                    if(matcher1.find()) {
                        lastName = matcher1.group(1).trim();
                        rep.nameValueMap.put(lastName, matcher1.group(2).trim());
                        if(matcher1.group(2).trim().length() > 1) {
                            rep.signatureList.add(lastName);
                        }
                    }
                    else {
                        Pattern pattern2 = Pattern.compile("^ *(.*)$");
                        Matcher matcher2 = pattern2.matcher(line);
                        if(matcher2.find()) {
                            rep.nameValueMap.put(lastName, rep.nameValueMap.get(lastName) + "; " + matcher2.group(1).trim());
                        }
                    }
                    rep.body.append(("[" + line + "]").trim().replaceAll(" +", " ").replaceAll("\\t+", " "));
                    continue;
                }
            }
            in.close();
        }
        LOGGER.info(repList.size() + " reports loaded");

        // *********************************************************************
        // extract the diagnosis and staging information from the synoptic
        // reports
        for(SynopticReport rep : repList) {
            boolean matchRep = false;
            for(SynopticSignature sig : sigList) {
                boolean match = true;
                int sigNameIndex = 0;
                for(String sigName : sig.signatureList) {
                    //System.out.println(sigName + " == " + rep.signatureList.get(sigNameIndex));
                    if(!sigName.equals(rep.signatureList.get(sigNameIndex++))) {
                        match = false;
                        break;
                    }
                }
                if(match) {
                    matchRep = true;
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
                        + "\t" + rep.nameValueMap.get(sig.diagnosis)
                        + "\t" + tnm
                        + "\t" + rep.body.toString()
                    );
                }
            }
            if(!matchRep) {
                StringBuilder tnm = new StringBuilder();
                {
                    StringBuilder allValues = new StringBuilder();
                    for(String name : rep.nameValueMap.keySet()) {
                        allValues.append(rep.nameValueMap.get(name));
                    }
                    Pattern pattern1 = Pattern.compile("(pT[^ :]*)");
                    Matcher matcher1 = pattern1.matcher(allValues);
                    while(matcher1.find()) {
                        tnm.append(matcher1.group(1));
                    }
                    Pattern pattern2 = Pattern.compile("p(N[^ :]*)");
                    Matcher matcher2 = pattern2.matcher(allValues);
                    while(matcher2.find()) {
                        tnm.append(matcher2.group(1));
                    }
                    Pattern pattern3 = Pattern.compile("p(M[^ :]*)");
                    Matcher matcher3 = pattern3.matcher(allValues);
                    while(matcher3.find()) {
                        tnm.append(matcher3.group(1));
                    }
                }
                System.out.println(
                    rep.accessionNumber
                    + "\t" + "unknown"
                    + "\t" + "unknown"
                    + "\t" + tnm
                    + "\t" + rep.body.toString()
                );
            }
        }
        
    }
    
}

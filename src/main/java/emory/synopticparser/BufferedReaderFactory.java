package emory.synopticparser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ghsmith
 */
public class BufferedReaderFactory {
 
    // convenient way to strip control characters, in this case 0xA0, from CoPath output
    public static BufferedReader getBufferedReaderInstance(String fileName) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FilterInputStream(new BufferedInputStream(new FileInputStream(fileName))) {
            @Override
            public int read() throws IOException {
                int result = in.read();
                while(result == 0xA0) {
                    result = in.read();
                }
                return result;
            }
            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                for(int x = off; x < len; x++) {
                    int result = read();
                    if(result > -1) {
                        b[x] = (byte)result;
                    }
                    else {
                        return(x - off == 0 ? -1 : x - off);
                    }
                }
                return len;
            }
            @Override
            public int read(byte[] b) throws IOException {
                throw new RuntimeException("not supported");
            }
        }));
    }
    
}

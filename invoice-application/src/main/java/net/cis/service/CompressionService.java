package net.cis.service;

import java.io.IOException;


/**
 * Created by NhanNguyen 19/10/2018
 */
public interface CompressionService {

	byte[] compress(final String str) throws IOException;
	
    String decompress(final byte[] compressed) throws IOException;
    
    boolean isCompressed(final byte[] compressed);
}
 
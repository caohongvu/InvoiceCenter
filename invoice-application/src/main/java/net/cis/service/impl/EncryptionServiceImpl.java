package net.cis.service.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.cis.bkav.entity.CommandDataEntity;
import net.cis.common.util.BkavInvoiceUtil;
import net.cis.common.util.constant.BkavConfigurationConstant;
import net.cis.service.CompressionService;
import net.cis.service.EncryptionService;

/**
 * Created by NhanNguyen 19/10/2018
 */

@Service
public class EncryptionServiceImpl implements EncryptionService {

	@Autowired
	CompressionService compressionService;

	private static Key decodeKey;

	private static IvParameterSpec decodeIvy;

	private static String decodeBase64(String encodedString) throws UnsupportedEncodingException {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		String decodedString = new String(decodedBytes, "windows-1252");

		return decodedString;
	}

	private static Key generateKey(String key) throws Exception {
		return new SecretKeySpec(key.getBytes("windows-1252"), "AES");
	}

	private static IvParameterSpec generateIvParameterSpec(String ivy) throws Exception {
		return new IvParameterSpec(ivy.getBytes("windows-1252"));
	}

	@Override
	public String doEncryptedCommandData(CommandDataEntity commandDataEntity, String partnerToken) throws Exception {
		Map<String, String> keyMap = BkavInvoiceUtil.parseTokenToKeyAndIvy(partnerToken);

		String key_token = new String(keyMap.get("KEY").getBytes("windows-1252"), StandardCharsets.UTF_8);
		String ivy_token = new String(keyMap.get("IVY").getBytes("windows-1252"), StandardCharsets.UTF_8);

		String decodedKey = decodeBase64(key_token);
		String decodedIvy = decodeBase64(ivy_token);

		Key key = generateKey(decodedKey);
		IvParameterSpec ivParameterSpec = generateIvParameterSpec(decodedIvy);

		decodeKey = key;
		decodeIvy = ivParameterSpec;

		Cipher cipher = Cipher.getInstance(BkavConfigurationConstant.CIS_BKAV_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

		// Convert to JSON
		ObjectMapper mapper = new ObjectMapper();
		String strCommandData = mapper.writeValueAsString(commandDataEntity);
		System.out.println(strCommandData);

		// Zip Object CommandData
		byte[] zipCommandData = compressionService.compress(strCommandData);

		byte[] byteEncrypted = cipher.doFinal(zipCommandData);
		String encrypted = Base64.getEncoder().encodeToString(byteEncrypted);

		return encrypted;
	}

	@Override
	public String doDecryptedCommamdData(String strEncryted, String partnerToken) throws Exception {
		Key key = decodeKey;
		IvParameterSpec ivParameterSpec = decodeIvy;

		Cipher cipher = Cipher.getInstance(BkavConfigurationConstant.CIS_BKAV_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

		byte[] decodedValue = Base64.getDecoder().decode(strEncryted.trim());
		byte[] byteDecrypted = cipher.doFinal(decodedValue);

		// Unzip Object CommandData
		String strUnzip = compressionService.decompress(byteDecrypted);

		return strUnzip;
	}

}

package net.cis.service;

import net.cis.bkav.entity.CommandDataEntity;


/**
 * Created by NhanNguyen 19/10/2018
 */
public interface EncryptionService {

	String doEncryptedCommandData(CommandDataEntity commandDataEntity, String partnerToken) throws Exception;
	
	String doEncryptedRequestBody(String requestBody, String partnerToken) throws Exception;

    String doDecryptedCommamdData(String strEncryted, String partnerToken) throws Exception;
}
 
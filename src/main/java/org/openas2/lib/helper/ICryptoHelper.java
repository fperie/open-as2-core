package org.openas2.lib.helper;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.mail.internet.MimeBodyPart;

public interface ICryptoHelper
{
	boolean isEncrypted(MimeBodyPart part) throws Exception;

	KeyStore getKeyStore() throws Exception;

	KeyStore loadKeyStore(InputStream in, char[] password) throws Exception;

	KeyStore loadKeyStore(String filename, char[] password) throws Exception;

	boolean isSigned(MimeBodyPart part) throws Exception;

	String calculateMIC(MimeBodyPart part, String digest, boolean includeHeaders) throws Exception;

	MimeBodyPart decrypt(MimeBodyPart part, Certificate cert, Key key) throws Exception;

	void deinitialize() throws Exception;

	MimeBodyPart encrypt(MimeBodyPart part, Certificate cert, String algorithm) throws Exception;

	void initialize() throws Exception;

	MimeBodyPart sign(MimeBodyPart part, Certificate cert, Key key, String digest) throws Exception;

	MimeBodyPart verify(MimeBodyPart part, Certificate cert) throws Exception;
}

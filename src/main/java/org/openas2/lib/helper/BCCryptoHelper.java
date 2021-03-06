package org.openas2.lib.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.util.encoders.Base64;
import org.openas2.lib.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCCryptoHelper implements ICryptoHelper
{
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BCCryptoHelper.class);

	@Override
	public boolean isEncrypted(MimeBodyPart part) throws MessagingException
	{
		ContentType contentType = new ContentType(part.getContentType());
		String baseType = contentType.getBaseType().toLowerCase();

		if (baseType.equalsIgnoreCase("application/pkcs7-mime"))
		{
			String smimeType = contentType.getParameter("smime-type");

			return ((smimeType != null) && smimeType.equalsIgnoreCase("enveloped-data"));
		}

		return false;
	}

	@Override
	public boolean isSigned(MimeBodyPart part) throws MessagingException
	{
		ContentType contentType = new ContentType(part.getContentType());
		String baseType = contentType.getBaseType().toLowerCase();

		return baseType.equalsIgnoreCase("multipart/signed");
	}

	@Override
	public String calculateMIC(MimeBodyPart part, String digest, boolean includeHeaders)
			throws GeneralSecurityException, MessagingException, IOException
	{
		String micAlg = convertAlgorithm(digest);

		MessageDigest md = MessageDigest.getInstance(micAlg, "BC");

		// convert the Mime data to a byte array, then to an InputStream
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		if (includeHeaders)
		{
			part.writeTo(bOut);
		}
		else
		{
			IOUtil.copy(part.getInputStream(), bOut);
		}

		byte[] data = bOut.toByteArray();

		InputStream bIn = trimCRLFPrefix(data);

		// calculate the hash of the data and mime header
		DigestInputStream digIn = new DigestInputStream(bIn, md);

		byte[] buf = new byte[4096];

		while (digIn.read(buf) >= 0)
		{
		}

		bOut.close();

		byte[] mic = digIn.getMessageDigest().digest();
		String micString = new String(Base64.encode(mic));
		StringBuffer micResult = new StringBuffer(micString);
		micResult.append(", ").append(digest);

		return micResult.toString();
	}

	@Override
	public MimeBodyPart decrypt(MimeBodyPart part, Certificate cert, Key key)
			throws GeneralSecurityException, MessagingException, CMSException, IOException,
			SMIMEException
	{
		// Make sure the data is encrypted
		if (!isEncrypted(part))
		{
			throw new GeneralSecurityException("Content-Type indicates data isn't encrypted");
		}

		// Cast parameters to what BC needs
		X509Certificate x509Cert = castCertificate(cert);

		// Parse the MIME body into an SMIME envelope object
		SMIMEEnveloped envelope = new SMIMEEnveloped(part);

		// Get the recipient object for decryption
		RecipientId recId = new JceKeyTransRecipientId(x509Cert);
		recId.setSerialNumber(x509Cert.getSerialNumber());
		recId.setIssuer(x509Cert.getIssuerX500Principal().getEncoded());

		RecipientInformation recipient = envelope.getRecipientInfos().get(recId);

		if (recipient == null)
		{
			throw new GeneralSecurityException("Certificate does not match part signature");
		}

		// try to decrypt the data
		final byte[] decryptedData;

		try
		{
			decryptedData = recipient.getContent(key, "BC");
		}
		catch (CMSException cmse)
		{
			if (StringUtils.containsIgnoreCase(cmse.getMessage(), "key invalid in message"))
			{
				LOGGER.error(
						"The following error occured during the decypher phase. JCE has been installed correctly in the JRE/JVM ? "
								+ "Please, can you verify this configuration.",
						cmse);
			}
			throw cmse;
		}

		return SMIMEUtil.toMimeBodyPart(decryptedData);
	}

	@Override
	public void deinitialize()
	{
		// do nothing...
	}

	@Override
	public MimeBodyPart encrypt(MimeBodyPart part, Certificate cert, String algorithm)
			throws GeneralSecurityException, SMIMEException
	{
		X509Certificate x509Cert = castCertificate(cert);

		String encAlg = convertAlgorithm(algorithm);

		SMIMEEnvelopedGenerator gen = new SMIMEEnvelopedGenerator();
		gen.addKeyTransRecipient(x509Cert);

		MimeBodyPart encData = gen.generate(part, encAlg, "BC");

		return encData;
	}

	@Override
	public void initialize()
	{
		Security.addProvider(new BouncyCastleProvider());

		MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
		mc.addMailcap("application/pkcs7-signature;; "
				+ "x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
		mc.addMailcap("application/pkcs7-mime;; "
				+ "x-java-content-handler=org.bouncycastle.mail.smime.handlers.pkcs7_mime");
		mc.addMailcap("application/x-pkcs7-signature;; "
				+ "x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
		mc.addMailcap("application/x-pkcs7-mime;; "
				+ "x-java-content-handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
		mc.addMailcap("multipart/signed;; "
				+ "x-java-content-handler=org.bouncycastle.mail.smime.handlers.multipart_signed");
		CommandMap.setDefaultCommandMap(mc);
	}

	@Override
	public MimeBodyPart sign(MimeBodyPart part, Certificate cert, Key key, String digest)
			throws GeneralSecurityException, SMIMEException, MessagingException
	{
		String signDigest = convertAlgorithm(digest);
		X509Certificate x509Cert = castCertificate(cert);
		PrivateKey privKey = castKey(key);

		SMIMESignedGenerator sGen = new SMIMESignedGenerator();
		sGen.addSigner(privKey, x509Cert, signDigest);

		MimeMultipart signedData;

		signedData = sGen.generate(part, "BC");

		MimeBodyPart tmpBody = new MimeBodyPart();
		tmpBody.setContent(signedData);
		tmpBody.setHeader("Content-Type", signedData.getContentType());

		return tmpBody;
	}

	@Override
	public MimeBodyPart verify(MimeBodyPart part, Certificate cert)
			throws GeneralSecurityException, IOException, MessagingException, CMSException
	{
		// Make sure the data is signed
		if (!isSigned(part))
		{
			throw new GeneralSecurityException("Content-Type indicates data isn't signed");
		}

		X509Certificate x509Cert = castCertificate(cert);

		MimeMultipart mainParts = (MimeMultipart)part.getContent();

		SMIMESigned signedPart = new SMIMESigned(mainParts);

		Iterator signerIt = signedPart.getSignerInfos().getSigners().iterator();
		SignerInformation signer;

		while (signerIt.hasNext())
		{
			signer = (SignerInformation)signerIt.next();

			if (!signer.verify(x509Cert, "BC"))
			{
				throw new SignatureException("Verification failed");
			}
		}

		return signedPart.getContent();
	}

	protected X509Certificate castCertificate(Certificate cert) throws GeneralSecurityException
	{
		if (cert == null)
		{
			throw new GeneralSecurityException("Certificate is null");
		}
		if (!(cert instanceof X509Certificate))
		{
			throw new GeneralSecurityException("Certificate must be an instance of X509Certificate");
		}

		return (X509Certificate)cert;
	}

	protected PrivateKey castKey(Key key) throws GeneralSecurityException
	{
		if (!(key instanceof PrivateKey))
		{
			throw new GeneralSecurityException("Key must implement PrivateKey interface");
		}

		return (PrivateKey)key;
	}

	protected String convertAlgorithm(String algorithm)
			throws NoSuchAlgorithmException
	{
		if (algorithm == null)
		{
			throw new NoSuchAlgorithmException("Algorithm is null");
		}

		final String response;
		switch (StringUtils.lowerCase(algorithm))
		{
			case "md5":
				response = SMIMESignedGenerator.DIGEST_MD5;
				break;

			case "sha1":
			case "sha-1":
				response = SMIMESignedGenerator.DIGEST_SHA1;
				break;

			case "sha224":
			case "sha-224":
				response = SMIMESignedGenerator.DIGEST_SHA224;
				break;

			case "sha256":
			case "sha-256":
				response = SMIMESignedGenerator.DIGEST_SHA256;
				break;

			case "sha384":
			case "sha-384":
				response = SMIMESignedGenerator.DIGEST_SHA384;
				break;

			case "sha512":
			case "sha-512":
				response = SMIMESignedGenerator.DIGEST_SHA512;
				break;

			case "3des":
			case "3-des":
				response = SMIMEEnvelopedGenerator.DES_EDE3_CBC;
				break;

			case "cast5":
				response = SMIMEEnvelopedGenerator.CAST5_CBC;
				break;

			case "idea":
				response = SMIMEEnvelopedGenerator.IDEA_CBC;
				break;

			case "rc2":
			case "rc-2":
				response = SMIMEEnvelopedGenerator.RC2_CBC;
				break;

			case "aes128":
			case "aes-128":
				response = SMIMEEnvelopedGenerator.AES128_CBC;
				break;

			case "aes192":
			case "aes-192":
				response = SMIMEEnvelopedGenerator.AES192_CBC;
				break;

			case "aes256":
			case "aes-256":
				response = SMIMEEnvelopedGenerator.AES256_CBC;
				break;

			default:
				throw new NoSuchAlgorithmException("Unknown algorithm: " + algorithm);
		}

		return response;
	}

	protected InputStream trimCRLFPrefix(byte[] data)
	{
		ByteArrayInputStream bIn = new ByteArrayInputStream(data);

		int scanPos = 0;
		int len = data.length;

		while (scanPos < (len - 1))
		{
			if (new String(data, scanPos, 2).equals("\r\n"))
			{
				bIn.read();
				bIn.read();
				scanPos += 2;
			}
			else
			{
				return bIn;
			}
		}

		return bIn;
	}

	@Override
	public KeyStore getKeyStore() throws KeyStoreException, NoSuchProviderException
	{
		return KeyStore.getInstance("PKCS12", "BC");
	}

	@Override
	public KeyStore loadKeyStore(InputStream in, char[] password) throws Exception
	{
		KeyStore ks = getKeyStore();
		ks.load(in, password);
		return ks;
	}

	@Override
	public KeyStore loadKeyStore(String filename, char[] password) throws Exception
	{
		FileInputStream fIn = new FileInputStream(filename);

		try
		{
			return loadKeyStore(fIn, password);
		}
		finally
		{
			fIn.close();
		}
	}
}

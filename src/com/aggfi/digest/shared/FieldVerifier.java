package com.aggfi.digest.shared;

import com.aggfi.digest.client.constants.SimpleConstants;
import com.aggfi.digest.client.constants.SimpleMessages;
import com.aggfi.digest.shared.model.IExtDigest;


/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> packing because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is note translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class FieldVerifier {

	/**
	 * Verifies that the specified name is valid for our service.
	 * 
	 * In this example, we only require that the name is at least four
	 * characters. In your application, you can use more complex checks to ensure
	 * that usernames, passwords, email addresses, URLs, and other fields have the
	 * proper syntax.
	 * 
	 * @param name the name to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidName(String name) {
		if (name == null) {
			return false;
		}
		return name.length() > 2;
	}

	public static void areValidDigestFields(IExtDigest digest, SimpleMessages messages, SimpleConstants constants) {
		//owner_id
		
		if(!FieldVerifier.isValidName(digest.getOwnerId())){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.ownerStr()));
		}
		if(!FieldVerifier.isValidName(digest.getAuthor())){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.authorStr()));
		}
		if(!FieldVerifier.isValidName(digest.getDomain())){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.domainStr()));
		}
		if(!FieldVerifier.isValidName(digest.getProjectId())){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.projectIdStr()));
		}
		if(!FieldVerifier.isValidName(digest.getName())){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.digestNameStr()));
		}
		
		//check owner id is of form: id@googlewave.com
		if(digest.getOwnerId().indexOf("@googlewave.com") < 0){
			throw new IllegalArgumentException(messages.incorrectFormParamExcptn(constants.ownerStr(), "your_id@googlewave.com"));
		}else{
			String ownerStr = digest.getOwnerId().substring(0, digest.getOwnerId().indexOf("@"));
			if(!FieldVerifier.isValidName(ownerStr)){
				throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.ownerStr()));
			}
		}
		
		//check owner id is of form: id@googlewave.com
		if(digest.getGooglegroupsId() != null && !"".equals(digest.getGooglegroupsId()) && digest.getGooglegroupsId().indexOf("@googlegroups.com") < 0){
			throw new IllegalArgumentException(messages.incorrectFormParamExcptn(constants.googlegroupsIdStr(), "your_group_id@googlegroups.com"));
		}
		
	}
}

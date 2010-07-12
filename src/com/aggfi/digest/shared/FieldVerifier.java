package com.aggfi.digest.shared;

import com.aggfi.digest.client.constants.DigestConstants;
import com.aggfi.digest.client.constants.DigestMessages;
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

	public static void areValidDigestFields(IExtDigest digest, DigestMessages messages, DigestConstants constants) {
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
		verifyProjectId(digest.getProjectId(), messages, constants);
		if(!FieldVerifier.isValidName(digest.getName())){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.digestNameStr()));
		}
		
		verifyWaveId(digest.getOwnerId(), messages, constants.ownerStr());
		
		
		
		//check owner id is of form: id@googlewave.com
		if(digest.getGooglegroupsId() != null && !"".equals(digest.getGooglegroupsId()) && digest.getGooglegroupsId().indexOf("@googlegroups.com") < 0){
			throw new IllegalArgumentException(messages.incorrectFormParamExcptn(constants.googlegroupsIdStr(), "your_group_id@googlegroups.com"));
		}
		
	}

	public static void verifyProjectId(String projectIdB4,
			DigestMessages messages, DigestConstants constants) {
		String[] prjIdSplit = projectIdB4.split("-");
		if(prjIdSplit.length == 1 || !FieldVerifier.isValidName(projectIdB4.split("-")[1])){
			throw new IllegalArgumentException(messages.missingCreationParamExcptn(constants.projectIdStr()));
		}
		String digestId = projectIdB4.split("-")[1];//because projectId is automatically prefixed with 'ownerId-' 
		isFieldAlphaNumeric(messages,digestId,constants.projectIdStr());
	}

	public static void verifyWaveId(String userWaveId,
			DigestMessages messages, String fieldName) throws IllegalArgumentException{
		String[] idParts = userWaveId.split("@");
		if(idParts.length > 2){
			throw new IllegalArgumentException(messages.incorrectFormParamExcptn(fieldName, "your_id@googlewave.com"));
		}
		isFieldAlphaNumeric(messages,idParts[0],fieldName);
		String[] domain = idParts[0].split(".");
		for(String str : domain){
			isFieldAlphaNumeric(messages,str,fieldName);
		}
		//check owner id is of form: id@googlewave.com
		if(userWaveId.indexOf("@googlewave.com") < 0 && userWaveId.indexOf("public@a.gwave.com") < 0 && userWaveId.indexOf("@googlegroups.com") < 0){
			throw new IllegalArgumentException(messages.incorrectFormParamExcptn(fieldName, "your_id@googlewave.com or your_id@googlegroups.com or public@a.gwave.com"));
		}else{
			String ownerStr = userWaveId.substring(0, userWaveId.indexOf("@"));
			if(!FieldVerifier.isValidName(ownerStr)){
				throw new IllegalArgumentException(messages.missingCreationParamExcptn(fieldName));
			}
		}
	}
	
	public static void isFieldAlphaNumeric(DigestMessages messages, String field, String fieldname){
		String origField = field;
		field = field.replace(".","").replace("_", "").replace("-", "");
		String splitStr = field.split("[\\W]")[0];
		if(field.split("\\W").length > 1 || splitStr.length() < field.length()){
			throw new IllegalArgumentException(messages.fieldShouldBeAlphaNumericExcptn(fieldname,origField));
		}
	}
}

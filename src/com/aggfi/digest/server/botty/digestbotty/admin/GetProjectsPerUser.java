package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.google.inject.Inject;

public class GetProjectsPerUser extends Command {
	private ExtDigestDao extDigestDao = null;
	private Util util = null;

	@Inject
	public GetProjectsPerUser(Util util, ExtDigestDao extDigestDao) {
		this.extDigestDao = extDigestDao;
		this.util = util;
	}

	@Override
	public JSONObject execute() throws JSONException {      
		String userId = this.getParam("userId");
		if (util.isNullOrEmpty(userId)) {
			throw new IllegalArgumentException("Missing required param: userId");
		}    
		String senderId = this.getParam("senderId");
	    if (senderId != null && !senderId.equals(userId)) {
	    	throw new RuntimeException(userId  + " and " + senderId + " do not match!" );
	    }
		JSONObject json = new JSONObject();
		JSONObject digestMapJson = new JSONObject();
		List<ExtDigest> digests = extDigestDao.retrDigestsByOwnerOrManagerId(userId);
		if (digests.size() > 0) {
			for (ExtDigest digest : digests) {
				String prjId = digest.getProjectId();
				String prjName = digest.getName();
				digestMapJson.put(prjId, prjName);
			}
		}else{
			digestMapJson.put("none", "none");
		}
		json.put("result", digestMapJson);
		return json;
	}

}

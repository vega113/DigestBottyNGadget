package com.aggfi.digest.server.botty.digestbotty.dao;

import java.util.List;

import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.dao.DigestDao;



public interface ExtDigestDao extends DigestDao {
	List<ExtDigest> retrDigestsByProjectId(String projectId);
	List<ExtDigest> retrDigestsByOwnerId(String ownerId);
	List<ExtDigest> retrDigestsByOwnerOrManagerId(String userId);
	List<ExtDigest> retrAllDigests();
}

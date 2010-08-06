package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;
import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.aggfi.digest.server.botty.google.forumbotty.admin.Command;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.gson.JsonParser;
import com.google.inject.Inject;

@SuppressWarnings("unchecked")
public class GetAllForumIds extends Command {
	private ExtDigestDao extDigestDao = null;
	 private static Cache cache = null;
	 private static final int CACHE_TIME_LIMIT = 60*60;
	 static {
		    try {
		      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
		      Map props = new HashMap();
		      props.put(GCacheFactory.EXPIRATION_DELTA, CACHE_TIME_LIMIT);
		      cache = cacheFactory.createCache(props);
		    } catch (CacheException e) {
		    }
		  }
	@Inject
	public GetAllForumIds(ExtDigestDao extDigestDao) {
		this.extDigestDao = extDigestDao;
	}

	@Override
	public JSONObject execute() throws JSONException {      
		JSONObject json = new JSONObject();
		Object o = cache.get("allForumIds");
		if(o == null){
			JSONObject digestMapJson = new JSONObject();
			List<ExtDigest> digests = extDigestDao.retrAllDigests();
			if (digests.size() > 0) {
				for (ExtDigest digest : digests) {
					String prjId = digest.getProjectId();
					String waveId = digest.getId();
					digestMapJson.put(prjId, waveId);
				}
			}else{
				digestMapJson.put("none", "none");
			}
			cache.put("allForumIds", digestMapJson.toString());
			json.put("result", digestMapJson);
		}else{
			JSONObject result = new JSONObject((String)o);
			json.put("result", result);
		}
		return json;
	}

}


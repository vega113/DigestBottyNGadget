package com.aggfi.digest.server.botty.digestbotty.admin;


import java.util.HashMap;
import com.vegalabs.general.server.command.Command;
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
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
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
		Object oTitles = cache.get("allWaveTitles");
		Object oDescriptions = cache.get("allForumDescriptions");
		if(o == null || oTitles == null || oDescriptions == null){
			JSONObject digestMapJson = new JSONObject();
			JSONObject digestMapTitleJson = new JSONObject();
			JSONObject digestMapDescriptionJson = new JSONObject();
			List<ExtDigest> digests = extDigestDao.retrAllDigests();
			if (digests.size() > 0) {
				for (ExtDigest digest : digests) {
					String prjId = digest.getProjectId();
					String waveId = digest.getId();
					String title = digest.getName();
					digestMapJson.put(prjId, waveId);
					digestMapDescriptionJson.put(waveId, digest.getDescription() + "; By " + digest.getAuthor());
					digestMapTitleJson.put(waveId, title);
				}
			}else{
				digestMapJson.put("none", "none");
			}
			cache.put("allForumIds", digestMapJson.toString());
			cache.put("allForumIdsDescriptions", digestMapDescriptionJson.toString());
			cache.put("allForumIdsTitles", digestMapTitleJson.toString());
			json.put("result", digestMapJson);
			json.put("resultTitles", digestMapTitleJson);
			json.put("resultDescriptions", digestMapDescriptionJson);
		}else{
			JSONObject result = new JSONObject((String)o);
			json.put("result", result);
			JSONObject resultTitles = new JSONObject((String)oTitles);
			json.put("resultTitles", resultTitles);
			JSONObject resultDescriptions = new JSONObject((String)oDescriptions);
			json.put("resultDescriptions", resultDescriptions);
		}
		return json;
	}

}


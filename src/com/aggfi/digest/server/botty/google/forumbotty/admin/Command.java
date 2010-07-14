package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.model.ExtDigest;

abstract public class Command {
  private static final Logger LOG = Logger.getLogger(Command.class.getName());
  private Map<String, String> params = new HashMap<String, String>();

  abstract public JSONObject execute() throws JSONException;

  public void setParams(Map<String, String> data) {
	  LOG.log(Level.FINE, "setParams" + data);
    params = data;
  }

  public Map<String, String> getParams() {
    return params;
  }
  
  protected void verifyUserAccess(ExtDigestDao extDigestDao, String projectId, String userId )throws RuntimeException{
	  List<ExtDigest> entries =  extDigestDao.retrDigestsByOwnerOrManagerId(userId);
	  for(ExtDigest entry : entries){
		  if(entry.getProjectId().equals(projectId)){
			  return;
		  }
		  LOG.info(entry.getProjectId() + ": " + projectId);
	  }
	  LOG.info(userId + " is not Authorized to manage Forum with Id: " + projectId);
	  throw new RuntimeException(userId + " is not Authorized to manage Forum with Id: " + projectId);
  }

  public String getParam(String key) {
    return this.params.get(key);
  }

  @Override
  public String toString() {
    Set<Entry<String, String>> params_ = params.entrySet();
    Iterator<Entry<String, String>> iterator = params_.iterator();
    StringBuffer str = new StringBuffer();
    while (iterator.hasNext()) {
      Entry<String, String> entry = iterator.next();
      String name = entry.getKey();
      String value = entry.getValue();
      str.append(String.format("%s=%s", name, value));
      if (iterator.hasNext()) {
        str.append(";");
      }
    }
    return str.toString();
  }
}

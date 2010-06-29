package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Singleton;

@Singleton
public class JsonExceptionHandler {
  private static final Logger LOG = Logger.getLogger(JsonExceptionHandler.class.getName());

  public void send(HttpServletResponse resp, Throwable e) throws IOException {
    LOG.log(Level.SEVERE, e.getMessage(), e);
    JSONObject json = new JSONObject();
    try {
    	Date date = new Date();
    	if(e.getMessage() != null)
    		json.put("error", date.toString() + "\n" + e.getMessage());
    	else {
    		json.put("error", date.toString() + "\n"  + e.getClass().getName());
    	}
    } catch (JSONException e1) {
      throw new RuntimeException();
    } finally {
      resp.getWriter().write(json.toString());
    }
  }

  public void send(HttpServletResponse resp, String message) throws IOException {
    Exception e = new Exception(message);
    LOG.log(Level.SEVERE, e.getMessage(), e);
    JSONObject json = new JSONObject();
    try {
      json.put("error", e.getMessage());
    } catch (JSONException e1) {
      throw new RuntimeException(e1);
    } finally {
      resp.getWriter().write(json.toString());
    }
  }
}

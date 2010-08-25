package com.aggfi.digest.server.botty.google.forumbotty.admin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.aggfi.digest.server.botty.google.forumbotty.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class JsonRpcProcessor extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(JsonRpcProcessor.class.getName());
	private JsonExceptionHandler jsonExceptionHandler = null;
	private Util util = null;
	private Injector injector = null;

	@Inject
	public JsonRpcProcessor(Injector injector, Util util, JsonExceptionHandler jsonExceptionHandler) {
		this.jsonExceptionHandler = jsonExceptionHandler;
		this.util = util;
		this.injector = injector;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOG.info(JsonRpcProcessor.class.getName() +  ", entering " + new Object[] {req, resp});
		try {
			String postBody = util.getPostBody(req);
			if (util.isNullOrEmpty(postBody)) {
				LOG.log(Level.SEVERE, "No data found in HTTP POST request.");
				IllegalArgumentException e = new IllegalArgumentException("No data found in HTTP POST request.");
				jsonExceptionHandler.send(resp, e);
				throw e;
			}

			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			JsonRpcRequest jsonRpcRequest = gson.fromJson(postBody, JsonRpcRequest.class);

			if (jsonRpcRequest != null) {
				String method = jsonRpcRequest.getMethod();
				if (method != null) {
					LOG.info("processing method " + method);
					Class<? extends Command> commandClass = CommandType.valueOfIngoreCase(method).getClazz();
					Command command = injector.getInstance(commandClass);
					command.setParams(jsonRpcRequest.getParams());

					try {
						JSONObject json = command.execute();
						resp.setContentType("application/json");
						resp.getWriter().write(json.toString());
					} catch (JSONException e) {
						jsonExceptionHandler.send(resp, e);
					} catch (IllegalArgumentException e) {
						jsonExceptionHandler.send(resp, e);
					}catch (com.google.apphosting.api.DeadlineExceededException e) {
						IllegalArgumentException iae = new IllegalArgumentException("App Engine Deadline of 30 second reached. Please rety the request.");
						jsonExceptionHandler.send(resp, iae);
					}
				}
			}
		} catch (IOException e) {
			LOG.info(this.getClass().getName() +  "doPost");
			jsonExceptionHandler.send(resp, e);
		}catch (RuntimeException e) {
			LOG.info(this.getClass().getName() +  "doPost");
			jsonExceptionHandler.send(resp, e);
		}
	}
}

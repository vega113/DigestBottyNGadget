package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.List;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification;
import com.aggfi.digest.server.botty.google.forumbotty.model.UserNotification.NotificationType;

public interface UserNotificationDao {
	public abstract UserNotification save(UserNotification entry);
	public abstract UserNotification getUserNotification(String id);
	public abstract List<UserNotification> getAllUserNotifications(String projectId,
			NotificationType type);
}

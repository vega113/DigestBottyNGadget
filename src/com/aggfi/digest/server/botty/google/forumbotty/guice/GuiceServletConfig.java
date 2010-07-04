package com.aggfi.digest.server.botty.google.forumbotty.guice;

import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDao;
import com.aggfi.digest.server.botty.digestbotty.dao.ExtDigestDaoImpl;
import com.aggfi.digest.server.botty.digestbotty.install.InstallGadgetServlet;
import com.aggfi.digest.server.botty.digestbotty.install.InstallServlet;
import com.aggfi.digest.server.botty.google.forumbotty.ForumBotty;
import com.aggfi.digest.server.botty.google.forumbotty.admin.*;
import com.aggfi.digest.server.botty.google.forumbotty.dao.*;
import com.aggfi.digest.server.botty.google.forumbotty.feeds.*;
import com.aggfi.digest.server.botty.google.forumbotty.feeds.GetPostCounts;
import com.aggfi.digest.server.botty.google.forumbotty.feeds.GetTagCounts;
import com.aggfi.digest.server.botty.google.forumbotty.notification.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class GuiceServletConfig extends GuiceServletContextListener {
  private static final Logger LOG = Logger.getLogger(GuiceServletConfig.class.getName());

  @Override
  protected Injector getInjector() {
    ServletModule servletModule = new ServletModule() {
      @Override
      protected void configureServlets() {
        serveRegex("\\/_wave/.*").with(ForumBotty.class);
        serve("/admin/jsonrpc").with(JsonRpcProcessor.class);

        serve("/send_email_updates").with(SendEmailUpdates.class);
        serve("/send_wave_updates").with(SendWaveUpdates.class);
        serve("/add_participants_task").with(AddParticipantsTask.class);
        
        serve("/feeds/get_tag_counts").with(GetTagCounts.class);        
        serve("/feeds/get_post_counts").with(GetPostCounts.class);
        serve("/feeds/get_forum_posts").with(GetForumPosts.class);
        serve("/feeds/get_latest_digest").with(GetLatestDigest.class);
        serve("/feeds/json").with(JsonGenerator.class);
        serve("/feeds/atom").with(AtomGenerator.class);       
        serve("/installNew").with(InstallServlet.class); 
        serve("/installGadget").with(InstallGadgetServlet.class); 
        
        
//        serve("/info").with(InfoServlet.class);  //TODO remove
//        serve("/info").with(InstallServlet.class);  //TODO remove
        
        //serve("/migrateTags").with(MigrateTags.class);
        //serve("/migrateTagsTask").with(MigrateTagsTask.class);
      }
    };

    AbstractModule businessModule = new AbstractModule() {
      @Override
      protected void configure() {
        bind(ForumPostDao.class).to(ForumPostDaoImpl.class);
        bind(TagDao.class).to(TagDaoImpl.class);
        bind(UserNotificationDao.class).to(UserNotificationDaoImpl.class);
        bind(AdminConfigDao.class).to(AdminConfigDaoImpl.class);
        bind(DigestDao.class).to(ExtDigestDaoImpl.class);
        bind(ExtDigestDao.class).to(ExtDigestDaoImpl.class);
      }

      @Provides
      @Singleton
      PersistenceManagerFactory getPmf() {
        return JDOHelper.getPersistenceManagerFactory("transactions-optional");
      }
    };

    return Guice.createInjector(servletModule, businessModule);
  }
}

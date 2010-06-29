package com.aggfi.digest.server.botty.google.forumbotty.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.aggfi.digest.server.botty.google.forumbotty.model.Digest;
import com.google.inject.Inject;

public class DigestDaoImpl implements DigestDao {
  private final Logger LOG = Logger.getLogger(DigestDaoImpl.class.getName());
  private PersistenceManagerFactory pmf = null;

  @Inject
  public DigestDaoImpl(PersistenceManagerFactory pmf) {
    this.pmf = pmf;
  }

  @Override
  public Digest save(Digest digest) {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      digest = pm.makePersistent(digest);
      digest = pm.detachCopy(digest);
    } finally {
      pm.close();
    }
    return digest;
  }

  @Override
  public Digest getLatestDigest() {
    PersistenceManager pm = pmf.getPersistenceManager();
    try {
      Query query = pm.newQuery(Digest.class);
      query.declareImports("import java.util.Date");
      query.setOrdering("created desc");

      List<Digest> digests = (List<Digest>) query.execute();
      if (digests.size() > 0) {
        return pm.detachCopy(digests.get(0));
      }
    } finally {
      pm.close();
    }
    return null;
  }
}

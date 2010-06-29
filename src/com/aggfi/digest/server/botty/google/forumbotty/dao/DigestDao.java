package com.aggfi.digest.server.botty.google.forumbotty.dao;

import com.aggfi.digest.server.botty.google.forumbotty.model.Digest;


public interface DigestDao {
  public abstract Digest save(Digest digest);

  public abstract Digest getLatestDigest();
}

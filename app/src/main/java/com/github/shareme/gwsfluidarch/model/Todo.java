package com.github.shareme.gwsfluidarch.model;

/**
 * Just a simple model of a to do note
 * Created by fgrott on 9/19/2016.
 */

public abstract class Todo {

  public abstract String getUid();

  public abstract String getTitle();

  public abstract Status getStatus();

  public enum Status {
    OPEN,
    CLOSED
  }


}

package com.example;

/** Main application entry point. */
public final class App {

  /**
   * Returns a greeting.
   *
   * @return the greeting string
   */
  public String getGreeting() {
    return "Hello world.";
  }

  /**
   * Main method.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(final String[] args) {
    System.out.println(new App().getGreeting());
  }
}

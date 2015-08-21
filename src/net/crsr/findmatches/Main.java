package net.crsr.findmatches;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.crsr.findmatches.nfa.BadPattern;
import net.crsr.findmatches.nfa.NFA;
import net.crsr.findmatches.nfa.NFABuilder;

public class Main {

  public static void main(String[] args) {
    try {

      final List<String> patterns = readPatterns("data/patterns.100");
      final String file = readFile("data/Lord Arthur Savile's Crime.txt");

      final NFA nfa = NFABuilder.build(patterns);
      System.out.println("Results: rule number (line number in patterns file) at ending location");
      System.out.println(nfa.matches(file, 0).toString());
      
    } catch (IOException e) {
      e.printStackTrace();
    } catch (BadPattern e) {
      e.printStackTrace();
    }
  }

  private static List<String> readPatterns(String filename) throws IOException {
    BufferedReader br = null;
    try {
      final List<String> patterns = new ArrayList<>();
      br = new BufferedReader(new FileReader(filename));
      String line = br.readLine();
      while (line != null) {
        patterns.add(line);
        line = br.readLine();
      }
      return patterns;
    } finally {
      try { if (br != null) { br.close(); } } catch (IOException e) { }
    }
  }
  
  private static String readFile(String filename) throws IOException {
    BufferedReader br = null;
    try {
      final StringBuilder sb = new StringBuilder();
      br = new BufferedReader(new FileReader(filename));
      String line = br.readLine();
      while (line != null) {
        sb.append(line).append('\n');
        line = br.readLine();
      }
      return sb.toString();
    } finally {
      try { if (br != null) { br.close(); } } catch (IOException e) { }
    }
  }
}

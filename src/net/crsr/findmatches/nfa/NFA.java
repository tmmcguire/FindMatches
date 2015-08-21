package net.crsr.findmatches.nfa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/// Construct a nondeterministic finite automaton using Thompson's construction.
///
/// Dragon book, Algorithm 3.3.
public class NFA {

  private static Integer nextState = 0;
  
  private final List<Integer> states = new ArrayList<>();
  private final Map<Integer,Transitions> transitionTable = new HashMap<>();
  private final Map<Integer,Integer> rules = new HashMap<>();
  
  private NFA() { }

  /**
   *  NFA recognizing the empty string.
   * @return a new NFA
   */
  public static NFA epsilon() {
    final NFA n = new NFA();
    n.states.add(nextState++);
    n.states.add(nextState++);
    n.transitionsForState(n.initial()).addEmptyTransition(n.terminal());
    return n;
  }
  
  /**
   *  NFA recognizing a character ch.
   * @param ch a character
   * @return a new NFA
   */
  public static NFA character(char ch) {
    final NFA n = new NFA();
    n.states.add(nextState++);
    n.states.add(nextState++);
    n.transitionsForState(n.initial()).addCharacterTransition(ch, n.terminal());
    return n;
  }
  
  /**
   *  NFA recognizing any character.
   * @return a new NFA
   */
  public static NFA dot() {
    final NFA n = new NFA();
    n.states.add(nextState++);
    n.states.add(nextState++);
    n.transitionsForState(n.initial()).addAnyTransition(n.terminal());
    return n;
  }
  
  /**
   *  NFA recognizing the disjunction of s and t: s|t
   * @param s a NFA
   * @param t a NFA
   * @return a new NFA
   */
  public static NFA disjunction(NFA s, NFA t) {
    final NFA n = new NFA();
    n.states.add(nextState++);
    n.states.add(nextState++);
    n.enclose(s);
    n.enclose(t);
    n.transitionsForState(n.initial()).addEmptyTransition(s.initial()).addEmptyTransition(t.initial());
    n.transitionsForState(s.terminal()).addEmptyTransition(n.terminal());
    n.transitionsForState(t.terminal()).addEmptyTransition(n.terminal());
    return n;
  }
  
  /**
   * NFA recognizing s followed by t: st
   * @param s a NFA
   * @param t a NFA
   * @return a new NFA
   */
  public static NFA sequence(NFA s, NFA t) {
    final NFA n = new NFA();
    n.states.add(nextState++);
    n.states.add(nextState++);
    n.enclose(s);
    n.enclose(t);
    n.transitionsForState(n.initial()).addEmptyTransition(s.initial());
    n.transitionsForState(s.terminal()).addEmptyTransition(t.initial());
    n.transitionsForState(t.terminal()).addEmptyTransition(n.terminal());
    return n;
  }
  
  /**
   * NFA recognizing zero or more occurrences of s: s*;
   * @param s a NFA
   * @return a new NFA
   */
  public static NFA star(NFA s) {
    final NFA n = new NFA();
    n.states.add(nextState++);
    n.states.add(nextState++);
    n.enclose(s);
    n.transitionsForState(n.initial()).addEmptyTransition(s.initial()).addEmptyTransition(n.terminal());
    n.transitionsForState(s.terminal()).addEmptyTransition(s.initial()).addEmptyTransition(n.terminal());
    return n;
  }
  
  public Integer initial() { return states.get(0); }
  
  public Integer terminal() { return states.get(1); }
  
  public void setRule(int state, int rule) {
    rules.put(state, rule);
  }
  
  public Transitions transitionsForState(Integer state) {
    Transitions tTable = transitionTable.get(state);
    if (tTable == null) {
      tTable = new Transitions();
      transitionTable.put(state, tTable);
    }
    return tTable;
  }
  
  public List<Pair<Integer,Integer>> matches(String str, int i) {
    int j = i;
    final ArrayList<Pair<Integer,Integer>> results = new ArrayList<>();
    Set<Integer> current = closure(initial());
    while (!current.isEmpty() && j < str.length()) {
      current = move(current, str.charAt(j));
      j++;
      if (current.contains(terminal())) {
        {
          String substring = str.substring(i,j);
          int len = substring.length();
          if (len > 10) {
            substring = "..." + substring.substring(len-10, len);
          }
          System.out.print("Found: '" + substring + "' (" + current.size() + ") ");
          for (Integer state : current) {
            final Integer rule = rules.get(state);
            if (rule != null) {
              results.add(Pair.n(rule, j));
              System.out.print(" with rule: " + rule + " at " + i + "-" + j);
            }
          }
          System.out.println();
        }
      } else if (!current.isEmpty()) {
        // System.out.println("  Current: " + current);
      }
    }
    return results;
  }
  
  public Set<Integer> closure(Integer state) {
    final HashSet<Integer> set = new HashSet<>();
    set.add(state);
    return closure(set);
  }

  public Set<Integer> closure(Set<Integer> states) {
    final Queue<Integer> todo = new ArrayDeque<>(states);
    final Set<Integer> closure = new HashSet<>();
    while (!todo.isEmpty()) {
      final Integer state = todo.remove();
      if (closure.contains(state)) { continue; }
      closure.add(state);
      todo.addAll( transitionsForState(state).epsilonTransitions() );
    }
    return closure;
  }
  
  public Set<Integer> move(Set<Integer> states, Character ch) {
    final Queue<Integer> todo = new ArrayDeque<>(states);
    final Set<Integer> newStates = new HashSet<>();
    while (!todo.isEmpty()) {
      final Integer state = todo.remove();
      newStates.addAll( transitionsForState(state).transitionsForCharacter(ch) );
    }
    return closure(newStates);
  }
  
  private void enclose(NFA n) {
    addStates(n);
    addTransitions(n);
    addRules(n);
  }
  
  private void addStates(NFA n) {
    states.addAll(n.states);
  }
  
  private void addTransitions(NFA n) {
    for (Integer st : n.states) {
      final Transitions transitions = n.transitionTable.get(st);
      if (transitions != null) {
        transitionTable.put(st, transitions.duplicate());
      }
    }
  }
  
  private void addRules(NFA n) {
    rules.putAll(n.rules);
  }
}

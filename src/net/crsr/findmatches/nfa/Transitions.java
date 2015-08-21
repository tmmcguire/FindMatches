package net.crsr.findmatches.nfa;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Transitions {
  private final Set<Integer> epsilon = new HashSet<>();
  private final Map<Character, Set<Integer>> transitions = new HashMap<>();
  private final Set<Integer> anyChar = new HashSet<>();
  
  public Transitions addEmptyTransition(Integer s) {
    epsilon.add(s);
    return this;
  }
  
  public Transitions addCharacterTransition(Character ch, Integer s) {
    Set<Integer> set = transitions.get(ch);
    if (set == null) {
      set = new HashSet<>();
      transitions.put(ch, set);
    }
    set.add(s);
    return this;
  }
  
  public Transitions addAnyTransition(Integer s) {
    anyChar.add(s);
    return this;
  }
  
  public Set<Integer> epsilonTransitions() {
    return Collections.unmodifiableSet(epsilon);
  }
  
  public Set<Integer> transitionsForCharacter(Character ch) {
    final Set<Integer> results = new HashSet<>(anyChar);
    results.addAll( transitions.getOrDefault(ch, Collections.<Integer>emptySet()) );
    return results;
  }
  
  public Transitions duplicate() {
    final Transitions t = new Transitions();
    t.epsilon.addAll(epsilon);
    t.transitions.putAll(transitions);
    t.anyChar.addAll(anyChar);
    return t;
  }
}
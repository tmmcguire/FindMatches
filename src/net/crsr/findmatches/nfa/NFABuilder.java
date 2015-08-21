package net.crsr.findmatches.nfa;

import java.util.List;

public class NFABuilder {
  
  public static NFA build(List<String> patterns) throws BadPattern {
    if (patterns.isEmpty()) { return NFA.epsilon(); }
    NFA cur = build(patterns.get(0));
    cur.setRule(cur.terminal(), 0);
    for (int i = 1; i < patterns.size(); ++i) {
      final NFA n = build(patterns.get(i));
      n.setRule(n.terminal(), i);
      cur = NFA.disjunction(cur, n);
    }
    cur = NFA.sequence(NFA.star(NFA.dot()), cur);
    return cur;
  }
  
  public static NFA build(String pattern) throws BadPattern {
    final Pair<NFA,Integer> p = parseNext(pattern, 0);
    NFA cur = p.l;
    int i = p.r;
    while (i < pattern.length()) {
      final Pair<NFA,Integer> q = parseNext(pattern, i);
      cur = NFA.sequence(cur, q.l);
      i = q.r;
    }
    return cur;
  }
  
  private static Pair<NFA,Integer> parseNext(String pattern, int i) throws BadPattern {
    NFA cur = NFA.epsilon();
    if (i < pattern.length()) {
      char ch = pattern.charAt(i);
      if (ch == '(') {
        i++;
        int end = findClosingParen(pattern, i);
        cur = build(pattern.substring(i, end));
        i = end + 1;
      } else if (ch == '.') {
        cur = NFA.dot();
        i++;
     } else if (ch == '*' || ch == ')' || ch == '|') {
        throw new BadPattern("Misformed regular expression: " + pattern);
      } else {
        cur = NFA.character(ch);
        i++;
      }
      
      while (i < pattern.length() && (pattern.charAt(i) == '*' || pattern.charAt(i) == '|')) {
        ch = pattern.charAt(i);
        if (ch == '*') {
          cur = NFA.star(cur);
          i++;
        } else if (ch == '|') {
          i++;
          Pair<NFA,Integer> p = parseNext(pattern, i);
          cur = NFA.disjunction(cur, p.l);
          i = p.r;
        }
      }
    }
    return Pair.n(cur, i);
  }
  
  private static int findClosingParen(String pattern, int i) throws BadPattern {
    int counter = 0;
    while (i < pattern.length()) {
      final char ch = pattern.charAt(i);
      if (ch == ')' && counter == 0) {
        return i;
      } else if (ch == ')' && counter > 0) {
        counter--;
      } else if (ch == '(') {
        counter++;
      }
      i++;
    }
    throw new BadPattern("Missing closing parenthesis: " + pattern);
  }
}

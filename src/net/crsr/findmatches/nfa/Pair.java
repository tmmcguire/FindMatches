package net.crsr.findmatches.nfa;

class Pair<L,R> {
  public final L l;
  public final R r;
  
  static <L,R> Pair<L,R> n(L l, R r) { return new Pair<>(l,r); }
  
  public Pair(L l, R r) {
    this.l = l;
    this.r = r;
  }
  
  public String toString() {
    return String.format("%d at %d\n", l, r);
  }
}
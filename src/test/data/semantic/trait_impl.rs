impl Printable for int {
  fn print(&self) { println(fmt!("%d", *self)) }
}
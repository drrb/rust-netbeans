trait Printable {
  fn print(&self) { println(fmt!("%d", *self)) }
  fn implement_me(&self);
}
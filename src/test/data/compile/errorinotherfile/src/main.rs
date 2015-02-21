mod in_editor;

fn main() {
    nonexistent_method(); //Shouldn't be reported because it's not in the editor
}
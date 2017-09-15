use std::io;

/// Entry point
fn main() {
    /*
        This program does something important
        and needs to be documented
    */
    println!("Guess the number!"); // Prints a line of text
}

fn other() {
    //! Not the entry point
    println!("Guess the number!"); // Prints a line of text
}


/**
 * a method that does something else
 */
fn yet_another() {

}

fn still_another() {
    /*!
     * a method that does something else still
     */
}
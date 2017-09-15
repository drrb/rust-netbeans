mod a_module;
pub mod a_public_module;

mod an_inline_module {
    fn a_namespaced_function() {

    }
}

pub mod a_public_inline_module {

}


mod an_outer_module {
    mod a_nested_module {
        mod a_deeply_nested_module {

        }
    }
}
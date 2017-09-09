use module_a;
use module_a as moda;
use module_a::submodule::SomeType;
use module_a::submodule::OtherType as otype;
use module_a::submodule2::*;
use module_a::submodule3::{Type1,Type2, Type3};
use self::my_submodule::*;
pub use module_b;
pub use module_b as modb;
pub use module_b::submodule::SomeType;
pub use module_b::submodule::OtherType as otype2;
pub use module_b::submodule2::*;
pub use module_b::submodule3::{Type1,Type2,Type3};
pub use self::my_submodule2::*;


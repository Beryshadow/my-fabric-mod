// rustlib/src/lib.rs

#[no_mangle]
pub extern "C" fn double_up(input: i32) -> i32 {
    input * 2
}

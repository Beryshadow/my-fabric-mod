// package com.example;

// import jnr.ffi.LibraryLoader;

// public class HelloRust {
//     public interface LibRust {
//         int double_up(int x);
//     }

//     /** Calls the native Rust library. */
//     public static int callDouble(int input) {
//         // Adjust path to your built library (without the platform‑specific extension for JNR).
//         String libPath = "build/libs/native/libmyrustlib";
//         LibRust lib = LibraryLoader.create(LibRust.class).load(libPath);
//         return lib.double_up(input);
//         // return input * 2;
//     }
// }


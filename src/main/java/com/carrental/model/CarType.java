package com.carrental.model;

/**
 * CarType enumerates the different categories of vehicles that can be
 * reserved through the system. Keeping the enum separate makes it
 * straightforward to add new types in the future and allows the compiler
 * to enforce valid values on reservation requests.
 */
public enum CarType {
    SEDAN,
    SUV,
    VAN
}
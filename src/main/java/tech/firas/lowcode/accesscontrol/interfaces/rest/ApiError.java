package tech.firas.lowcode.accesscontrol.interfaces.rest;

public record ApiError(int status, String error, String message) {
}

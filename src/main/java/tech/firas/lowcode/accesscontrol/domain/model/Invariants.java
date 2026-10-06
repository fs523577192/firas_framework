package tech.firas.lowcode.accesscontrol.domain.model;

final class Invariants {

    private Invariants() {
    }

    static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    static void requirePresent(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}

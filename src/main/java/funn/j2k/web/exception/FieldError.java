package funn.j2k.web.exception;

public record FieldError(String field, String code, String message) {}
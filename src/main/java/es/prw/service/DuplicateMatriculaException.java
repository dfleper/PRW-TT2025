package es.prw.service;

public class DuplicateMatriculaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateMatriculaException(String message) {
        super(message);
    }
}

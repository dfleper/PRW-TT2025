package es.prw.features.cliente.vehiculos.exception;

public class DuplicateMatriculaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateMatriculaException(String message) {
		super(message);
	}
}

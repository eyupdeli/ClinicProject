package clinic;


/**
 * Exception class used to notify that a Patient is not available
 */
public class NoSuchPatient extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NoSuchPatient(String ssn) {
		super("No patient found with ssn: " + ssn);
	}
}

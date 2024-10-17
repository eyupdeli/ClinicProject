package clinic;

/**
 * Exception class used to notify that a Doctor is not available
 */
public class NoSuchDoctor extends Exception {

	public NoSuchDoctor() {
	}

	public NoSuchDoctor(int id) {
		super("No doctor found with id: " + id);
	}
	
	public NoSuchDoctor(String ssn) {
		super("No doctor assigned for patient: " + ssn);
	}

	private static final long serialVersionUID = 1L;
}

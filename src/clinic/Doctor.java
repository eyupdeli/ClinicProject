package clinic;

public class Doctor extends Person {

	private Integer id;
	private String specialization;
	
	public Doctor(String firstName, String lastName, String ssn, int docID, String specialization) {
		super(firstName, lastName, ssn);
		this.id = docID;
		this.specialization = specialization;
	}

	public Integer getId() {
		return id;
	}

	public String getSpecialization() {
		return specialization;
	}
	
	@Override
	public String toString() {
		return String.format("%s [%d]: %s", super.toString(), id, specialization);
	}
}

package clinic;

public class Person {
	
	private String firstName;
	private String lastName;
	private String ssn;
	
	public Person(String firstName, String lastName, String ssn) {
		this.firstName = firstName;
	    this.lastName = lastName;
	    this.ssn = ssn;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getSsn() {
		return ssn;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s (%s)", lastName, firstName, ssn);
	}
}

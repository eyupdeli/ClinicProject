package clinic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Represents a clinic with patients and doctors.
 * 
 */


public class Clinic {

	/**
	 * Add a new clinic patient.
	 * 
	 * @param first first name of the patient
	 * @param last last name of the patient
	 * @param ssn SSN number of the patient
	 */
	
	private Map<String, Patient> patients = new HashMap<>();
	private Map<Integer, Doctor> doctors = new HashMap<>();
	private Map<Doctor, List<Patient>> doctorToPatients = new HashMap<>();
	
	public void addPatient(String first, String last, String ssn) {
		Patient patient = new Patient(first, last, ssn);
		patients.put(ssn, patient);
 	}


	/**
	 * Retrieves a patient information
	 * 
	 * @param ssn SSN of the patient
	 * @return the object representing the patient
	 * @throws NoSuchPatient in case of no patient with matching SSN
	 */
	public String getPatient(String ssn) throws NoSuchPatient {
		Patient patient= patients.get(ssn);
		
		if(patient == null) 
			throw new NoSuchPatient(ssn);
		
		return patient.toString();
	}

	/**
	 * Add a new doctor working at the clinic
	 * 
	 * @param first first name of the doctor
	 * @param last last name of the doctor
	 * @param ssn SSN number of the doctor
	 * @param docID unique ID of the doctor
	 * @param specialization doctor's specialization
	 */
	public void addDoctor(String first, String last, String ssn, int docID, String specialization) {
		Doctor doctor = new Doctor(last, first, ssn, docID, specialization);
		doctors.put(docID, doctor);
		doctorToPatients.put(doctor, new ArrayList<>()); // Initialize empty patient list.
	}

	/**
	 * Retrieves information about a doctor
	 * 
	 * @param docID ID of the doctor
	 * @return object with information about the doctor
	 * @throws NoSuchDoctor in case no doctor exists with a matching ID
	 */
	public String getDoctor(int docID) throws NoSuchDoctor {
		Doctor doctor = doctors.get(docID);
		
		if(doctor == null)
			throw new NoSuchDoctor(docID);
		
		return doctor.toString();
	}
	
	/**
	 * Assign a given doctor to a patient
	 * 
	 * @param ssn SSN of the patient
	 * @param docID ID of the doctor
	 * @throws NoSuchPatient in case of not patient with matching SSN
	 * @throws NoSuchDoctor in case no doctor exists with a matching ID
	 */
	public void assignPatientToDoctor(String ssn, int docID) throws NoSuchPatient, NoSuchDoctor {
		
		Patient patient = patients.get(ssn);
		if(patient == null)
			throw new NoSuchPatient(ssn);
		
		Doctor doctor = doctors.get(docID);
		if(doctor == null)
			throw new NoSuchDoctor(docID);
		
		List<Patient> assignedPatients = doctorToPatients.get(doctor);
		
		if(assignedPatients == null) {
			assignedPatients = new ArrayList<>();
			doctorToPatients.put(doctor, assignedPatients);
		}
		
		assignedPatients.add(patient);  //reference
		
	}

	/**
	 * Retrieves the id of the doctor assigned to a given patient.
	 * 
	 * @param ssn SSN of the patient
	 * @return id of the doctor
	 * @throws NoSuchPatient in case of not patient with matching SSN
	 * @throws NoSuchDoctor in case no doctor has been assigned to the patient
	 */
	public int getAssignedDoctor(String ssn) throws NoSuchPatient, NoSuchDoctor {
		
		Patient patient = patients.get(ssn);
		if(patient == null) 
			throw new NoSuchPatient(ssn);

		return doctorToPatients.keySet().stream()
				.filter(d -> doctorToPatients.get(d).contains(patient))
				.map(Doctor::getId)
				.findFirst()
				.orElseThrow(() -> new NoSuchDoctor());

	}
	
	/**
	 * Retrieves the patients assigned to a doctor
	 * 
	 * @param id ID of the doctor
	 * @return collection of patient SSNs
	 * @throws NoSuchDoctor in case the {@code id} does not match any doctor 
	 */
	public Collection<String> getAssignedPatients(int id) throws NoSuchDoctor {
		
		Doctor doctor = doctors.get(id);
		
		if(doctor == null)
			throw new NoSuchDoctor(id);
		
		List<Patient> assignedPatients = doctorToPatients.get(doctor);
		
		if(assignedPatients == null) 
			assignedPatients = new ArrayList<>();
		
		return assignedPatients.stream()
				.map(Patient::getSsn)
				.collect(Collectors.toList());
	}

	
		/**
		 * Loads data about doctors and patients from the given stream.
		 * <p>
		 * The text file is organized by rows, each row contains info about
		 * either a patient or a doctor.</p>
		 * <p>
		 * Rows containing a patient's info begin with letter {@code "P"} followed by first name,
		 * last name, and SSN. Rows containing doctor's info start with letter {@code "M"},
		 * followed by badge ID, first name, last name, SSN, and speciality.<br>
		 * The elements on a line are separated by the {@code ';'} character possibly
		 * surrounded by spaces that should be ignored.</p>
		 * <p>
		 * In case of error in the data present on a given row, the method should be able
		 * to ignore the row and skip to the next one.<br>
	
		 * 
		 * @param reader reader linked to the file to be read
		 * @throws IOException in case of IO error
		 */

	public int loadData(Reader reader) throws IOException {
		
		String line;
		int processedLines = 0;
	    
    	BufferedReader br = new BufferedReader(reader);

        while ((line = br.readLine()) != null) {
            line = line.trim();
            String[] parts = line.split(";");

	        if (line.isEmpty() || (line.startsWith("P") && (parts.length != 4)) || (line.startsWith("M") && parts.length != 6)) {
                System.err.println("Skipping invalid line: " + line);
                continue;
            }
            if (line.startsWith("P")) {
                addPatient(parts[1].trim(), parts[2].trim(), parts[3].trim());
            } else if (line.startsWith("M")) {
                int docID = Integer.parseInt(parts[1].trim());
                addDoctor(parts[2].trim(), parts[3].trim(), parts[4].trim(), docID, parts[5].trim());
            } else {
                throw new IllegalArgumentException("Unknown entry: " + line);
            }
            processedLines++;	
        }
	    return processedLines;
	}

	
	/**
	 * Loads data about doctors and patients from the given stream.
	 * <p>
	 * The text file is organized by rows, each row contains info about
	 * either a patient or a doctor.</p>
	 * <p>
	 * Rows containing a patient's info begin with letter {@code "P"} followed by first name,
	 * last name, and SSN. Rows containing doctor's info start with letter {@code "M"},
	 * followed by badge ID, first name, last name, SSN, and speciality.<br>
	 * The elements on a line are separated by the {@code ';'} character possibly
	 * surrounded by spaces that should be ignored.</p>
	 * <p>
	 * In case of error in the data present on a given row, the method calls the
	 * {@link ErrorListener#offending} method passing the line itself,
	 * ignores the row, and skip to the next one.<br>
	 * 
	 * @param reader reader linked to the file to be read
	 * @param listener listener used for wrong line notifications
	 * @throws IOException in case of IO error
	 */

	public int loadData(Reader reader, ErrorListener listener) throws IOException {
		
		String line;
	    int processedLines = 0;
	    int lineNumber = 0;
		
	    BufferedReader br = new BufferedReader(reader);

	    while ((line = br.readLine()) != null) {
	        lineNumber++;
	        line = line.trim();
            String[] parts = line.split(";");
	        
	        if (line.isEmpty() || (line.startsWith("P") && (parts.length != 4)) || (line.startsWith("M") && parts.length != 6)) {
	            listener.offending(lineNumber, line);
	            continue;
	        }
        	if (line.startsWith("P") && parts.length == 4) {
                addPatient(parts[1], parts[2], parts[3]);
            }
        	else if (line.startsWith("M")  && parts.length == 6 ) {
            	addDoctor(parts[2], parts[3], parts[4], Integer.parseInt(parts[1]), parts[5]);
            }
            else {
                listener.offending(lineNumber, line);
            }
            processedLines++;
	    }
		return processedLines;
	}

	/**
	 * Retrieves the collection of doctors that have no patient at all.
	 * The doctors are returned sorted in alphabetical order
	 * 
	 * @return the collection of doctors' ids
	 */
	
	public Collection<Integer> idleDoctors(){
		
		return doctorToPatients.keySet().stream()
				.filter(doctor -> doctorToPatients.get(doctor).isEmpty())
				.sorted(Comparator.comparing(Doctor::getLastName))
				.map(Doctor::getId)
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves the collection of doctors having a number of patients larger than the average.
	 * 
	 * @return  the collection of doctors' ids
	 */
	public Collection<Integer> busyDoctors(){
		
		long numberOfDoctorWithPatients = doctorToPatients.keySet().stream()
				.filter(doctor -> !doctorToPatients.get(doctor).isEmpty())
				.count();
		
		long sumOfAllPatients = doctorToPatients.values().stream()
				.mapToLong(list -> list.size())
				.sum();
			
		double average = (double) sumOfAllPatients / numberOfDoctorWithPatients;
		
		return doctorToPatients.keySet().stream()
				.filter(doctor -> doctorToPatients.get(doctor).size() > average)
				.map(Doctor :: getId)
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves the information about doctors and relative number of assigned patients.
	 * <p>
	 * The method returns list of strings formatted as "{@code ### : ID SURNAME NAME}" where {@code ###}
	 * represent the number of patients (printed on three characters).
	 * <p>
	 * The list is sorted by decreasing number of patients.
	 * 
	 * @return the collection of strings with information about doctors and patients count
	 */
	
	public Collection<String> doctorsByNumPatients(){
		
		return doctorToPatients.keySet().stream()
				.filter(doctor -> doctorToPatients.get(doctor) != null)
				.sorted((d1, d2) -> (Integer.compare(doctorToPatients.get(d2).size(), doctorToPatients.get(d1).size())))
				.map(doctor -> String.format("%03d : %d %s %s", 
						 doctorToPatients.get(doctor).size(), doctor.getId(), doctor.getLastName(), doctor.getFirstName()))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves the number of patients per (their doctor's)  speciality
	 * <p>
	 * The information is 	a collections of strings structured as {@code ### - SPECIALITY}
	 * where {@code SPECIALITY} is the name of the speciality and 
	 * {@code ###} is the number of patients cured by doctors with such speciality (printed on three characters).
	 * <p>
	 * The elements are sorted first by decreasing count and then by alphabetic speciality.
	 * 
	 * @return the collection of strings with speciality and patient count information.
	 */
	
	public Collection<String> countPatientsPerSpecialization(){

		Map<String, Integer> specAssignedTotalPatient = new HashMap<>();
		
		doctorToPatients.keySet().stream()
			.filter(d -> doctorToPatients.get(d) != null)
			.forEach(doctor -> {
				String spec = doctor.getSpecialization();
				Integer number = doctorToPatients.get(doctor).size();
				specAssignedTotalPatient.merge(spec, number, Integer::sum); 
			});
		
		return specAssignedTotalPatient.entrySet().stream()
		      .filter(entry -> entry.getValue() > 0) 
			  .sorted(Map.Entry.<String,Integer>comparingByValue().reversed()
						.thenComparing(Map.Entry.comparingByKey()))	
		      .map(entry -> String.format("%03d - %s", entry.getValue(), entry.getKey()))
			  .collect(Collectors.toList());
		
		
//solution provided by using keyset()
		
//		return specAssignedTotalPatient.keySet().stream()
//					.filter(spec -> specAssignedTotalPatient.get(spec) > 0 )
//					.sorted(Comparator.comparing((String spec) -> specAssignedTotalPatient.get(spec))
//							.reversed()
//							.thenComparing(Comparator.naturalOrder()))
//					.map(key -> String.format("%03d - %s", specAssignedTotalPatient.get(key), key))
//					.collect(Collectors.toList());

	}

}

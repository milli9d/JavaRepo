package finalproject.entities;

public class Person implements java.io.Serializable {

	private static final long serialVersionUID = 4190276780070819093L;

	// this is a person object that you will construct with data from the DB
	// table. The "sent" column is unnecessary. It's just a person with
	// a first name, last name, age, city, and ID.

	private String firstName, lastName, city;
	private boolean sent;
	private int age, id;
	private String bord = " \n======================";

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the sent
	 */
	public boolean isSent() {
		return sent;
	}

	/**
	 * @param sent the sent to set
	 */
	public void setSent(boolean sent) {
		this.sent = sent;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public Person() {
		this.firstName = "";
		this.lastName = "";
		this.city = "";
		this.age = 0;
		this.id = 0;
		this.sent = false;

	}

	public Person(String first, String last, int age, String city, int id) {
		this.firstName = first;
		this.lastName = last;
		this.age = age;
		this.city = city;
		this.id = id;
		this.sent = false;
	}

	public String toString() {
		return "Person Info:" + bord + "\nID = " + this.id + "\nName = " + this.firstName + " " + this.lastName
				+ "\nAge = " + this.age + "\nCity = " + this.city + "\nSent = " + this.sent + bord;

	}

	public static void main(String args[]) {

		Person a = new Person("Milind", "Singh", 25, "New York", 01);
		System.out.println(a);

	}

}

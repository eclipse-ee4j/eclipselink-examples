/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/

package eclipselink.example.moxy.json.jsonprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.oxm.json.JsonParserSource;

import eclipselink.example.moxy.json.jsonprocessing.model.Customer;
import eclipselink.example.moxy.json.jsonprocessing.model.PhoneNumber;

/**
 * This example executes the following basic steps:
 *
 * 1) Create a Customer instance that embeds a list of Phone Numbers
 *
 * 2) Use the JSON Writer API to write the customer from a file in JSON format
 * using the JSR 353 JSON Writer API.
 *
 * 3) Use the JSON Reader API to read the JSON representation of a customer from
 * a file using the JSR 353 JSON Reader API
 *
 * 4) Use the JSR 353 JSON Generator API to write a Customer to a file in JSON
 * format
 *
 * 5) Use the JSR 353 JSON Parser API to read the JSON representation of a
 * customer from a file
 *
 * 6) Use MOXy to write a customer to a file in JSON format
 *
 * 7) Use MOXy to read a customer from a JSON-formatted file
 *
 * @author johnclingan
 * @since EclipseLink 2.6.0
 */

public class Main {
	public static final String FILENAME_WRITER = "target" + File.separator
			+ "customer.writer.json";

	public static final String FILENAME_GENERATOR = "target" + File.separator
			+ "customer.generator.json";

	public static final String FILENAME_MOXY = "target" + File.separator
			+ "customer.moxy.json";

	public static void main(String[] args) throws Exception {

		System.out.println();
		System.out.println("Running EclipseLink MOXy JSON Processing Example");

		// Step 1 - Create a customer
		Customer customer = createCustomer();

		// Step 2 - Write Customer to a file in JSON format using JsonWriter
		try (JsonWriter writer = Json.createWriter(new FileOutputStream(
				FILENAME_WRITER))) {
			JsonObject jsonCustomer = createCustomerJsonObject(customer);
			writer.writeObject(jsonCustomer);
			System.out.println();
		    System.out.println("*******************************************************************************");
			System.out.println("*****************       Using JSON reader/writer       ************************");
			System.out.println("*");
			System.out.println("* Customer instance written to " + FILENAME_WRITER);
		}

		// Step 3 - Read JSON representation of a customer from a file using
		// JsonReader and create a customer instance
		try (JsonReader reader = Json.createReader(new FileInputStream(
				FILENAME_WRITER))) {
			JsonObject jsonObject = reader.readObject();
			Customer customer2 = createCustomerFromJsonObject(jsonObject);
			System.out.println("*");
			System.out.println("* Customer read from " + FILENAME_WRITER);
			System.out.println("* " + customer2);
		    System.out.println("*******************************************************************************");
			System.out.println();
		}

		// Step 4 - Write a Customer to a file using the JsonGenerator API
		try (JsonGenerator generator = Json
				.createGenerator(new FileOutputStream(FILENAME_GENERATOR))) {
			generateCustomer(customer, generator);
			System.out.println();
		    System.out.println("*******************************************************************************");
			System.out.println("*****************      Using JSON paser/generator     *************************");
			System.out.println("*");
			System.out.println("* Customer instance written to " + FILENAME_GENERATOR);
		}

		// Step 5 - Read JSON representation of customer from a file
		// using JsonParser
		Customer parsedCustomer = parseJsonCustomer(FILENAME_GENERATOR);
		System.out.println("*");
		System.out.println("* Customer read from " + FILENAME_GENERATOR);
		System.out.println("* " + parsedCustomer);
		System.out.println("*******************************************************************************");
		System.out.println();

		// Step 6 - Write Customer using MOXy
		System.out.println("*******************************************************************************");
		System.out.println("************************      Using MOXy     **********************************");
		System.out.println("*");
		System.out.println("* Writing customer to " + FILENAME_MOXY);
		writeCustomerUsingMoxy(customer, FILENAME_MOXY);

		// Step 7 - Read Customer using MOXy

		System.out.println("*");
		System.out.println("* Customer read from " + FILENAME_MOXY + " is:");
		System.out.println("*" + parseCustomerUsingMoxy(FILENAME_WRITER));
		System.out.println("*******************************************************************************");

		System.out.println();
		System.out.println();
	}

	/********************************************
	 * Create an instance of a customer
	 ********************************************/

	private static Customer createCustomer() {
		PhoneNumber p1 = new PhoneNumber("555-1111", "Mobile");
		PhoneNumber p2 = new PhoneNumber("555-2222", "Home");

		return new Customer(1, "John", "Doe", Arrays.asList(p1, p2));
	}

	/********************************************
	 * Creates a JsonObject from Customer instance
	 ********************************************/

	private static JsonObject createCustomerJsonObject(Customer customer) {

		JsonArrayBuilder phoneArrayBuilder = Json.createArrayBuilder();

		for (PhoneNumber p : customer.getPhoneNumbers()) {
			JsonObjectBuilder phoneBuilder = Json.createObjectBuilder();
			phoneBuilder.add("number", p.getNumber()).add("type", p.getType());
			phoneArrayBuilder.add(phoneBuilder);
		}

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("id", customer.getId())
				.add("firstName", customer.getFirstName())
				.add("lastName", customer.getLastName())
				.add("phoneNumbers", phoneArrayBuilder);

		return builder.build();
	}

	/*****************************************************
	 * Create an instance of a customer from a jsonObject
	 *****************************************************/

	private static Customer createCustomerFromJsonObject(JsonObject jsonObject) {
		Customer customer = new Customer();

		customer.setId(jsonObject.getInt("id"));
		customer.setFirstName(jsonObject.getString("firstName"));
		customer.setLastName(jsonObject.getString("lastName"));

		JsonArray jsonPhoneArray = jsonObject.getJsonArray("phoneNumbers");

		for (JsonObject jsonPhone : jsonPhoneArray
				.getValuesAs(JsonObject.class)) {
			PhoneNumber phoneNumber = new PhoneNumber();

			phoneNumber.setNumber(jsonPhone.getString("number"));
			phoneNumber.setType(jsonPhone.getString("type"));

			customer.getPhoneNumbers().add(phoneNumber);
		}

		return customer;
	}

	/*************************************************************************
	 * Read a JSON-formatted customer into a Customer instance using the JSON
	 * parsing API. Parsing JSON takes more work (more code), but has two
	 * distinct advantages:
	 *
	 * 1) A potentially very large JsonObject does not have to be stored in
	 * memory
	 *
	 * 2) If only a subset of data is needed, parsing can immediately return
	 *
	 *************************************************************************/

	private static Customer parseJsonCustomer(String filename) {
		Customer customer = new Customer();
		String key = ""; // They key of the JSON key/value pair
		PhoneNumber phoneNumber = new PhoneNumber();
		boolean inPhoneNumberArray = false;

		try (JsonParser parser = Json
				.createParser(new FileInputStream(filename))) {

			while (parser.hasNext()) {
				switch (parser.next()) {
				case KEY_NAME:
					key = parser.getString();
					break;

				case VALUE_NUMBER:
					// There is only one "number" in customer - id - so just
					// assume it is the id
					customer.setId(parser.getInt());
					break;

				case VALUE_STRING:
					// Get the "value" of the key/value pair
					String value = parser.getString();

					switch (key) {
					case "firstName":
						customer.setFirstName(value);
						break;
					case "lastName":
						customer.setLastName(value);
						break;

					case "number":
						phoneNumber.setNumber(value);
						break;

					case "type":
						phoneNumber.setType(value);
						break;
					}

					break;

				case START_OBJECT:
					if (inPhoneNumberArray) {
						phoneNumber = new PhoneNumber();
					}
					break;

				case END_OBJECT:
					if (inPhoneNumberArray) {
						customer.getPhoneNumbers().add(phoneNumber);
					}
					break;

				case START_ARRAY:
					// There is only one JSON Array in Customer - the list of
					// phone numbers so we can hard-code this
					inPhoneNumberArray = true;
					break;

				case END_ARRAY:
					// There is only one JSON Array in Customer - the list of
					// phone numbers, so we can hard-code adding phone number
					inPhoneNumberArray = false;
					break;

				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex);
		}

		return customer;
	}

	/*************************************************************************
	 * Write a customer instance to a file using the JsonGenerator API
	 *************************************************************************/
	private static void generateCustomer(Customer customer,
			JsonGenerator generator) {
		generator.writeStartObject().write("id", customer.getId())
				.write("firstName", customer.getFirstName())
				.write("lastName", customer.getLastName())
				.writeStartArray("phoneNumbers");

		for (PhoneNumber phoneNumber : customer.getPhoneNumbers()) {
			generator.writeStartObject()
					.write("number", phoneNumber.getNumber())
					.write("type", phoneNumber.getType()).writeEnd();
		}

		generator.writeEnd() // Write end of phone number array
				.writeEnd(); // Write end of customer
	}

	private static Customer parseCustomerUsingMoxy(String filename)
			throws FileNotFoundException, JAXBException {
		StreamSource source = new StreamSource(new FileInputStream(filename));
		Map<String, Object> jaxbProperties = new HashMap<String, Object>();

		jaxbProperties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
		jaxbProperties
				.put(JAXBContextProperties.MEDIA_TYPE, "application/json");

		JAXBContext context = JAXBContextFactory.createContext(
				new Class[] { Customer.class }, jaxbProperties);

		Unmarshaller unmarshaller = context.createUnmarshaller();

		JAXBElement<Customer> elementCustomer = unmarshaller.unmarshal(source,
				Customer.class);

		return elementCustomer.getValue();
	}

	private static void writeCustomerUsingMoxy(Customer customer,
			String filename) throws FileNotFoundException, JAXBException {
		Map<String, Object> jaxbProperties = new HashMap<String, Object>();

		// set media type
		jaxbProperties
				.put(JAXBContextProperties.MEDIA_TYPE, "application/json");

		JAXBContext context = JAXBContextFactory.createContext(
				new Class[] { Customer.class }, jaxbProperties);

		Marshaller marshaller = context.createMarshaller();

		marshaller.marshal(customer, new File(filename));
	}
}
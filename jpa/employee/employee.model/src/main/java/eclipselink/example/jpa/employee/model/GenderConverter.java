/*******************************************************************************
 * Copyright (c) 1998, 2011, 2014 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 	dclarke - initial JPA Employee example using XML (bug 217884)
 *      jclingan - Updated to support JPA 2.1 Attribute Converters
 ******************************************************************************/
package eclipselink.example.jpa.employee.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Simple enum used in an ObjectTypeConverter
 *
 * @see Employee#gender
 * @author jclingan
 * @since 2.5.2
 */

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

	@Override
	public String convertToDatabaseColumn(Gender gender) {
		switch (gender) {
		case Male:
			return "M";

		case Female:
			return "F";

		default:
			throw new IllegalArgumentException("Invalid gender: " + gender);
		}
	}

	@Override
	public Gender convertToEntityAttribute(String gender) {
		switch (gender) {
		case "M":
			return Gender.Male;

		case "F":
			return Gender.Female;

		default:
			throw new IllegalArgumentException("Invalid gender code: " + gender);
		}
	}
}

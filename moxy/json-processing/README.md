# EclipseLink MOXy JSON and JSON Processing (JSR 353) Example

This example will demonstrate how to use the Json Processing API (JSR 353) using EclipseLink. The example consists of:

- Use the JSR 353 JSON Writer API to write the customer from a file in JSON format
- Use the JSR 353 JSON Reader API to read the JSON representation of a customer from a file
- Use the JSR 353 JSON Generator API to write a Customer to a file in JSON format
- Use the JSR 353 JSON Parser API to read the JSON representation of a customer from a file
- Use MOXy API to write a customer to a file in JSON format
- Use MOXy API to read the JSON the JSON representation of a customer from a file

 Simply run 'mvn'. The default goal is 'install'

Three files will be created:
- Created by JsonWriter:    target/customer.generator.json
- Created by JsonGenerator: target/customer.writer.json
- Created by MOxy:          target/customer.moxy.json

# EclipseLink MOXy JSON Processing (JSR 353) Example

This example will demonstrate how to use the Json Processing API (JSR 353) using EclipseLink. The example consists of:

- Use the JSON Writer API to write the customer from a file in JSON format
- Use the JSON Reader API to read the JSON representation of a customer from a file
- Use the JSON Generator API to write a Customer to a file in JSON format
- Use the JSON Parser API to read the JSON representation of a customer from a file

 Simply run 'mvn'. The default goal is 'install'

Two files will be created:
- Created by JsonWriter:    target/customer.generator.json
- Created by JsonGenerator: target/customer.writer.json

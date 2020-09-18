Implement a Spring Boot REST API which allows sending an email to one or more recipients. 
Email delivery should be done via sendgrid.com. You can signup for a free trial account for your testing here. 
You can generate a starter Spring Boot application from start.spring.io. Please implement service in Java 8 and use whichever build tool (Gradle / Maven) you feel most comfortable with. 

# Email API input:

- To - Required
- CC / BCC - Optional
- Body - Text/HTML
- Subject

# Output: Whatever you think is appropriate for this REST API

# Additional features

Implement API tests and appropriate test coverage for this API.
- Add a config toggle which if enabled will filter out and log emails targeted to non bitsflux.com email domains.
- Add an optional boolean QueryParam enrich=true to enrich the email messages by appending a random quote of the day, the current Weather in Carlsbad (the US city) or any other piece of data which you can pull from an external REST API as they pass through this service.
- Please consider that you are shipping this service into prod and feel free to add any additional functionality that you think is neccessary but please do not spend any longer then a couple of hours on this task. 

If you like please include a prioritised list of improvements you would make in a following sprint. The goal here is to understand how you approach designing and implementing REST APIs and get a feel for your coding & problem solving skills not to provide completely polished code. Please feel free to submit a partial solution if neccessary or reach out for clarification on anything.

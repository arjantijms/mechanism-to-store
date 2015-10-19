# mechanism-to-store

Working application that demonstrates how an auth module can delegate to a JSR 375 identity store

# How to run

Deploy to a full Java EE server and request:

http://localhost:8080/mechanism-to-store-app/servlet

This will show the details for the anonymous (not logged-in) user.

Then request:

http://localhost:8080/mechanism-to-store-app/servlet?name=reza&password=secret1

To see the caller being authenticated.

# Tested servers

This has been tested on GlassFish 4.1.1. The war that this maven project builds can be directed deployed to a
stock GlassFish server. No configuration of any kind is necessary.






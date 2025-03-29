# Backend setup:

1. Install and run [Docker](https://docs.docker.com/get-docker/).
1. In root directory run `docker-compose up -d` to set up database.
1. Make sure you have installed and configured JDK 21 (`corretto-21`)
1. Build and run the backend server.

# SSL setup for https

1. Generate keystore using this command `keytool -genkeypair -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650`. It will prompt you some questions.
1. Default password in application.properties is set to 123456. If you want to use other password, make sure you create `KEYSTORE_PASSWORD` environment variable with the same value.
1. Use default values for the rest of the command prompts.
1. Move the generated `keystore.p12 ` to `backend/src/main/resources`.

# Fronted setup:

1. Install Node v23.6.0
1. Move to `fronted` directory and run `npm install`
1. Build app with `npm run build` (production)
1. Start app with `npm run dev` (development)
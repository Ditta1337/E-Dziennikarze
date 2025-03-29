# Backend setup:

1. Install and run [Docker](https://docs.docker.com/get-docker/).
1. In root directory run `docker-compose up -d` to set up database.
1. Make sure you have installed and configured JDK 21 (`corretto-21`)
1. Build and run the backend server.

# Fronted setup:

1. Install Node v23.6.0
1. Move to `fronted` directory and run `npm install`
1. Build app with `npm run build` (production)
1. Start app with `npm run dev` (development)
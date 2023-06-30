import Keycloak from "keycloak-js";
const keycloak = new Keycloak({
 url: "http://localhost:8180/auth",
 realm: "freshfood-web",
 clientId: "freshfood",
});

export default keycloak;
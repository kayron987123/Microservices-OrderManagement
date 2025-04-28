CREATE TABLE customers
(
    id_customer   SERIAL PRIMARY KEY,
    uuid_customer UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    name          VARCHAR(100)                          NOT NULL,
    last_name     VARCHAR(100)                          NOT NULL,
    password      VARCHAR(100)                          NOT NULL,
    email         VARCHAR(100) UNIQUE                   NOT NULL,
    phone         VARCHAR(20)                           NOT NULL
);

CREATE TABLE roles
(
    id_role   SERIAL PRIMARY KEY,
    uuid_role UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    name      VARCHAR(30) UNIQUE                    NOT NULL
);

CREATE TABLE permissions
(
    id_permission   SERIAL PRIMARY KEY,
    uuid_permission UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    name            VARCHAR(30) UNIQUE                    NOT NULL
);

CREATE TABLE customers_roles
(
    id_customer INT REFERENCES customers (id_customer) ON DELETE CASCADE,
    id_role     INT REFERENCES roles (id_role) ON DELETE CASCADE,
    PRIMARY KEY (id_customer, id_role)
);

CREATE TABLE roles_permissions
(
    id_role       INT REFERENCES roles (id_role) ON DELETE CASCADE,
    id_permission INT REFERENCES permissions (id_permission) ON DELETE CASCADE,
    PRIMARY KEY (id_role, id_permission)
);

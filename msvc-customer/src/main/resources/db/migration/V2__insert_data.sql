-- Insertar roles
INSERT INTO roles (uuid_role, name) VALUES (gen_random_uuid(), 'ROLE_ADMIN');
INSERT INTO roles (uuid_role, name) VALUES (gen_random_uuid(), 'ROLE_USER');
INSERT INTO roles (uuid_role, name) VALUES (gen_random_uuid(), 'ROLE_MODERATOR');
INSERT INTO roles (uuid_role, name) VALUES (gen_random_uuid(), 'ROLE_GUEST');

-- Insertar permisos
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'CREATE_PRODUCTS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'READ_PRODUCTS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'UPDATE_PRODUCTS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'DELETE_PRODUCTS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'CREATE_PERMISSIONS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'READ_PERMISSIONS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'UPDATE_PERMISSIONS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'DELETE_PERMISSIONS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'CREATE_ROLES');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'READ_ROLES');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'UPDATE_ROLES');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'DELETE_ROLES');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'CREATE_CUSTOMERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'READ_CUSTOMERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'UPDATE_CUSTOMERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'DELETE_CUSTOMERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'CREATE_ORDERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'READ_ORDERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'UPDATE_ORDERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'DELETE_ORDERS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'CREATE_ORDER_DETAILS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'READ_ORDER_DETAILS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'UPDATE_ORDER_DETAILS');
INSERT INTO permissions (uuid_permission, name) VALUES (gen_random_uuid(), 'DELETE_ORDER_DETAILS');

--Insertar relaciones entre roles y permisos
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 1);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 2);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 3);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 4);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 5);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 6);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 7);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 8);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 9);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 10);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 11);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 12);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 13);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 14);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 15);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 16);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 17);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 18);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 19);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 20);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 21);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 22);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 23);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (1, 24);

INSERT INTO roles_permissions (id_role, id_permission) VALUES (2, 2);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (2, 17);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (2, 18);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (2, 19);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (2, 20);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (2, 22);

INSERT INTO roles_permissions (id_role, id_permission) VALUES (3, 2);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (3, 3);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (3, 18);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (3, 19);

INSERT INTO roles_permissions (id_role, id_permission) VALUES (4, 2);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (4, 18);
INSERT INTO roles_permissions (id_role, id_permission) VALUES (4, 20);
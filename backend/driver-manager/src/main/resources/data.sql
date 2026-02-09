-- Inserir Motoristas
INSERT INTO drivers (id, name, email, phone_number, cpf, cnh, city, state, available) VALUES 
('d290f1ee-6c54-4b01-90e6-d701748f0851', 'João da Silva', 'joao.silva@email.com', '11987654321', '83120155059', '12345678901', 'São Paulo', 'SP', true);

INSERT INTO drivers (id, name, email, phone_number, cpf, cnh, city, state, available) VALUES 
('7b38d72f-9811-4712-9844-031024317765', 'Maria Oliveira', 'maria.oliveira@email.com', '21987654321', '51139164010', '10987654321', 'Rio de Janeiro', 'RJ', true);

INSERT INTO drivers (id, name, email, phone_number, cpf, cnh, city, state, available) VALUES 
('a123b456-7890-1234-5678-90abcdef1234', 'Carlos Santos', 'carlos.santos@email.com', '31987654321', '58744036070', '11223344556', 'Belo Horizonte', 'MG', false);

INSERT INTO drivers (id, name, email, phone_number, cpf, cnh, city, state, available) VALUES 
('b234c567-8901-2345-6789-01abcdef2345', 'Ana Souza', 'ana.souza@email.com', '41987654321', '83592802022', '99887766554', 'Curitiba', 'PR', true);

-- Inserir Tipos de Veículos
INSERT INTO driver_vehicle_types (driver_id, vehicle_type) VALUES ('d290f1ee-6c54-4b01-90e6-d701748f0851', 'TRUCK');

INSERT INTO driver_vehicle_types (driver_id, vehicle_type) VALUES ('7b38d72f-9811-4712-9844-031024317765', 'CAR');
INSERT INTO driver_vehicle_types (driver_id, vehicle_type) VALUES ('7b38d72f-9811-4712-9844-031024317765', 'MOTORCYCLE');

INSERT INTO driver_vehicle_types (driver_id, vehicle_type) VALUES ('a123b456-7890-1234-5678-90abcdef1234', 'TRUCK');

INSERT INTO driver_vehicle_types (driver_id, vehicle_type) VALUES ('b234c567-8901-2345-6789-01abcdef2345', 'CAR');

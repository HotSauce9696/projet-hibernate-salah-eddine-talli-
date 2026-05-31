CREATE DATABASE IF NOT EXISTS gestion_produits;
USE gestion_produits;

CREATE TABLE IF NOT EXISTS produit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    quantite_en_stock INT NOT NULL DEFAULT 0,
    disponibilite BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO produit (code, libelle, type, quantite_en_stock, disponibilite) VALUES
('P001', 'Pommes Golden', 'Fruits', 150, TRUE),
('P002', 'Bananes', 'Fruits', 200, TRUE),
('P003', 'Carottes', 'Légumes', 100, TRUE),
('P004', 'Lait', 'Produits laitiers', 50, TRUE),
('P005', 'Pain complet', 'Boulangerie', 30, FALSE);
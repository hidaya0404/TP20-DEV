<?php

class Database
{
    private string $host = "localhost";
    private string $port = "3307";
    private string $db_name = "numberbook";
    private string $username = "root";
    private string $password = "root";

    public function getConnection(): ?PDO
    {
        try {
            $dsn = "mysql:host=" . $this->host .
                   ";port=" . $this->port .
                   ";dbname=" . $this->db_name .
                   ";charset=utf8mb4";

            $conn = new PDO($dsn, $this->username, $this->password);

            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $conn->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

            return $conn;

        } catch (PDOException $e) {
            echo json_encode([
                "success" => false,
                "message" => "Erreur de connexion à la base de données",
                "error" => $e->getMessage()
            ]);

            return null;
        }
    }
}
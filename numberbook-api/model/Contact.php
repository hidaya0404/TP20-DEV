<?php

class Contact
{
    private PDO $conn;
    private string $table = "contacts";

    public ?int $id;
    public string $name;
    public string $phone;
    public string $source;

    public function __construct(PDO $db)
    {
        $this->conn = $db;
    }

    public function insert(): bool
    {
        $sql = "INSERT INTO " . $this->table . " 
                (name, phone, source) 
                VALUES 
                (:name, :phone, :source)";

        $stmt = $this->conn->prepare($sql);

        $this->name = htmlspecialchars(strip_tags($this->name));
        $this->phone = htmlspecialchars(strip_tags($this->phone));
        $this->source = htmlspecialchars(strip_tags($this->source));

        $stmt->bindParam(":name", $this->name);
        $stmt->bindParam(":phone", $this->phone);
        $stmt->bindParam(":source", $this->source);

        return $stmt->execute();
    }

    public function getAll(): array
    {
        $sql = "SELECT id, name, phone, source, created_at 
                FROM " . $this->table . " 
                ORDER BY created_at DESC";

        $stmt = $this->conn->prepare($sql);
        $stmt->execute();

        return $stmt->fetchAll();
    }

    public function search(string $keyword): array
    {
        $sql = "SELECT id, name, phone, source, created_at 
                FROM " . $this->table . " 
                WHERE name LIKE :keyword 
                   OR phone LIKE :keyword 
                ORDER BY created_at DESC";

        $stmt = $this->conn->prepare($sql);

        $keyword = htmlspecialchars(strip_tags($keyword));
        $keyword = "%" . $keyword . "%";

        $stmt->bindParam(":keyword", $keyword);
        $stmt->execute();

        return $stmt->fetchAll();
    }
}
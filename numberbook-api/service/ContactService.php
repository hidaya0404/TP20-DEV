<?php

require_once __DIR__ . '/../config/Database.php';
require_once __DIR__ . '/../model/Contact.php';

class ContactService
{
    private ?PDO $conn;
    private Contact $contact;

    public function __construct()
    {
        $database = new Database();
        $this->conn = $database->getConnection();

        if ($this->conn !== null) {
            $this->contact = new Contact($this->conn);
        }
    }

    public function addContact(array $data): array
    {
        if ($this->conn === null) {
            return [
                "success" => false,
                "message" => "Connexion à la base de données impossible"
            ];
        }

        if (
            empty($data["name"]) ||
            empty($data["phone"])
        ) {
            return [
                "success" => false,
                "message" => "Le nom et le numéro de téléphone sont obligatoires"
            ];
        }

        $this->contact->name = $data["name"];
        $this->contact->phone = $data["phone"];
        $this->contact->source = $data["source"] ?? "android";

        if ($this->contact->insert()) {
            return [
                "success" => true,
                "message" => "Contact ajouté avec succès"
            ];
        }

        return [
            "success" => false,
            "message" => "Erreur lors de l'ajout du contact"
        ];
    }

    public function addContacts(array $contacts): array
    {
        if ($this->conn === null) {
            return [
                "success" => false,
                "message" => "Connexion à la base de données impossible"
            ];
        }

        $inserted = 0;
        $failed = 0;

        foreach ($contacts as $item) {
            if (
                empty($item["name"]) ||
                empty($item["phone"])
            ) {
                $failed++;
                continue;
            }

            $this->contact->name = $item["name"];
            $this->contact->phone = $item["phone"];
            $this->contact->source = $item["source"] ?? "android";

            if ($this->contact->insert()) {
                $inserted++;
            } else {
                $failed++;
            }
        }

        return [
            "success" => true,
            "message" => "Synchronisation terminée",
            "inserted" => $inserted,
            "failed" => $failed
        ];
    }

    public function getAllContacts(): array
    {
        if ($this->conn === null) {
            return [
                "success" => false,
                "message" => "Connexion à la base de données impossible"
            ];
        }

        $contacts = $this->contact->getAll();

        return [
            "success" => true,
            "contacts" => $contacts
        ];
    }

    public function searchContacts(string $keyword): array
    {
        if ($this->conn === null) {
            return [
                "success" => false,
                "message" => "Connexion à la base de données impossible"
            ];
        }

        if (empty($keyword)) {
            return [
                "success" => false,
                "message" => "Veuillez saisir un nom ou un numéro"
            ];
        }

        $contacts = $this->contact->search($keyword);

        return [
            "success" => true,
            "contacts" => $contacts
        ];
    }
}